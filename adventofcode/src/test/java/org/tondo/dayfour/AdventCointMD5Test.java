package org.tondo.dayfour;

import static org.junit.Assert.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

public class AdventCointMD5Test {
	
	@Test
	public void testAdventCoinMD5Answer() throws NoSuchAlgorithmException {
		int number = getLowestIntegerBrutalForce("iwrupvqb", "00000");
		System.out.println("Day 4. First part: Lowest number 5 zeroes: " + number);
		assertEquals("Lowest number:", 346386,  number);
		
		number = getLowestIntegerBrutalForce("iwrupvqb", "000000");
		System.out.println("Day 4. Second part: Lowest number 6 zeroes: " + number);
		assertEquals("Lowest number:", 9958218,  number);
	}
	
	@Test
	public void testJavaMD5Samples() throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		
		byte[] data = md5.digest("abcdef609043".getBytes());
		assertTrue(byteArrayToHexString(data).startsWith("000001dbbfa"));
		
		data = md5.digest("pqrstuv1048970".getBytes());
		assertTrue(byteArrayToHexString(data).startsWith("000006136ef"));
	}
	
	
	@Test
	public void testAdventCoinSamplesBurtalForice() throws NoSuchAlgorithmException {
		int number = getLowestIntegerBrutalForce("abcdef", "00000");
		assertEquals(609043, number);
		
		number = getLowestIntegerBrutalForce("pqrstuv", "00000");
		assertEquals(1048970, number);
	}
	
	public int getLowestIntegerBrutalForce(String prefix, String startsWith) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		
		int number = 1;
		// try it till overflow
		while (number > 0) {
			String md5Input = prefix + number;
			byte[] digest = md5.digest(md5Input.getBytes());
			if (byteArrayToHexString(digest).startsWith(startsWith)) {
				return number;
			}			
			number++;
		}
		
		throw new IllegalStateException("Can't find suitable numner");
		
	}
	
	private String byteArrayToHexString(byte[] data) {
		StringBuilder sb = new StringBuilder();
		
		for (byte b : data) {
			String hexStr = Integer.toHexString(b & 0xFF);
			if (hexStr.length() < 2) {
				hexStr = "0"+hexStr;
			}
			sb.append(hexStr);
		}
		
		return sb.toString();
	}
}
