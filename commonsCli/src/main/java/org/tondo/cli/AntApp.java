package org.tondo.cli;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Implementations of example provided at Commons CLI Getting started page.
 * Example simulates ant (java building tool) command line options.
 *  
 * @author TondoDev
 *
 */
public class AntApp {
	
	private Options options;
	private CommandLineParser parser;
	private HelpFormatter formatter;
	
	public AntApp() {
		this.parser = new DefaultParser();
		this.formatter = new HelpFormatter();
		this.options = initCmdOptions();
	}
	
	public CommandLine parse(String[] args) {
		CommandLine cmdArgsResult = null;
		try {
			cmdArgsResult = parser.parse(options, args);
		} catch (ParseException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		
		
		return cmdArgsResult;
	}
	
	public void printHelp() {
		printHelp(null);
	}
	
	public void printHelp(String leadingMsg) {
		if (leadingMsg != null) {
			System.out.println(leadingMsg);
		}
		
		formatter.printHelp(
				"AntApp", // print usage: <this.parameter>
				// some text before arguemtn list
				"Emulation of some Ant command line arguments", 
				options, 
				// text after argument list
				"Created by TondoDev while learning Apache Commons CLI library");
	}

	public static void main(String[] args) {
		args = new String[] {"-debug"};
		System.out.println(Arrays.toString(args));
		
		AntApp antApp = new AntApp();
		CommandLine cmdArgsResult = null;
		try {
			cmdArgsResult = antApp.parse(args);
		} catch (IllegalStateException e) {
			antApp.printHelp("Parsing error: " + e.getMessage());
			return;
		}
		if (cmdArgsResult.getOptions().length == 0 // nothing was matched
				|| cmdArgsResult.hasOption("help")) {
			
			antApp.printHelp();
		}
	}
	
	
	private Options initCmdOptions() {
		Options options = new Options();
		
		// boolean arguments, only for querying if they are provided or not
		options.addOption("help", 			"print this message" );
		options.addOption("projecthelp",	"print project help information" );
		options.addOption("version", 		"print the version information and exit" );
		options.addOption("quiet", 			"be extra quiet" );
		options.addOption("verbose", 		"be extra verbose" );
		options.addOption("debug", 			"print debugging information" );
		options.addOption("emacs", 			"produce logging information without adornments" );
		
		
		Option logfile = Option.builder("lf")
				.hasArg()
				// still don't understand what is this for
				// is it  only for displaying in help?
				.argName("file") 
				.desc("use given file for log" )
				.build();
		
		Option logger = Option.builder("class")
				.argName("classname")
				.hasArg()
				.desc( "the class which it to perform "
                                  + "logging" )
                .build();
		Option listener  = Option.builder("listener")
				.argName("classname")
				.hasArg()
				.desc( "add an instance of class as "
                                  + "a project listener" )
                .build(); 

		Option buildfile = Option.builder( "bf" )
				.argName("file")
                .hasArg()
                .desc(  "use given buildfile" )
                .build();

		Option find  = Option.builder( "find" )
				.argName("file")
                .hasArg()
                .desc( "search for buildfile towards the "
                                  + "root of the filesystem and use it" )
                .build();
		
		options.addOption(logfile);
		options.addOption(logger);
		options.addOption(listener);
		options.addOption(buildfile);
		options.addOption(find);
		
		// java property stype
		Option javaProp = Option.builder("D")
					.numberOfArgs(2)
					.argName("property=value")
					.valueSeparator()
					.desc("use value for given property")
					.build();
		options.addOption(javaProp);
		
		return options;
	}
}

