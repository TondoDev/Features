package org.tondo.dayeight;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

public class EscapingTest {
	
	private static String specialChars = "\"\\";
	

	@Test
	public void testEscapingAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/eight/unescaping.txt");
		assertNotNull(is);
		
		int diff = getCodesVSMemLenDiff(is);
		System.out.println("Day 8 Second part: Code - Mem = " + diff);
		assertEquals("Encoded Mem bytes difference", 2085, diff);
	}
	
	@Test
	public void testEscapingSamples() {
		assertEquals(6, getEncodedLength("\"\"")); // ""
		assertEquals(3, getEncodedLength("a")); // a
		assertEquals(7, getEncodedLength("\"a\"")); // "a"
		assertEquals(8, getEncodedLength("\"xa\"")); // "xa"
		assertEquals(10, getEncodedLength("\"\\\\\"")); // "\\"
		
		assertEquals(10, getEncodedLength("\"\\ad\"")); //"\ad"
		assertEquals(12, getEncodedLength("\"a\\\"b\"")); // "a\"b"
		//assertEquals(4, getEncodedLength("\"adb\\xF8\"")); // "adb\xF8"
//		assertEquals(0, getLengthInMem(""));
//		assertEquals(0, getLengthInMem(""));
	}
	
	
	private int getEncodedLength(String strLiteral) {
		StringBuilder sb = new StringBuilder();
		sb.append('"');
		int len = strLiteral.length();
		char curr = 0;
		for (int i = 0; i < len; i++) {
			curr = strLiteral.charAt(i);
			if (isSpecial(curr)) {
				sb.append('\\');
			}
			sb.append(curr);
		}
		sb.append('"');
		return sb.toString().length();
	}
	
	private int getCodesVSMemLenDiff(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		int codeLen = 0;
		int memLen = 0;
		String line = null;
		while ((line = reader.readLine()) != null) {
			codeLen += getEncodedLength(line);
			memLen += line.length();
		}
		
		return codeLen - memLen;
	}
	
	private boolean isSpecial(char ch) {
		return specialChars.indexOf(ch) > -1;
	}
}
