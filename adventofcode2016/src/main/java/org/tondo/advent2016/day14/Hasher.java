package org.tondo.advent2016.day14;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author TondoDev
 *
 */
public class Hasher {

	private String salt;
	private MessageDigest digest;
	
	public Hasher(String salt) {
		this.salt = salt;
		
		try {
			this.digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 algorithm not available", e);
		}
	}
	
	public String computeHashForIndex(long index) {
		String tohash = this.salt + index;
		byte[] binaryHash = digest.digest(tohash.getBytes());
		return byteArrayToString(binaryHash);
	}
	
	public String byteArrayToString(byte[] arr) {
		StringBuilder sb = new StringBuilder(32);
		for (int i = 0; i < arr.length; i++) {
			int v = arr[i] & 0xFF;
			String sv = Integer.toHexString(v);
			sb.append(v < 16 ? ("0" + sv) : sv);
		}
		return sb.toString();
	}
}
