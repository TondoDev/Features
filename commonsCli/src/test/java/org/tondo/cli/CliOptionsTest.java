package org.tondo.cli;

import static org.junit.Assert.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
				// relevant with .hasArg(true), if set to true, otion argument can be ommitted
				// withoud crashing on parse exception
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
}
