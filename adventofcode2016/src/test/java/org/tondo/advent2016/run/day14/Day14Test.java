package org.tondo.advent2016.run.day14;

import org.junit.Test;
import org.tondo.advent2016.day14.PadKeyGenerator;

import static org.junit.Assert.*;

/**
 * 
 * @author TondoDev
 *
 */
public class Day14Test {
	
	private static final String MY_SALT = "";
	private static final String SAMPLE_SALT = "abc";
	
	private static final int NUM_OF_KEYS = 64;

	@Test
	public void testPart1() {
		PadKeyGenerator generator = new PadKeyGenerator();
		long index = generator.getIndexForKeyCount(MY_SALT, NUM_OF_KEYS);
		System.out.println("Day 14 - Part 1: Index after all keys: " + index);
	}
	
	
	@Test
	public void testSample() {
		PadKeyGenerator generator = new PadKeyGenerator();
		long index = generator.getIndexForKeyCount(SAMPLE_SALT, NUM_OF_KEYS);
		assertEquals(22728, index);
	}
}
