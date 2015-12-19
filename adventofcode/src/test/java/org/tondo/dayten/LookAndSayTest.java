package org.tondo.dayten;

import static org.junit.Assert.*;

import org.junit.Test;

public class LookAndSayTest {

	@Test
	public void testLookAndSayAnswer() {
		String lookAndSay = this.getLookAndTestSequence("3113322113", 40);
		System.out.println("Day 10. First part: Look and say legth: " +  lookAndSay.length());
		assertEquals("Day 10. First part: Look and say legth ", 329356, lookAndSay.length());
		
		lookAndSay = this.getLookAndTestSequence("3113322113", 50);
		System.out.println("Day 10. Second part: Look and say legth (50 times): " +  lookAndSay.length());
		assertEquals("Day 10. Second part: Look and say legth (50 times) ", 4666278, lookAndSay.length());
	}
	
	@Test
	public void testLookAndSaySamples() {
		assertEquals("11", getLookAndTestSequence("1", 1));
		assertEquals("21", getLookAndTestSequence("11", 1));
		assertEquals("1211", getLookAndTestSequence("21", 1));
		assertEquals("111221", getLookAndTestSequence("1211", 1));
		
		assertEquals("Iterations test", "111221", getLookAndTestSequence("1", 4));
	}
	
	
	private String getLookAndTestSequence(String initString, int reapeats) {
		
		String workingString = initString;
		for (int i = 0; i < reapeats; i++) {
			StringBuilder sb = new StringBuilder();
			int workLen = workingString.length();
			char current = 0;
			char prev = 0;
			int count = 0;
			for (int ci = 0; ci < workLen; ci++) {
				current = workingString.charAt(ci);
				if (current != prev && prev != 0) {
					sb.append(count)
						.append(prev);
					count = 1;
				} else {
					count++;
				}
				prev = current;
			}
			sb.append(count)
			.append(prev);
			workingString = sb.toString();
		}
		
		return workingString;
	}
}
