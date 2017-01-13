package org.tondo.advent2016.day8;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author TondoDev
 *
 */
public class InsctructionDecoder {
	
	private static class RectInstr implements ScreenInstruction {
		private int width;
		private int height;
		
		public RectInstr(int w, int h) {
			this.width = w;
			this.height = h;
		}

		@Override
		public void execute(TinyScreen screer) {
			screer.rect(width, height);
		}
	}
	
	private static class ColRotation implements ScreenInstruction {
		private int col;
		private int pixels;
		
		public ColRotation(int c, int p) {
			this.col = c;
			this.pixels = p;
		}

		@Override
		public void execute(TinyScreen screen) {
			screen.rotateCol(col, pixels);
		}
	}
	
	private static class RowRotation implements ScreenInstruction {
		
		private int row;
		private int pixels;
		
		public RowRotation(int r, int p) {
			this.row = r;
			this.pixels = p;
		}

		@Override
		public void execute(TinyScreen screen) {
			screen.rotateRow(row, pixels);
		}
	}
	
	private static final Pattern REACT = Pattern.compile("^rect ([1-9][0-9]*)x([1-9][0-9]*)$");
	private static final Pattern ROTATE_COL = Pattern.compile("^rotate column x=([0-9][0-9]*) by ([1-9][0-9]*)$");
	private static final Pattern ROTATE_ROW = Pattern.compile("^rotate row y=([0-9][0-9]*) by ([1-9][0-9]*)$");

	
	public ScreenInstruction decode(String instructionRaw) {
		String trimed = instructionRaw.trim();
		Matcher m = REACT.matcher(trimed);
		if (m.find()) {
		
			return new RectInstr(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
		}
		
		m = ROTATE_ROW.matcher(trimed);
		if (m.find()) {
			return new RowRotation(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
		}
		
		m = ROTATE_COL.matcher(trimed);
		if(m.find()) {
			return new ColRotation(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
		}
		
		throw new IllegalArgumentException("Invalid instruction code: " + instructionRaw);
	}
}
