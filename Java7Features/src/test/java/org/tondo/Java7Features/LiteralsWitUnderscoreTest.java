package org.tondo.Java7Features;

import org.junit.Test;

import static org.junit.Assert.*;

public class LiteralsWitUnderscoreTest {

	@Test
	public void testNumbersWitUnderscore() {
		assertEquals(10, 1_0);
		assertEquals(10, 1___0);
		assertEquals(10200L, 10_20__0L);
		assertEquals(5.0015f, 5.00_15f, 0.0005);
		assertEquals(500000.5, 500_000.5f, 0.1);
		
		assertEquals(0x1515, 0x15_15);
		assertEquals(0b10110001, 0b1011__0001);
		
		
		// INVALID CONSTRUCTIONs
		// int a = 10_;
		// int a = _10;
		// float a = 5_.1
		// float a = 5._1
	}
}
