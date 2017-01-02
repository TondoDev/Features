package org.tondo.advent2016.run.day7;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.junit.Test;
import org.tondo.advent2016.day7.TlsProcessor;

/**
 * 
 * 
 *
 */
public class Day7Test {

	
	@Test
	public void testPart1() throws IOException {
		InputStream input = getClass().getResourceAsStream("/day7/day7Part1.txt");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")))) {
			TlsProcessor processor = new TlsProcessor();
			String line = null;
			while ((line = reader.readLine()) != null) {
				processor.processIP(line);
			}
			
			System.out.println("Day 7 Part 1: Tls supported addresses: " + processor.getTlsSupportedCount());
		}
	}
	
	
	@Test
	public void tokenTest() {
		TlsProcessor proc = new TlsProcessor();
		
		assertTrue(proc.isTlsSupported("abba[mnop]qrst"));
		assertFalse(proc.isTlsSupported("abcd[bddb]xyyx"));
		assertFalse(proc.isTlsSupported("aaaa[qwer]tyui"));
		assertTrue(proc.isTlsSupported("aaaa[qwer]xyyx"));
		assertTrue(proc.isTlsSupported("ioxxoj[asdfgh]zxcvbn"));
		assertFalse(proc.isTlsSupported("pfvpbqmyapypiubxmt[kjfhekecjzzqftpj]sybxxlmvzhloooom"));
	}
}
