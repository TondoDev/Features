package org.tondo.advent2016.day2;

/**
 * 
 * @author TondoDev
 *
 */
public class DiamondDecryptor extends Decryptor {
	
	private static final char[][] KEYBOARD = {
				{' ', ' ', '1', ' ', ' '},
				{' ', '2', '3', '4', ' '},
				{'5', '6', '7', '8', '9'},
				{' ', 'A', 'B', 'C', ' '},
				{' ', ' ', 'D', ' ', ' '}
				};
	
	private int xPos;
	private int yPos;
	private static final int MAX = 5;
	
	public DiamondDecryptor() {
		this.xPos = 0;
		this.yPos = 2;
	}
	
	public void decryptLine(String line) {
		int len = line.length();
		for (int i = 0; i < len; i ++) {
			char c = line.charAt(i);
			validateDirection(c);
			
			if (c == 'U') {
				int ty = this.yPos - 1;
				if (ty >= 0 && KEYBOARD[ty][this.xPos] != ' ') {
					this.yPos--;
				}
			} else if (c == 'D') {
				int ty = this.yPos + 1;
				if (ty < MAX && KEYBOARD[ty][this.xPos] != ' ') {
					this.yPos++;
				}
			}  else if (c == 'R') {
				int tx = this.xPos + 1;
				if (tx < MAX && KEYBOARD[this.yPos][tx] != ' ') {
					this.xPos++;
				}
			} else if (c == 'L') {
				int tx = this.xPos - 1;
				if (tx >= 0 && KEYBOARD[this.yPos][tx] != ' ') {
					this.xPos--;
				}
			}
		}
		storeChar(KEYBOARD[this.yPos][this.xPos]);
	}
}
