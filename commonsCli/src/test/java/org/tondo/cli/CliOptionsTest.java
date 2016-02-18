package org.tondo.cli;

import static org.junit.Assert.*;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.junit.Test;

/**
 * 
 * @author TondoDev
 *
 */
public class CliOptionsTest {

	@Test
	public void testOptionCreation() {
		Options options = new Options();
		
		options.addOption("t", false, "Display current time");
		assertEquals("Options size after adding one element", 1, options.getOptions().size());
		
		Option opt = new Option("list", "list current directory");
		options.addOption(opt);
		assertEquals("Options size after adding second element", 2, options.getOptions().size());
		
		Option relative = Option.builder()
			.argName("r")
			.optionalArg(false)
			.desc("specifies relative path")
			.longOpt("relative")
			.hasArg(true)
			.build();
		options.addOption(relative);
		assertEquals("Options size after adding third element", 3, options.getOptions().size());
	}
	
	@Test
	public void testParsingCmdLine() throws ParseException {
		// only non-deprecated implementation
		CommandLineParser parser = new DefaultParser();
		String[] args = new String[] {"progName" ,"-b", "/home/TondoDev"};
		
		Option baseOpt = Option.builder("b") // short name should go here
				.desc("base path to something")
				// specifies if options should have argument, whithout this, following cmd string 
				// without -- or - prefix will be treated as program argument, not options argument
				.hasArg(true)
				// relevant with .hasArg(true), if set to true, option argument can be omitted
				// without crashing on parse exception
				.optionalArg(true)
//				.argName("b") // this is not name of options but its value???
				.longOpt("base")
				.build();
		Options options = new Options().addOption(baseOpt);
		
		CommandLine cmdLine = parser.parse(options, args);
		assertTrue("Found base by short", cmdLine.hasOption("b"));
		assertTrue("Found base by long", cmdLine.hasOption("base"));
		
		// values
		assertEquals("Value by short", "/home/TondoDev", cmdLine.getOptionValue("b"));
		assertEquals("Value by long", "/home/TondoDev", cmdLine.getOptionValue("base"));
		assertNull("Value by long and double dash prefix is not recognized", cmdLine.getOptionValue("--base"));
	}
	
	
	@Test
	public void testJavaStyleProperties() throws ParseException {
		Option dopt = Option.builder("D")
				// here be carefull, also exists hasArg() method for single argument
				// and for that -Dkey=value properties doesn't work 
				.hasArgs()
				.build();
		Options options = new Options();
		options.addOption(dopt);
		
		CommandLineParser parser = new DefaultParser();
		
		CommandLine result = parser.parse(options, new String[] {"-Dopica"});
		assertEquals("Recognied value without any delimeter from option", "opica", result.getOptionValue("D"));
		
		// multiple options present
		result = parser.parse(options, new String[] {"-Dopica", "-Dskorica"});
		assertEquals("Default first vaule is returned", "opica", result.getOptionValue("D"));
		String[] values = result.getOptionValues("D");
		assertEquals(2, values.length);
		assertEquals("First value of same option", "opica", values[0]);
		assertEquals("Second value of same option", "skorica", values[1]);
		
		// multiple options present but separated from -D by space
		// same behavior as with united option and value
		result = parser.parse(options, new String[] { "-D", "opica", "-D", "skorica" });
		assertEquals("Default first vaule is returned", "opica", result.getOptionValue("D"));
		values = result.getOptionValues("D");
		assertEquals(2, values.length);
		assertEquals("First value of same option", "opica", values[0]);
		assertEquals("Second value of same option", "skorica", values[1]);
		
		// now argument is required
		try {
			parser.parse(options, new String[] {"-D"});
			fail("MissingArgumentException expected");
		} catch (MissingArgumentException e) {}
		
		// = is considered as default value separator?
		result = parser.parse(options, new String[] {"-Dopica=skorica"});
		assertEquals("Default first vaule is returned", "opica", result.getOptionValue("D"));
		String[] separated = result.getOptionValues("D");
		assertEquals("First value of same option", "opica", separated[0]);
		assertEquals("Second value of same option", "skorica", separated[1]);
		
		// we can get result also as properties
		Properties props = result.getOptionProperties("D");
		assertNotNull(props);
		assertEquals("Number of keys", 1, props.keySet().size());
		assertEquals("Value access by key", "skorica", props.getProperty("opica"));
		
		
		// separated pair by space with single D option is not considered as more values
		result = parser.parse(options, new String[] {"-Dopica=skorica", "praca=slachti"});
		assertEquals("Default first vaule is returned", "opica", result.getOptionValue("D"));
		separated = result.getOptionValues("D");
		assertEquals(2, values.length);
		assertEquals("First value of same option", "opica", separated[0]);
		assertEquals("Second value of same option", "skorica", separated[1]);
	}
	
