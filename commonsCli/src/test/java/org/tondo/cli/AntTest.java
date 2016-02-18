package org.tondo.cli;

import static org.junit.Assert.*;

import org.apache.commons.cli.CommandLine;
import org.junit.Test;

/**
 * Testing of command line args similar to Ant application
 * @author TondoDev
 *
 */
public class AntTest {

	@Test
	public void testBooleanOptions() {
		AntApp app = new AntApp();
		CommandLine result = app.parse(new String[] {"help", "-version", "-quiet"});
		assertFalse("Because not dash present", result.hasOption("help"));
		assertTrue(result.hasOption("version"));
		assertTrue(result.hasOption("quiet"));
		
		try {
			app.parse(new String[] {"--quiet"});
			fail("IllegalStateException expctedt because -- are used only with longOpt, but quiet is specified only as opt");
		} catch (IllegalStateException e) {}
	}
}
