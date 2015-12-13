package org.tondo.dayeight;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

public class UnescapingTest {
	
	private static String hexaChars = "0123456789ABCDEFabcdef";
	
	private static enum State {
		START,
		OPEN,
		CHARACTER,
		ESCAPING,
		CODE,
		FCCH, // first code Character 
		SCCH, // second code character
		CLOSING
	}

	@Test
	public void testUnscapingAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/dayeight/unescaping.txt");
		assertNotNull(is);
		
		int diff = getCodesVSMemLenDiff(is);
		System.out.println("Day 8 First part: Code - Mem = " + diff);
		assertEquals("Code Mem bytes difference", 1350, diff);
	}
	
	@Test
	public void testUnescapingSamples() {
		assertEquals(0, getLengthInMem("\"\""));
		assertEquals(1, getLengthInMem("\"a\""));
		assertEquals(2, getLengthInMem("\"xa\""));
		assertEquals(1, getLengthInMem("\"\\\\\"")); // "\"
		
		assertEquals(2, getLengthInMem("\"\\ad\"")); //"\ad"
		assertEquals(3, getLengthInMem("\"a\\\"b\"")); // "a\"b"
		assertEquals(4, getLengthInMem("\"adb\\xF8\"")); // "adb\xF8"
//		assertEquals(0, getLengthInMem(""));
//		assertEquals(0, getLengthInMem(""));
	}
	
	
	private int getLengthInMem(String strLiteral) {
		char curr = 0;
		int memSize = 0;
		State s = State.START;
		
		
		int len = strLiteral.length();
		for (int i = 0; i < len; i++) {
			curr = strLiteral.charAt(i);
			
			if (s == State.START) {
				if (curr != '"') {
					throw new IllegalStateException("Missing first \" (uvodzovka)");
				} else {
					s = State.CHARACTER;
				}
			} else if (s == State.CLOSING) {
				throw new IllegalStateException("Unexpected closing uvodzovka");
			} else if (s == State.CHARACTER && curr == '\\') {
				s=State.ESCAPING;
			} else if (s == State.ESCAPING) {
				if (curr == 'x') {
					s = State.CODE;
				} else {
					memSize++;
					s = State.CHARACTER;
				}
			} else if (s == State.CODE) {
				if (isHex(curr)) {
					s = State.SCCH;
				} else {
					throw new IllegalStateException("Invalid code character");
				}
			} else if (s == State.SCCH) {
				if (isHex(curr)) {
					s = State.CHARACTER;
					memSize++;
				} else {
					throw new IllegalStateException("Invalid code character");
				}
			} else if (s == State.CHARACTER && curr == '"') {
				s = State.CLOSING;
			} else if (s == State.CHARACTER) {
				memSize++;
			}
		}
		
		if (s != State.CLOSING) {
			throw new IllegalStateException("Unexpected end of string");
		}
		
		return memSize;
	}
	
	private int getCodesVSMemLenDiff(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		int codeLen = 0;
		int memLen = 0;
		String line = null;
		while ((line = reader.readLine()) != null) {
			codeLen += line.length();
			memLen += getLengthInMem(line);
		}
		
		return codeLen - memLen;
	}
	
	private boolean isHex(char ch) {
		return hexaChars.indexOf(ch) > -1;
	}
}