	@Test
	public void testAssignmentWithSeparator() throws ParseException {
		Option dopt = Option.builder("D")
				.valueSeparator('=')
				.hasArgs()
				.build();
		Options options = new Options();
		options.addOption(dopt);
		CommandLineParser parser = new DefaultParser();
		
		// in separed from -D switch
		CommandLine result = parser.parse(options, new String[] {"-D", "opica=macka"});
		String[] separated = result.getOptionValues("D");
		assertEquals(2, separated.length);
		assertEquals("opica", separated[0]);
		assertEquals("macka", separated[1]);
	}
	
	
	@Test
	public void testAssignmentWithoutSeparator() throws ParseException {
		Option dopt = Option.builder("D")
				.hasArgs()
				.build();
		Options options = new Options();
		options.addOption(dopt);
		CommandLineParser parser = new DefaultParser();
		
		// in separed from -D switch
		CommandLine result = parser.parse(options, new String[] {"-D", "opica=macka"});
		String[] separated = result.getOptionValues("D");
		assertEquals(1, separated.length);
		assertEquals("opica=macka", separated[0]);
	}
	
	@Test
	public void testValueSeparator() throws ParseException {
		// not provided arguments or separator configuration
		Option dopt = Option.builder("D")
				.valueSeparator('-')
				.hasArgs()
				.build();
		Options options = new Options();
		options.addOption(dopt);

		CommandLineParser parser = new DefaultParser();
		CommandLine result = parser.parse(options, new String[] {"-D", "opica-macka-pes"});
		assertEquals("Default first vaule is returned", "opica", result.getOptionValue("D"));
		String[] separated = result.getOptionValues("D");
		assertEquals(3, separated.length);
		assertEquals("opica", separated[0]);
		assertEquals("macka", separated[1]);
		assertEquals("pes", separated[2]);
	}
	
	@Test
	public void testJavaStylePropertiesNotCorrect() throws ParseException {
		// not provided arguments or separator configuration
		Option dopt = Option.builder("D")
				.build();
		Options options = new Options();
		options.addOption(dopt);
		
		CommandLineParser parser = new DefaultParser();
		
		// basic configuration is not enought
		try {
			parser.parse(options, new String[] {"-Dopica"});
			fail("UnrecognizedOptionException expected!");
		} catch (UnrecognizedOptionException e) {}
		
		try {
			parser.parse(options, new String[] {"-Dopica=skorica"});
			fail("UnrecognizedOptionException expected!");
		} catch (UnrecognizedOptionException e) {}
	}
	
	@Test
	public void testGitLikeAssignmet() throws ParseException {
		Option dopt = Option.builder()
				.longOpt("author")
				.hasArg()
				.build();
		Options options = new Options();
		options.addOption(dopt);
		
		CommandLineParser parser = new DefaultParser();
		CommandLine result = parser.parse(options, new String[] {"--author=popey"});
		assertEquals("popey", result.getOptionValue("author"));
	}
}
