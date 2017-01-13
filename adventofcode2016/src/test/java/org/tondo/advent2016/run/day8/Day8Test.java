package org.tondo.advent2016.run.day8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			System.out.println(screen);
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
	
	@Test
	public void testParsingPattern() {
		 final Pattern ROTATE_COL = Pattern.compile("^rotate column x=([0-9][0-9]*) by ([1-9][0-9]*)$");
		 final Pattern ROTATE_ROW = Pattern.compile("^rotate row y=([0-9][0-9]*) by ([1-9][0-9]*)$");
		 
		 assertFalse(ROTATE_COL.matcher("rotate row y=0 by 4").find());
		 Matcher colMatch = ROTATE_COL.matcher("rotate column x=18 by 1");
		 assertTrue(colMatch.find());
		 assertEquals("columnIndex", "18", colMatch.group(1));
		 assertEquals("pixes count", "1", colMatch.group(2));
		 
		 assertFalse(ROTATE_ROW.matcher("rotate column x=18 by 1").find());
		 Matcher rowMatch = ROTATE_ROW.matcher("rotate row y=0 by 4");
		 assertTrue(rowMatch.find());
		 assertEquals("rowIndex", "0", rowMatch.group(1));
		 assertEquals("pixes count", "4", rowMatch.group(2));
		 
	}
}
