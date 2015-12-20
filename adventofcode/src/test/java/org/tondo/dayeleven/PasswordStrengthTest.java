package org.tondo.dayeleven;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class PasswordStrengthTest {
	
	private static int DIFF = 'z' - 'a' + 1; 
	private Pattern dangerous = Pattern.compile("[iol]");
	private Pattern doubled = Pattern.compile("(.)\\1");
	
	@Test
	public void testPasswordStrengthAnswer() {
		
		String nextValidPwd = getNextValidPassword("hepxcrrq");
		System.out.println("Day 11 First part: Next valid password: " + nextValidPwd);
		assertEquals("Day 11 First part: Next valid password", "hepxxyzz", nextValidPwd);
		
		String nextNextPwd = getNextValidPassword(nextValidPwd);
		System.out.println("Day 11 Second part: Next valid password: " + nextNextPwd);
		assertEquals("Day 11 Second part: Next valid password", "hepxxyzz", nextNextPwd);
	}
	
	@Test
	public void testPasswrodStrengthSamples() {
		assertEquals("abcdffaa", getNextValidPassword("abcdefgh"));
		assertEquals("ghjaabcc", getNextValidPassword("ghijklmn"));
	}
	
	@Test
	public void testWordIncrement() {
		assertEquals("a", incrementWord("a", 0));
		assertEquals("b", incrementWord("a", 1));
		assertEquals("i", incrementWord("a", 8));
		assertEquals("az", incrementWord("ay", 1));
		assertEquals("ba", incrementWord("ay", 2));
	}
	
	@Test
	public void predicateTest() {
		assertTrue(isDoubled("aaobbc"));
		assertTrue(isDoubled("aaaa"));
		assertFalse(isDoubled("aaoc"));
		assertFalse(isDoubled("aaa"));
		
		
		assertTrue(isIncreasing("abcbbc"));
		assertTrue(isIncreasing("aaefg"));
		assertFalse(isIncreasing("abdc"));
		assertFalse(isIncreasing("aaa"));
		
	}
	
	private String getNextValidPassword(String prevPwd) {
		String str = prevPwd;
		while ((str = incrementWord(str, 1)) != null) {
			// optimalization for string which can be skipped because it contains illegal characters
			Matcher m = dangerous.matcher(str);
			if (m.find()) {
				int index = m.start();
				int len = str.length();
				StringBuilder sb = new StringBuilder(str);
				sb.setCharAt(index, (char) (str.charAt(index) + 1));
				for (int i = index + 1; i < len; i++) {
					sb.setCharAt(i, 'a');
				}
				str = sb.toString();
			}
			
			if (isDoubled(str) && isIncreasing(str)) {
				return str;
			}
		}
		
		throw new IllegalStateException("Now valid password can be generated from given string!");
	}
	
	private boolean isIncreasing(String str) {
		int len = str.length();
		int seq = 0;
		char prev = 0;
		for (int i = 0; i < len; i ++) {
			char curr = str.charAt(i);
			
			if (prev != 0 && ((prev + 1) ==  curr)) {
				seq++;
			} else {
				seq = 1;
			}
			
			if (seq == 3) {
				return true;
			}
			
			prev = curr;
		}
		
		return false;
	}
	
	private boolean isDoubled(String str) {
		Matcher matcher = doubled.matcher(str);
		if (!matcher.find()) {
			return false;
		}
		int firstOccur = matcher.start();
		
		if (!matcher.find()) {
			return false;
		}
		
		return (matcher.start() - firstOccur) > 1;
	}
	
	/**
	 * Return null if overflow is set after string capacity
	 * String capacity is inherited from source string.
	 * @param word
	 * @param incrementSize
	 * @return
	 */
	private String incrementWord(String word, int incrementSize) {
		StringBuilder sb = new StringBuilder(word);
		
		int len = word.length();
		int overflow = incrementSize;
	
		for (int i = len - 1; i >= 0 && overflow > 0; i--) {
			int curr = word.charAt(i) - 'a';
			int added = curr + overflow;
			int mod = (added % DIFF);
			char fc = (char) (mod + 'a');
			sb.setCharAt(i, fc);
			overflow = added / DIFF;
		}
		
		if (overflow > 0) {
			return null;
		}
		
		return sb.toString();
	}
}
