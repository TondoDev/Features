package org.tondo.advent2016.day7;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author TondoDev
 *
 */
public class TlsProcessor {
	
	private static final int WINDOW_SIZE = 3;

	private int tlsSupportedCount = 0;
	private int sslSupportedCount = 0;
	
	private static final Pattern SIGNATURE = Pattern.compile("(.)(.)\\2\\1");
	
	public void processIP(String ipv7) {
		if (isTlsSupported(ipv7)) {
			this.tlsSupportedCount++;
		}
		
		if (isSslSupported(ipv7)) {
			this.sslSupportedCount++;
		}
	}
	
	public boolean isTlsSupported(String ipv7) {
		
		StringBuilder tokenBuffer = new StringBuilder();
		int len = ipv7.length();
		boolean foundSignature = false;
		boolean inBracketSignature = false;
		
		for (int i = 0; i < len; i++) {
			char data = ipv7.charAt(i);
			if (data == '[') {
				foundSignature =  foundSignature || hasTlsSignature(tokenBuffer.toString());
				tokenBuffer = new StringBuilder();
			} else if (data == ']') {
				if (hasTlsSignature(tokenBuffer.toString())) {
					inBracketSignature = true;
					break;
				}
				tokenBuffer = new StringBuilder();
			} else {
				tokenBuffer.append(data);
			}
		}
		
		if (!foundSignature && !inBracketSignature && tokenBuffer.length() > 0) {
			foundSignature =  foundSignature || hasTlsSignature(tokenBuffer.toString());
		}
		
		return foundSignature && !inBracketSignature;
	}
	
	public boolean isSslSupported(String ipv7) {
		StringBuilder tokenBuffer = new StringBuilder();
		int len = ipv7.length();
		boolean found = false;
		Set<String> supernet = new HashSet<>();
		Set<String> hypertext = new HashSet<>();
		
		for (int i = 0; i < len; i++) {
			char data = ipv7.charAt(i);
			if (data == '[') {
				Set<String> signatures =  getABASignatures(tokenBuffer.toString());
				if (!signatures.isEmpty()) {
					found = found || containsBAB(hypertext, signatures);
				}
				supernet.addAll(signatures);
				tokenBuffer = new StringBuilder();
			} else if (data == ']') {
				Set<String> signatures =  getABASignatures(tokenBuffer.toString());
				if (!signatures.isEmpty()) {
					found = found || containsBAB(supernet, signatures);
				}
				hypertext.addAll(signatures);
				tokenBuffer = new StringBuilder();
			} else {
				tokenBuffer.append(data);
			}
			
			if (found) {
				return true;
			}
		}
		
		return tokenBuffer.length() > 0 ? containsBAB(hypertext, getABASignatures(tokenBuffer.toString())) :false;
	}
	
	private Set<String> getABASignatures(String token) {
		Set<String> aba = new HashSet<>();
		char[] window = new char[WINDOW_SIZE];
		int len = token.length();
		
		for (int i = 0; i < len; i++) {
			window[0] = window[1];
			window[1] = window[2];
			window[2] = token.charAt(i);
			
			if (window[0] == window[2] && window[0] != window[1]) {
				aba.add(new String(window));
			}
		}
		
		return aba;
	}
	
	private boolean containsBAB(Set<String> target, Set<String> toReverse) {
		for (String item : toReverse) {
			char bound = item.charAt(1);
			String reversed = "" + bound + item.charAt(0) + bound;
			if (target.contains(reversed)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean hasTlsSignature(String token) {
		Matcher m = SIGNATURE.matcher(token);
		boolean found = false;
		while (m.find() && !found) {
			found = found || !m.group(1).equals(m.group(2));
		}
		
		return found;
	}
	
	public int getTlsSupportedCount() {
		return tlsSupportedCount;
	}
	
	public int getSslSupportedCount() {
		return sslSupportedCount;
	}
}
