package org.tondo.dayone;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class BasementTest {
	
	@Test
	public void testBasementAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/dayone/basement.txt");
		assertNotNull(is);
		int index = getFirstBasementIndex(is);
		System.out.println("Day 1. Second part: Basement index is: " + index);
		assertEquals(" Basement index", 1783,  index);
	}
	
	@Test
	public void testBasementSamples() throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(")".getBytes());
		assertEquals(1, getFirstBasementIndex(is));
		
		is = new ByteArrayInputStream("()())".getBytes());
		assertEquals(5, getFirstBasementIndex(is));
	}
	
	
	private int getFirstBasementIndex(InputStream is) throws IOException {
		int currentIndex = 1;
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
			
			if (currentFloor == -1) {
				return currentIndex;
			}
			
			currentIndex++;
		}
		
		throw new IllegalStateException("Basement never visited!");
	}

}
