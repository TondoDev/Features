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
		while (passCharIndex < 8) {
			byte[] hashInput = (seed + suffix).getBytes();
			byte[] digest = md5.digest(hashInput);
			
			Character pwdChar = getPasswordChar(digest);
			if (pwdChar != null) {
				password.append(pwdChar);
				System.out.println(suffix);
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
	
	public String createPasswordComplex() {
		return null;
	}
	
	
}
