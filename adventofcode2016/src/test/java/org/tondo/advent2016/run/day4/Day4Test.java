package org.tondo.advent2016.run.day4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.tondo.advent2016.day4.RoomDecrypter;

/**
 * 
 * @author TondoDev
 *
 *
 */
public class Day4Test {

	@Test
	public void testPart1() throws UnsupportedEncodingException, IOException {
		InputStream is = getClass().getResourceAsStream("/day4/day4Part1.txt");
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
			RoomDecrypter decrypter = new RoomDecrypter();
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					decrypter.processRoom(RoomDecrypter.parseLine(line));
				}
			}
			
			System.out.println("Day 4 - Part 1: sector sum: " + decrypter.getSectorSum()); // 137896
			System.out.println("Day 4 - Part 2: storage sectorId: " + decrypter.getNorthPoleStorageRoom()); // 501
		}
	}
	
	@Test
	public void testRegExp() {
		Pattern p = Pattern.compile("^(.+)-(\\d+)\\[([a-z]{5})\\]$");
		Matcher m = p.matcher("aaaa-bbb-z-y-x-123[abxyz]");
		System.out.println("Find: " + m.find());
		System.out.println("name: " + m.group(1));
		System.out.println("sector: " + m.group(2));
		System.out.println("checksum: " + m.group(3));
	}
	
	
	@Test
	public void testSingleRoom() {
		RoomDecrypter decrypter = new RoomDecrypter();
		decrypter.processRoom(RoomDecrypter.parseLine("totally-real-room-200[decoy]"));
		System.out.println(decrypter.getSectorSum());
	}
	
	@Test
	public void testCaesar() {
		System.out.println(new RoomDecrypter().decryptRoomName("qzmt-zixmtkozy-ivhz", 343));
	}
}
