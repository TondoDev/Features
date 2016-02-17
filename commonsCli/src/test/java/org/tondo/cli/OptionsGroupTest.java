package org.tondo.cli;

import static org.junit.Assert.*;

import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Test;

/**
 * OptionGroup is list of mutually exclusive params
 * @author TondoDev
 *
 */
public class OptionsGroupTest {
	
	@Test
	public void testSimpleOptionGroup() throws ParseException {
		Option download = new Option("down", "Donwload");
		Option upload = new Option("up", "Upload");
		Option ping = new Option("ping", "Test of connection");

		// option grouop is constructed by simple options
		OptionGroup group = new OptionGroup();
		group.addOption(download);
		group.addOption(upload);
		group.addOption(ping);
		
		Options options = new Options();
		options.addOptionGroup(group);
		options.addOption("h", "print help");
		
		CommandLineParser parser = new DefaultParser();
		
		// empty list
		CommandLine result = parser.parse(options, new String[] {}); 
		assertEquals("Nothing matched", 0, result.getOptions().length);
		
		result = parser.parse(options, new String[] {"-ping"}); 
		assertEquals("One matched", 1, result.getOptions().length);
		assertTrue("One argument is present", result.hasOption("ping"));
		// we can't examine selected group value if we present OtionsGroup object passed to Options instance
		assertEquals("Selected  group value", "ping", group.getSelected());
		
		// selecting more options from group is error
		try {
			result = parser.parse(options, new String[] {"-ping", "-down"});
			fail("AlreadySelectedException expecetd");
		} catch (AlreadySelectedException e) {}
	}
	
	@Test
	public void testRequiredGroup() throws ParseException {
		Option download = new Option("down", "Donwload");
		Option upload = new Option("up", "Upload");
		Option ping = new Option("ping", "Test of connection");

		// option grouop is constructed by simple options
		OptionGroup group = new OptionGroup();
		group.addOption(download);
		group.addOption(upload);
		group.addOption(ping);
		group.setRequired(true);
		
		Options options = new Options();
		options.addOptionGroup(group);
		options.addOption("h", "print help");
		
		CommandLineParser parser = new DefaultParser();
		
		// missing any argument from required option group
		try {
			parser.parse(options, new String[] {"-h"}); 
			fail("MissingOptionException expected!");
		} catch (MissingOptionException e) {}
		
		CommandLine result = parser.parse(options, new String[] {"-up"});
		assertTrue("One argument is present", result.hasOption("up"));
		assertEquals("Selected group value", "up", group.getSelected());
	}
	
	@Test
	public void testGroupWithValue() throws ParseException {
		Option download = new Option("down", "Donwload");
		Option upload = Option.builder("up")
					.hasArg(true)
					.build();
		Option ping = new Option("ping", "Test of connection");

		// option grouop is constructed by simple options
		OptionGroup group = new OptionGroup();
		group.addOption(download);
		group.addOption(upload);
		group.addOption(ping);
		group.setRequired(true);
		
		Options options = new Options();
		options.addOptionGroup(group);
		
		CommandLineParser parser = new DefaultParser();
		
		// group opt without req value
		CommandLine result = parser.parse(options, new String[] {"-down"});
		assertTrue("Selected group option without value",  result.hasOption("down"));
		
		// group option with required vaule
		result = parser.parse(options, new String[] {"-up", "star"});
		assertTrue("Selected group option without value",  result.hasOption("up"));
		assertEquals("Group option has value", "star", result.getOptionValue("up"));
		
		// group option with without required argument 
		try {
			result = parser.parse(options, new String[] {"-up"});
			fail("MissingArgumentException expected!");
		} catch (MissingArgumentException e) {}
	}
	
	
}

