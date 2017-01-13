package org.tondo.advent2016.day9;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * 
 *  @author TondoDev
 */
public class Decompressor {
	
	private enum MarkState {
		STARTF,
		FIRST,
		STARTS,
		SECOND
	}
	
	private static class Mark {
		private int charsCount;
		private int repeatCount;
		private int readedData;
		final boolean ok;
		
		public Mark(int cc, int rc) {
			this.charsCount = cc;
			this.repeatCount = rc;
			this.readedData = 0;
			ok = true;
		}
		
		public Mark(int readed) {
			this.readedData = readed;
			ok = false;
		}
		
		public boolean isOk() {
			return ok;
		}
		
		public int getCharsCount() {
			return charsCount;
		}
		public int getRepeatCount() {
			return repeatCount;
		}
		
		public int getReadedData() {
			return readedData;
		}
	}

	private long decompressedLen = 0;
	
	public void decompress(Reader reader) throws IOException {
		
		int readChar = 0;
		while((readChar = reader.read()) != -1) {
			char c = (char) readChar;
			
			if (c == '(') {
				Mark mark = readComressionMark(reader);
				if (mark.isOk()) {
					this.decompressedLen += expend(mark, reader);
				} else {
					// incorrect syntax in mark is treated as standard data
					// count also opening bracket.
					this.decompressedLen += (mark.getReadedData() + 1);
				}
			} else if (!Character.isWhitespace(c)) {
				// ignoring whitespace
				this.decompressedLen++;
			}
		}
	}
	
	private int expend(Mark compressMark, Reader reader) throws IOException {
		if (compressMark.getCharsCount() == 0) {
			return 0;
		}
		
		int readed = 0;
		while (readed < compressMark.getCharsCount() && reader.read() != -1) {
			readed++;
		}
		
		return readed * compressMark.getRepeatCount();
	}
	
	private Mark readComressionMark(Reader reader) throws IOException {
		StringBuilder token = new StringBuilder();
		
		MarkState state = MarkState.STARTF;
		int data = 0;
		int readedCnt = 0;
		int chars = 0;
		int reps = 0;
		while ((data = reader.read()) != -1) {
			readedCnt++;
			char c = (char)data;
			
			if (Character.isDigit(c) && (state == MarkState.STARTF || state == MarkState.FIRST)) {
				state = MarkState.FIRST;
				token.append(c);
			} else if (c == 'x' && state == MarkState.FIRST) {
				try {
					chars = Integer.valueOf(token.toString()); 
					token = new StringBuilder();
				} catch (NumberFormatException e) {
					return new Mark(readedCnt);
				}
				state = MarkState.STARTS;
			} else if (Character.isDigit(c) && (state == MarkState.STARTS || state == MarkState.SECOND)) {
				state = MarkState.SECOND;
				token.append(c);
			} else if (c == ')' && state == MarkState.SECOND) {
				try {
					reps = Integer.valueOf(token.toString()); 
					return new Mark(chars, reps);
				} catch (NumberFormatException e) {
					return new Mark(readedCnt);
				}
			} else {
				return new Mark(readedCnt);
			}
		}
		
		return new Mark(readedCnt);
	}
	
	public void decompressRecursive(Reader reader) throws IOException {
		this.decompressedLen = decompressRecursiveInternal(reader, decompressedLen);
	}
	
	private long decompressRecursiveInternal(Reader reader, long currenLen) throws IOException {
		int readChar = 0;
		while((readChar = reader.read()) != -1) {
			char c = (char) readChar;
			
			if (c == '(') {
				Mark mark = readComressionMark(reader);
				if (mark.isOk()) {
					currenLen = recursiveExpand(mark, reader, currenLen);
				} else {
					// incorrect syntax in mark is treated as standard data
					// count also opening bracket.
					currenLen += (mark.getReadedData() + 1);
				}
			} else if (!Character.isWhitespace(c)) {
				// ignoring whitespace
				currenLen++;
			}
		}
		
		return currenLen;
	}
	
	
	private long recursiveExpand(Mark compressMark, Reader reader, long currenLen) throws IOException {
		if (compressMark.getCharsCount() == 0) {
			return 0;
		}
		
		int readed = 0;
		int data = 0;
		StringBuilder buffer = new StringBuilder();
		while (readed < compressMark.getCharsCount() && (data = reader.read()) != -1) {
			readed++;
			buffer.append((char)data);
		}
		
		return compressMark.getRepeatCount() * decompressRecursiveInternal(new StringReader(buffer.toString()), 0L) + currenLen;
	}
	
	public long getDecompressedLen() {
		return decompressedLen;
	}
}
