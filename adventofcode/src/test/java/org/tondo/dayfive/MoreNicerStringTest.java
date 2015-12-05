package org.tondo.dayfive;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.junit.Test;

public class MoreNicerStringTest {
	
	private Pattern pair = Pattern.compile("(..).*\\1");
	private Pattern middlechar = Pattern.compile("(.).\\1");
	
	@Test
	public void testMoreNicerStringAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/dayfive/nicestringsupgraded.txt");
		assertNotNull(is);
		
		int niceStrings = getMoreNicerStringCount(is);
		System.out.println("Day 5. Second part: Nice strings count: " + niceStrings);
		assertEquals("Nice strings count", 53,  niceStrings);
	}
	
	@Test
	public void testNiceStringSamples() {
		assertTrue(isMoreNicerString("qjhvhtzxzqqjkmpb"));
		assertTrue(isMoreNicerString("xxyxx"));
		assertTrue(isMoreNicerString("aaaa"));
		
		assertFalse(isMoreNicerString("tgatg"));
		assertFalse(isMoreNicerString("aaa"));
		assertFalse(isMoreNicerString("uurcxstgmygtbstg"));
		assertFalse(isMoreNicerString("haegwjzuvuyypxyu"));
		assertFalse(isMoreNicerString("ieodomkazucvgmuy"));
	}
	
	
	private int getMoreNicerStringCount(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		int niceStringsCount = 0;
		
		String line = null;
		while((line = reader.readLine()) != null) {
			if (isMoreNicerString(line)) {
				niceStringsCount++;
			}
		}
		
		return niceStringsCount;
	}
	
	private boolean isMoreNicerString(String param) {
		
		if (!pair.matcher(param).find()) {
			return false;
		}
		
		if(!middlechar.matcher(param).find()) {
			return false;
		}
		
		return true;
	}

}
