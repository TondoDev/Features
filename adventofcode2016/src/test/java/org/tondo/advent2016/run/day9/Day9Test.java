package org.tondo.advent2016.run.day9;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.tondo.advent2016.day9.Decompressor;

/**
 * 
 * @author TondoDev
 *
 */
public class Day9Test {
	
	@Test
	public void testPart1() throws UnsupportedEncodingException, IOException {
		InputStream input = getClass().getResourceAsStream("/day9/day9Part1.txt");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
			Decompressor decompressor = new Decompressor();
			decompressor.decompress(reader);
			
			System.out.println("Day 9 Part 1: Decomressed length: " + decompressor.getDecompressedLen()); // 152851
		}
	}
	
	
	@Test
	public void testDecompressor() throws IOException {
		Decompressor dec = null;
		
		dec = new Decompressor();
		dec.decompress(new StringReader(""));
		assertEquals("empty string", 0, dec.getDecompressedLen());
		
		dec = new Decompressor();
		dec.decompress(new StringReader("ABCD"));
		assertEquals(4, dec.getDecompressedLen());
		
		dec = new Decompressor();
		dec.decompress(new StringReader("ABCD("));
		assertEquals("incomplete mark", 5, dec.getDecompressedLen());
		
		dec = new Decompressor();
		dec.decompress(new StringReader("ABCD(58"));
		assertEquals("incomplete mark", 7, dec.getDecompressedLen());
		
		dec = new Decompressor();
		dec.decompress(new StringReader("ABCD(58a58)"));
		assertEquals("wrong syntax mark", 11, dec.getDecompressedLen());
		
		dec = new Decompressor();
		dec.decompress(new StringReader("A(1x5)BC"));
		assertEquals(7, dec.getDecompressedLen());
		
		dec = new Decompressor();
		dec.decompress(new StringReader("(3x3)XYZ"));
		assertEquals(9, dec.getDecompressedLen());
		
		dec = new Decompressor();
		dec.decompress(new StringReader("A(2x2)BCD(2x2)EFG"));
		assertEquals(11, dec.getDecompressedLen());
		
		dec = new Decompressor();
		dec.decompress(new StringReader("(6x1)(1x3)A"));
		assertEquals(6, dec.getDecompressedLen());
		
		dec = new Decompressor();
		dec.decompress(new StringReader("X(8x2)(3x3)ABCY"));
		assertEquals(18, dec.getDecompressedLen());
	}
}
