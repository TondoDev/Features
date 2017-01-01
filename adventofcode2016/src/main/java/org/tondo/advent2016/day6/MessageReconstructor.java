package org.tondo.advent2016.day6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author TondoDev
 *
 */
public class MessageReconstructor {
	
	private static final int MSG_LEN = 8;
	private List<Map<Character, Integer>> charCounter;
	
	public MessageReconstructor() {
		this.charCounter = new ArrayList<Map<Character, Integer>>();
		for (int i = 0; i < MSG_LEN; i++) {
			this.charCounter.add(new HashMap<Character, Integer>());
		}
	}
	
	
	public void processMessageChunk(String chunk) {
		int len = chunk.length();
		for (int i = 0; i < len; i++) {
			char c = chunk.charAt(i);
			Map<Character, Integer> counter = this.charCounter.get(i);
			Integer count = counter.get(c);
			if (count == null) {
				counter.put(c, 1);
			} else {
				counter.put(c, ++count);
			}
		}
	}
	
	public String reconstruct() {
		StringBuilder buffer = new StringBuilder();
		
		for (Map<Character, Integer> entry : this.charCounter) {
			buffer.append(findMaxOccured(entry));
		}
		
		return buffer.toString();
	}
	
	public String reconstructMin() {
		StringBuilder buffer = new StringBuilder();
		
		for (Map<Character, Integer> entry : this.charCounter) {
			buffer.append(findMinOccured(entry));
		}
		
		return buffer.toString();
	}
	
	private char findMinOccured(Map<Character, Integer> chars) {
		char minxChar = 127;
		int minOccurence = Integer.MAX_VALUE;
		for (Map.Entry<Character, Integer> entry : chars.entrySet()) {
			if (entry.getValue() < minOccurence) {
				minxChar = entry.getKey();
				minOccurence = entry.getValue();
			}
		}
		
		return minxChar;
	}
	
	private char findMaxOccured(Map<Character, Integer> chars) {
		char maxChar = 0;
		int maxOccurence = -1;
		for (Map.Entry<Character, Integer> entry : chars.entrySet()) {
			if (entry.getValue() > maxOccurence) {
				maxChar = entry.getKey();
				maxOccurence = entry.getValue();
			}
		}
		
		return maxChar;
	}
}
