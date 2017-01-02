package org.tondo.advent2016.day7;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 
 *
 */
public class TlsProcessor {

	private int tlsSupportedCount = 0;
	private static final Pattern SIGNATURE = Pattern.compile("(.)(.)\\2\\1");
	
	public void processIP(String ipv7) {
		if (isTlsSupported(ipv7)) {
			this.tlsSupportedCount++;
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
}
