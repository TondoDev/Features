package org.tondo.advent2016.day5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author TondoDev
 *
 */
public class PasswordCreator {

	private MessageDigest md5;
	private static final int PASS_LEN = 8;
	
	
	public PasswordCreator() {
		try {
			this.md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			
			throw new IllegalStateException("MD5 algorithm not supported!");
		}
	}
	
	public String createPasswordSimple(String seed) {
		StringBuilder password = new StringBuilder();
		int passCharIndex = 0;
		long suffix = 1;
		while (passCharIndex < PASS_LEN) {
			byte[] hashInput = (seed + suffix).getBytes();
			byte[] digest = md5.digest(hashInput);
			
			Character pwdChar = getPasswordChar(digest);
			if (pwdChar != null) {
				password.append(pwdChar);
			//	System.out.println(suffix);
				passCharIndex++;
			}
			
			suffix++;
		}
		
		return password.toString();
	}
	
	private Character getPasswordChar(byte[] digest) {
		if (digest[0] == 0 && digest[1] == 0 && (digest[2] >= 0 && digest[2] < 16)) {
			return Integer.toHexString(digest[2] & 0xFF).charAt(0);
		}
		
		return null;
	}
	
	public String createPasswordComplex(String seed) {
		int foundChars = 0;
		long suffix = 1;
		char[] password = new char[PASS_LEN];
		while(foundChars < PASS_LEN) {
			byte[] hashInput = (seed + suffix).getBytes();
			byte[] digest = md5.digest(hashInput); 
			Integer charIndex = getCharPosition(digest);
			
			// take first occurrence on index
			if (charIndex != null && password[charIndex] == 0) {
				password[charIndex] = getComplexPwdChar(digest);
				//System.out.println("ci: " + charIndex + " char: " + password[charIndex] + " all: " + Arrays.toString(password));
				foundChars++;
			}
			suffix++;
		}
		
		return new String(password);
	}
	
	private Integer getCharPosition(byte[] digest) {
		if (digest[0] == 0 && digest[1] == 0 && (digest[2] >= 0 && digest[2] < 8)) {
			return (int)digest[2];
		}
		
		return null;
	}
	
	private Character getComplexPwdChar(byte[] digest) {
		String hexaChar = Integer.toHexString(digest[3] & 0xFF);
		return hexaChar.length() == 1 ? '0' : hexaChar.charAt(0);
	}
	
	
}
