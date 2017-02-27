package org.tondo.advent2016.run.day14;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.tondo.advent2016.day14.Hasher;
import org.tondo.advent2016.day14.PadKeyGenerator;

/**
 * 
 * @author TondoDev
 *
 */
public class Day14Test {
	
	private static final String MY_SALT = "cuanljph";
	private static final String SAMPLE_SALT = "abc";
	
	private static final int NUM_OF_KEYS = 64;
	
	@Test
	public void testPart1() {
		PadKeyGenerator generator = new PadKeyGenerator();
		long index = generator.getIndexForKeyCount(MY_SALT, NUM_OF_KEYS);
		System.out.println("Day 14 - Part 1: Index after all keys: " + index); //23769
	}

	@Test
	public void testPart1Stupid() {
		PadKeyGenerator generator = new PadKeyGenerator();
		long index = generator.stupid(MY_SALT, NUM_OF_KEYS);
		System.out.println("Day 14 - Part 1: Index after all keys: " + index); //23769
	}
	
	
	@Test
	public void testSample() {
		PadKeyGenerator generator = new PadKeyGenerator();
		//8811
		long index = generator.getIndexForKeyCount(SAMPLE_SALT, NUM_OF_KEYS);
		assertEquals(22728, index);
	}
	
	@Test
	public void testStupid() {
		PadKeyGenerator generator = new PadKeyGenerator();
		//8811
		long index = generator.stupid(MY_SALT, NUM_OF_KEYS);
		assertEquals(22728, index);
	}
	
	@Test
	public void testRegExp() {
		// see negative lookahead
		Pattern pattern = Pattern.compile("((.)\\2\\2)(?!s)");
		Matcher m = pattern.matcher("vfgtxxxx");
		assertTrue(m.find());
		assertEquals("x", m.group(2));
		assertEquals("xxx", m.group(1));
	}
	
	@Test
	public void testNegativeLookahead() {
		Pattern test = Pattern.compile("(.)\\1\\1(?!\\1\\1)(.+|$)");
		Matcher m = test.matcher("abcxxxxxa");
		assertTrue(m.find());
		for (int i = 0; i <= m.groupCount(); i++) {
			System.out.println(i + ": " + m.group(i));
		}
	}
	
	@Test
	public void testOtherAproach() {
		Pattern test = Pattern.compile("(.)\\1+");
		Matcher m = test.matcher("abbx");
		assertTrue(m.find());
		for (int i = 0; i <= m.groupCount(); i++) {
			System.out.println(i + ": " + m.group(i));
		}
	}
	
	@Test
	public void testHasToString() {
		Hasher h = new Hasher("abc");
		System.out.println(h.computeHashForIndex(39));
	}
}
