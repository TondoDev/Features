package org.tondo.advent2016.run.day2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.junit.Test;
import org.tondo.advent2016.day2.Decryptor;
import org.tondo.advent2016.day2.DiamondDecryptor;
import org.tondo.advent2016.day2.ClassicDecrypt;

/**
 * Bathroom security
 * 
 * @author TondoDev
 *
 */
public class Day2Test {
	
	
	@Test
	public void testPart1() throws IOException {
		InputStream input = getClass().getResourceAsStream("/day2/day2Part1.txt");
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")))) {
			Decryptor decryptor = new ClassicDecrypt();
			Decryptor diamond = new DiamondDecryptor();
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				decryptor.decryptLine(line);
				diamond.decryptLine(line);
			}
			
			System.out.println("Day 2 - Part 1: password " + decryptor.getPassword()); // 56855
			System.out.println("Day 2 - Part 2: password " + diamond.getPassword()); // B3C27
		}
	}
}
