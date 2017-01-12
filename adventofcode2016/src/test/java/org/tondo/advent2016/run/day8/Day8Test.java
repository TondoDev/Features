package org.tondo.advent2016.run.day8;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.tondo.advent2016.day8.InsctructionDecoder;
import org.tondo.advent2016.day8.ScreenInstruction;
import org.tondo.advent2016.day8.TinyScreen;

/**
 * 
 * @author TondoDev
 *
 */
public class Day8Test {

	@Test
	public void testPart1() throws UnsupportedEncodingException, IOException {
		InputStream input = getClass().getResourceAsStream("/day8/day8Part1.txt");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
			TinyScreen screen = new TinyScreen();
			InsctructionDecoder decoder = new InsctructionDecoder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				ScreenInstruction instr = decoder.decode(line);
				instr.execute(screen);
			}
			
			System.out.println("Day 8 Part 1 - Lit pixels count: " + screen.getLitPixelsCount());
		}
	}
	
	@Test
	public void testScreen() {
		TinyScreen screen = new TinyScreen();
		assertEquals("empty screen", 0, screen.getLitPixelsCount());
		
		screen.rect(2, 2);
		
		screen.rotateRow(0, 2);
		screen.rotateRow(1, 2);
		
		screen.rotateCol(2, 2);
		screen.rotateCol(3, 2);
		
		screen.rect(2, 2);
		assertEquals(8, screen.getLitPixelsCount());
	}
}
