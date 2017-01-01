package org.tondo.advent2016.run.day6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.junit.Test;
import org.tondo.advent2016.day6.MessageReconstructor;

/**
 * 
 * @author TondoDev
 *
 */
public class Day6Test {

	@Test
	public void testPart1() throws IOException {
		
		InputStream input = getClass().getResourceAsStream("/day6/day6Part1.txt");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")))) {
			MessageReconstructor recon = new MessageReconstructor();
			String line = null;
			while((line = reader.readLine()) != null) {
				if (!line.isEmpty()) {
					recon.processMessageChunk(line);
				}
			}
			
			System.out.println("Day 6 Part 1: message " + recon.reconstruct());	// xhnqpqql
			System.out.println("Day 6 Part 2: message " + recon.reconstructMin()); // brhailro
		}
	}
}
