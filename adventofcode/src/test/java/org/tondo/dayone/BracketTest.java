package org.tondo.dayone;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class BracketTest {
	
	@Test
	public void testBracketsAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/dayone/brackets.txt");
		assertNotNull(is);
		int floor = getRightFloor(is);
		System.out.println("Day 1. First part: Current floor is: " + floor);
		assertEquals("Current floor", 232,  floor);
	}
	
	@Test
	public void testBracketExamples() throws IOException {
		
		ByteArrayInputStream is = new ByteArrayInputStream("(())".getBytes());
		assertEquals(0, getRightFloor(is));
		
		is = new ByteArrayInputStream("()()".getBytes());
		assertEquals(0, getRightFloor(is));
		
		is = new ByteArrayInputStream("(((".getBytes());
		assertEquals(3, getRightFloor(is));
		is = new ByteArrayInputStream("(()(()(".getBytes());
		assertEquals(3, getRightFloor(is));
		is = new ByteArrayInputStream("))(((((".getBytes());
		assertEquals(3, getRightFloor(is));
		
		
		is = new ByteArrayInputStream("())".getBytes());
		assertEquals(-1, getRightFloor(is));
		is = new ByteArrayInputStream("))(".getBytes());
		assertEquals(-1, getRightFloor(is));
		
		is = new ByteArrayInputStream(")))".getBytes());
		assertEquals(-3, getRightFloor(is));
		is = new ByteArrayInputStream(")())())".getBytes());
		assertEquals(-3, getRightFloor(is));
	}
	
	
	private int getRightFloor(InputStream is) throws IOException {
		int currentFloor = 0;
		
		int data = -1;
		while ((data = is.read()) != -1) {
			if ('(' == (char)data ) {
				currentFloor++;
			} else if (')' == (char)data) {
				currentFloor--;
			} else {
				throw new IllegalStateException("Not valid input character!");
			}
		}
		
		return currentFloor;
	}
   
}
