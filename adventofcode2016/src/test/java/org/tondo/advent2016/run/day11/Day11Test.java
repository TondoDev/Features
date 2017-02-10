package org.tondo.advent2016.run.day11;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.tondo.advent2016.day11.ConfigurationReader;
import org.tondo.advent2016.day11.FloorState;
import org.tondo.advent2016.day11.StateSpace;
import org.tondo.advent2016.day11.StateSpace.InternalState;

/**
 * 
 * @author TondoDev
 *
 */
public class Day11Test {

	@Test
	public void testPart1() throws IOException {
		InputStream input = this.getClass().getResourceAsStream("/day11/day11Part1.txt");
		try (BufferedReader reader = new BufferedReader( new InputStreamReader(input, Charset.forName("UTF-8")))) {
			ConfigurationReader conf = new ConfigurationReader();
			conf.readConfiguration(reader);
			StateSpace stateSpace = new StateSpace(conf.getInitialState());
			int steps = stateSpace.findMinimalSteps(conf.getEndState());
			System.out.println("Day 11 Part 1: Minimal steps: " + steps);
			stateSpace.printMinaml();
		}
	}
	
	// cur: {1=[2m], 2=[1g, 1m], 3=[2g], 4=[]}|| e = 2 {1=[2m], 2=[1m], 3=[2g, 1g], 4=[]}|| e = 3
	@Test
	public void testGenStates() {
		Map<Integer, List<String>> floors = new HashMap<>();
		floors.put(1, Collections.<String>emptyList());
		floors.put(2, Collections.<String>emptyList());
		floors.put(3, Arrays.asList("2m", "2g", "1m", "1g"));
		floors.put(4, Collections.<String>emptyList());
		
		FloorState fs = new FloorState(floors, 3);
		InternalState internal = new InternalState(fs, null, 0);
		
		StateSpace stateSpace = new StateSpace(fs);
		stateSpace.generateNextStates(internal);
		
		//assertTrue(stateSpace.isValidFloorConfiguration(Arrays.asList("1g", "1m", "2g")));
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
