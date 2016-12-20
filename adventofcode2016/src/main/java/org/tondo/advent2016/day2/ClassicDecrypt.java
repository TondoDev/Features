package org.tondo.advent2016.day2;

/**
 * 
 * @author TondoDev
 *
 */
public class ClassicDecrypt extends Decryptor{
	
	private int currentPosition;
	
	public ClassicDecrypt() {
		this.currentPosition = 5;
	}

	public void decryptLine(String instructions) {
		this.currentPosition = decrypt(instructions, this.currentPosition);
		storeChar((char)(this.currentPosition + '0'));
	}
	
	private int decrypt(String instructions, int pos) {
		int newPos = pos;
		int len = instructions.length();
		for (int i = 0; i < len; i++) {
			char c = instructions.charAt(i);
			validateDirection(c);
			
			if (c == 'U') {
				int v =  newPos - 3;
				if (v > 0) {
					newPos = v;
				}
			} else if ( c == 'D') {
				int v =  newPos + 3;
				if (v < 10) {
					newPos = v;
				}
			} else if ( c == 'L') {
				int v =  newPos - 1;
				if (v % 3 != 0) {
					newPos = v;
				}
			}  else if ( c == 'R') {
				int v =  newPos + 1;
				if (v % 3 != 1) {
					newPos = v;
				}
			}
			// else should never happened
		}
		
		return newPos;
	}
}
