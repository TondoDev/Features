package org.tondo.advent2016.day14;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author TondoDev
 *
 */
public class PadKeyGenerator {
	
	private static final Pattern THREE_C = Pattern.compile("(.)\\2\\2");
	private static final Pattern FIVE_C = Pattern.compile("(.)\\2\\2\\2\\2");
	
	private static final int KEY_LIFE = 1000;
	
	public long getIndexForKeyCount(String salt, int numOfKeys) {
		
		int foundKeys = 0;
		long index = 0;
		Hasher hasher = new Hasher(salt);
		KeyRegister register = new KeyRegister(KEY_LIFE);
		while (foundKeys < numOfKeys) {
			
			String hashValue = hasher.computeHashForIndex(index);
			Matcher tm = THREE_C.matcher(hashValue);
			if (tm.find()) {
				register.registerPossibleChar(tm.group(1), index);
			}
			
			// TODO are more matching groups of 4 chars possible?
			// overlapping sequences?
			Matcher fm = FIVE_C.matcher(hashValue);
			while (fm.find()) {
				if (register.isPadKey(fm.group(1), index)) {
					foundKeys++;
				}
			}
			
			index++;
		}
		
		return index - 1;
	}
}
