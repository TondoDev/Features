package org.tondo.dayfive;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class NiceStringTest {
	
	private Pattern vowels = Pattern.compile("[aeiou]");
	private Pattern doubled = Pattern.compile("(.)\\1");
	private Pattern illegal = Pattern.compile("ab|cd|pq|xy");
	
	@Test
	public void testNiceyStringAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/dayfive/nicestrings.txt");
		assertNotNull(is);
		
		int niceStrings = getNiceStringCount(is);
		System.out.println("Day 5. First part: Nice strings count: " + niceStrings);
		assertEquals("Nice strings count", 258,  niceStrings);
	}
	
	@Test
	public void testNiceStringSamples() {
		
		assertTrue(isNiceString("ugknbfddgicrmopn"));
		assertTrue(isNiceString("aaa"));

		assertFalse(isNiceString("jchzalrnumimnmhp"));
		assertFalse(isNiceString("haegwjzuvuyypxyu"));
		assertFalse(isNiceString("dvszwmarrgswjxmb"));
	}
	
	
	private int getNiceStringCount(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		int niceStringsCount = 0;
		
		String line = null;
		while((line = reader.readLine()) != null) {
			if (isNiceString(line)) {
				niceStringsCount++;
			}
		}
		
		return niceStringsCount;
	}
	
	private boolean isNiceString(String param) {
		
		Matcher vowelMatcher =  vowels.matcher(param);
		int vowelsFound = 0;
		while (vowelMatcher.find() && vowelsFound < 3) {
			vowelsFound++;
		}
		
		if (vowelsFound < 3) {
			return false;
		}
		
		if(!doubled.matcher(param).find()) {
			return false;
		}
		
		if (illegal.matcher(param).find()) {
			return false;
		}
		
		return true;
	}

}
