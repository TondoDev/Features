package org.tondo.advent2016.day14;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author TondoDev
 *
 */
public class PadKeyGenerator {
	
	private static final Pattern REPEATING_CHARS = Pattern.compile("(.)\\1{2,}");
	private static final int KEY_LIFE = 1000;
	
	public long getIndexForKeyCount(String salt, int numOfKeys) {
		return getIndexForKeyCount(salt, numOfKeys, new Hasher(salt));
	}
	
	public long getIndexForKeyCount(String salt, int numOfKeys, int strechFactor) {
		return getIndexForKeyCount(salt, numOfKeys, new Hasher(salt, strechFactor));
	}
	
	
	private long getIndexForKeyCount(String salt, int numOfKeys, Hasher hasher) {
		long index = 0;
		KeyCache register = new KeyCache(KEY_LIFE);
		List<Long> validKeyIndices = new ArrayList<>();
		while (validKeyIndices.size() < numOfKeys) {
			
			String hashValue = hasher.computeHashForIndex(index);
			Matcher tm = REPEATING_CHARS.matcher(hashValue);
			boolean prefix = false;
			while (tm.find()) {
				String found = tm.group(0);
				int fn = found.length();
				// if (!prefix && (fn == 3 || fn == 4)) {
				if (!prefix && (fn >= 3)) {
					register.registerPossibleChar(tm.group(1), index);
					prefix = true;
				} 
				if (fn >= 5) {
					validKeyIndices.addAll(register.matchedTripples(tm.group(1), index));
				}
			}
			index++;
		}
		Collections.sort(validKeyIndices);
		//printList(validKeyIndices);
		return validKeyIndices.get(numOfKeys - 1);
	}
	
	public long stupid(String salt, int numOfKeys) {
		
		long index = 0;
		Hasher hasher = new Hasher(salt);
		List<Long> validKeyIndices = new ArrayList<>();
		while (validKeyIndices.size() < numOfKeys) {
			String hashValue = hasher.computeHashForIndex(index);
			Matcher tm = REPEATING_CHARS.matcher(hashValue);
			if (tm.find()) {
				String found = tm.group(0);
				int fn = found.length();

				if (fn >= 3) {
					String fiveSearch =  new StringBuilder()
							.append(tm.group(1))
							.append(tm.group(1))
							.append(tm.group(1))
							.append(tm.group(1))
							.append(tm.group(1)).toString();
					for (int i = 1; i <= KEY_LIFE; i++) {
						String fivehashValue = hasher.computeHashForIndex(index + i);
						if (fivehashValue.contains(fiveSearch)) {
							validKeyIndices.add(index);
							break;
						}
					}
				} 
			}
			index++;
		}
		Collections.sort(validKeyIndices);
		printList(validKeyIndices);
		return validKeyIndices.get(validKeyIndices.size() - 1);
	}
	
	private void printList(List<Long> items)  {
		for (Long l : items) {
			System.out.println(l);
		}
	}
}
