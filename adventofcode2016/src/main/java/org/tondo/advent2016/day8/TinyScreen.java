package org.tondo.advent2016.day8;

/**
 * 
 * @author TondoDev
 *
 */
public class TinyScreen {

	private static final int COLS = 10;//50
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
	
	public void rotateCol(int col, int pixels) {
		int rotate = pixels % ROWS;
		// nothing to do
		if (rotate == 0) {
			return;
		}
		byte buff = this.pixels[0][col];
		int bi = 0;
		
		for (int r = 0; r < ROWS; r++) {
			int dest = (bi + rotate) % ROWS;
			byte tmp = this.pixels[dest][col];
			this.pixels[dest][col] = buff;
			buff = tmp;
			bi = dest;
		}
	}
	
	public void ratateRow(int row, int pixels) {
		
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
		// TODO Auto-generated method stub
		return builder.toString();
	}
}
