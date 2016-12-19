package org.tondo.advent2016.run.day1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.junit.Test;
import org.tondo.advent2016.day1.Navigator;

/**
 * Taxicab geomertry
 * @author TondoDev
 *
 */
public class Day1Test {

	@Test
	public void testPart1() throws IOException {
		InputStream input = getClass().getResourceAsStream("/day1/day1Part1.txt");
		
		Navigator nav = new Navigator();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")))) {
			int character = 0;
			StringBuilder buffer =  new StringBuilder();
			while ((character = reader.read()) != -1) {
				if (character == ',') {
					nav.processStep(Navigator.parse(buffer.toString().trim()));
					buffer = new StringBuilder();
				} else {
					buffer.append((char)character);
				}
			}
			
			if (buffer != null && buffer.length() > 0) {
				nav.processStep(Navigator.parse(buffer.toString().trim()));
			}
		}
		
		System.out.println("Day 1 - Part 1: Distance: " + nav.getDistance()); // 230
	}
}
