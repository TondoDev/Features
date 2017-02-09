package org.tondo.advent2016.run.day11;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.tondo.advent2016.day11.ConfigurationReader;
import org.tondo.advent2016.day11.StateSpace;

/**
 * 
 * @author TondoDev
 *
 */
public class Day11Test {

	@Test
	public void testPart1() throws IOException {
		InputStream input = this.getClass().getResourceAsStream("/day11/tmp.txt");
		try (BufferedReader reader = new BufferedReader( new InputStreamReader(input, Charset.forName("UTF-8")))) {
			ConfigurationReader conf = new ConfigurationReader();
			conf.readConfiguration(reader);
			StateSpace stateSpace = new StateSpace(conf.getInitialState());
			int steps = stateSpace.findMinimalSteps(conf.getEndState());
			System.out.println("Day 11 Part 1: Minimal steps: " + steps);
		}
	}
	
	private static final Pattern FLOOR_PARSER = Pattern.compile("^The ([a-z]+) floor contains (.*)$");
	private static final Pattern DEVICE_PARSER = Pattern.compile("([a-z]+)(?:-compatible)? (microchip|generator)");
	
	@Test
	public void testParser() {
		String line =// "The third floor contains a cobalt-compatible microchip, a curium-compatible microchip, a ruthenium-compatible microchip, and a plutonium-compatible microchip.";
				"The second floor contains a cobalt generator, a curium generator, a ruthenium-compatible microchip, a ruthenium generator, and a plutonium generator.";
		
		Matcher floorMatch = FLOOR_PARSER.matcher(line);
		assertTrue(floorMatch.find());
		//assertEquals("second", floorMatch.group(1));
		
		Matcher deviceMatch = DEVICE_PARSER.matcher(floorMatch.group());
		while (deviceMatch.find()) {
			System.out.println(deviceMatch.group(1) + " " + deviceMatch.group(2));
		}
	}
}
