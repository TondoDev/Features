package org.tondo.advent2016.run.day15;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.tondo.advent2016.day15.BallMachine;
import org.tondo.advent2016.day15.Disc;
import org.tondo.advent2016.day15.MachineParser;

/**
 * 
 * @author TondoDev
 *
 */
public class Day15Test {

	@Test
	public void testPart1() throws UnsupportedEncodingException, IOException {
		InputStream input = getClass().getResourceAsStream("/day15/day15Part1.txt");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
			MachineParser parser = new MachineParser();
			BallMachine machine = parser.parse(reader);
			System.out.println("Day 15 - part1: initial time is:" +  machine.calculateInitialTime()); // 16824
		}
	}
	
	@Test
	public void testPart2() throws UnsupportedEncodingException, IOException {
		InputStream input = getClass().getResourceAsStream("/day15/day15Part1.txt");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
			MachineParser parser = new MachineParser();
			BallMachine machine = parser.parse(reader);
			machine.appendDisc(new Disc(11, 0, 7));
			System.out.println("Day 15 - part2: initial time is:" +  machine.calculateInitialTime()); // 3543984
		}
	}

	@Test
	public void testTmpInput() throws UnsupportedEncodingException, IOException {
		InputStream input = getClass().getResourceAsStream("/day15/tmp.txt");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
			MachineParser parser = new MachineParser();
			BallMachine machine = parser.parse(reader);
			System.out.println("Day 15 - part1: tmp:" +  machine.calculateInitialTime());
		}
	}
}
