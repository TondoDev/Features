package org.tondo.advent2016.day8;

/**
 * 
 * @author TondoDev
 *
 */
public class TinyScreen {

	private static final int COLS = 50;
	private static final int ROWS = 6;
	private static final byte LIT = 0x0F;
	
	private byte[][] pixels;
	
	
	public TinyScreen() {
		pixels = new byte[ROWS][COLS];
	}
	
	public int getLitPixelsCount() {
		int count = 0;
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				if (pixels[r][c] == LIT) {
					count++;
				}
			}
		}
		return count;
	}
	
	public void rect(int width, int height) {
		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				pixels[r][c] = LIT;
			}
		}
	}
	
	public void rotateCol(int col, int rotateCount) {
		int rotate = rotateCount % ROWS;
		// nothing to do
		if (rotate == 0) {
			return;
		}
		
		for (int cnt = 0; cnt < rotate; cnt++) {
			byte lastItem =  this.pixels[ROWS - 1][col];
			for (int r = ROWS - 1; r > 0 ; r--) {
				this.pixels[r][col] = this.pixels[r - 1][col];
			}
			this.pixels[0][col] = lastItem;
		}
	}
	
	public void rotateRow(int row, int rotateCount) {
		int rotate = rotateCount % COLS;
		// nothing to do
		if (rotate == 0) {
			return;
		}
		
		for (int cnt = 0; cnt < rotate; cnt++) {
			byte lastItem =  this.pixels[row][COLS - 1];
			for (int c = COLS - 1; c > 0 ; c--) {
				this.pixels[row][c] = this.pixels[row][c - 1];
			}
			this.pixels[row][0] = lastItem;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (int r = 0; r <ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				if (this.pixels[r][c] == LIT) {
					builder.append('#');
				} else {
					builder.append('.');
				}
			}
			builder.append("\n");
		}
		return builder.toString();
	}
}
