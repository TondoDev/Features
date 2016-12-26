package org.tondo.advent2016.day4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author TondoDev
 *
 */
public class RoomDecrypter {
	
	private static final Pattern PARSER = Pattern.compile("^(.+)-(\\d+)\\[([a-z]{5})\\]$");
	private static final int CHECKSUM_LEN = 5;
	private static final int DIVISOR = 26;
	
	private static final Comparator<Map.Entry<Character, Integer>> COMPARATOR = new Comparator<Map.Entry<Character,Integer>>() {

		@Override
		public int compare(Entry<Character, Integer> o1, Entry<Character, Integer> o2) {
			int countRes = o2.getValue().compareTo(o1.getValue());
			return countRes != 0 ? countRes : o1.getKey().compareTo(o2.getKey());
		}
	};
	
	public static class RoomData {
		private String name;
		private String checksum;
		private int sector;
		
		private RoomData(String n, String chk, int s) {
			this.name = n;
			this.checksum =chk;
			this.sector = s;
		}
		
		public String getName() {
			return name;
		}
		
		public String getChecksum() {
			return checksum;
		}
		
		public int getSector() {
			return sector;
		}
	}
	
	private long sectorSum;
	private int northPoleStorageRoom;
	
	
	public RoomDecrypter() {
		sectorSum = 0L;
	}
	
	public long getSectorSum() {
		return sectorSum;
	}
	
	public void processRoom(RoomData room) {
		Map<Character, Integer> letterStats = countLetters(room.getName());
		String checksum = calculateChecksum(letterStats);
		
		if (checksum.equals(room.getChecksum())) {
			this.sectorSum += room.getSector();
			String decryptedName = decryptRoomName(room.name, room.sector);
			if (decryptedName.contains("north")) {
				// expected one and only one room contains 'north' string
				this.northPoleStorageRoom = room.sector;
			}
		}
	}
	
	private String calculateChecksum (Map<Character, Integer> letters) {
		LinkedHashMap<Character, Integer> sortedLetters = sortLetters(letters);
		
		StringBuilder buffer = new StringBuilder();
		Iterator<Character> iterator = sortedLetters.keySet().iterator();
		int cnt = 0;
		
		while (iterator.hasNext() && cnt < CHECKSUM_LEN) {
			buffer.append(iterator.next());
			cnt++;
		}
		
		return buffer.toString();
	}
	
	private LinkedHashMap<Character, Integer> sortLetters(Map<Character, Integer> letters) {
		List<Map.Entry<Character, Integer>> tmpList = new ArrayList<>(letters.entrySet());
		Collections.sort(tmpList, COMPARATOR);
		LinkedHashMap<Character, Integer> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<Character, Integer> entry : tmpList) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		
		return sortedMap;
	}
	
	private Map<Character, Integer> countLetters(String name) {
		Map<Character, Integer> counter = new HashMap<>();
		
		int len = name.length();
		for (int i = 0; i < len; i++) {
			Character letter = name.charAt(i);
			// ignoring delimiter dashes
			if (letter == '-') {
				continue;
			}
			Integer currentLetterCount = counter.get(letter);
			if (currentLetterCount == null) {
				counter.put(letter, 1);
			} else {
				counter.put(letter, currentLetterCount + 1);
			}
		}
		
		return counter;
	}
	
	public String decryptRoomName(String name, int sectorId) {
		
		StringBuilder decryptBuffer = new StringBuilder();
		
		int len = name.length();
		for (int i = 0; i < len; i++) {
			int c = name.charAt(i);
			if (c == '-') {
				decryptBuffer.append(' ');
			} else {
				int ordinal = ((int)(c - 'a') + sectorId)%DIVISOR;
				decryptBuffer.append((char)(ordinal + 'a'));
			}
		}
		
		return decryptBuffer.toString();
	}
	
	public int getNorthPoleStorageRoom() {
		return northPoleStorageRoom;
	}
	
	public static RoomData parseLine(String line) {
		Matcher tonkenizer = PARSER.matcher(line);
		
		if (!tonkenizer.find()) {
			throw new IllegalArgumentException("Incorrect format of room data! " + line);
		}
		
		String name = tonkenizer.group(1);
		int sector = Integer.parseInt(tonkenizer.group(2));
		String checksum = tonkenizer.group(3);
		return new RoomData(name, checksum, sector);
	}
}
