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
	private int stretchCount;
	private MessageDigest digest;
	
	
	public Hasher(String salt, int strech) {
		this.salt = salt;
		this.stretchCount = strech;
		
		try {
			this.digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 algorithm not available", e);
		}
	}
	public Hasher(String salt) {
		this(salt, 0);
	}
	
	public String computeHashForIndex(long index) {
		String stringHex = computeHashForString(this.salt + index);
		for (int i = 0; i < this.stretchCount; i++) {
			stringHex = computeHashForString(stringHex);
		}
		return stringHex;
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
	
	public String computeHashForString(String value) {
		byte[] binaryHash = digest.digest(value.getBytes());
		return byteArrayToString(binaryHash);
	}
	
	
}
