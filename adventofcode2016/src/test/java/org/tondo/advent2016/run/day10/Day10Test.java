package org.tondo.advent2016.run.day10;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.tondo.advent2016.day10.BotSwarm;

import static org.junit.Assert.*;

/**
 * 
 * @author TondoDev
 *
 */
public class Day10Test {

	private static final Pattern INPUT_VAL = Pattern.compile("^value ([0-9]+) goes to bot ([0-9]+)$");
	private static final Pattern CHANGE = Pattern.compile("^bot ([0-9]+) gives low to (bot|output) ([0-9]+) and high to (bot|output) ([0-9]+)$");
	
	@Test
	public void testPart1() throws UnsupportedEncodingException, IOException {
		InputStream input = getClass().getResourceAsStream("/day10/day10Part1.txt");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
			BotSwarm swarm = new BotSwarm();
			String line = null;
			while ((line = reader.readLine()) != null) {
				swarm.processInstruction(line);
			}
			
			swarm.printInfo();
			System.out.println("Day 10 Part 1 - 17 and 61 comparing botId is: " + swarm.getTerminatingBotId());
			System.out.println("Day 10 Part 2 - Multiplication of 0,1,2 output bins: " + swarm.getMultipliedOutput(2));
		}
	}
	
	@Test
	public void testInstructionParsing() {
		Matcher inputMatcher = INPUT_VAL.matcher("value 67 goes to bot 187");
		assertTrue(inputMatcher.find());
		assertEquals("value", "67", inputMatcher.group(1));
		assertEquals("botId", "187", inputMatcher.group(2));
		
		Matcher changeMatcher = CHANGE.matcher("bot 150 gives low to output 11 and high to bot 53");
		assertTrue(changeMatcher.find());
		assertEquals("src bot", "150", changeMatcher.group(1));
		assertEquals("low type", "output", changeMatcher.group(2));
		assertEquals("low id", "11", changeMatcher.group(3));
		assertEquals("high type", "bot", changeMatcher.group(4));
		assertEquals("high id", "53", changeMatcher.group(5));
	}
}
