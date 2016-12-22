package org.tondo.advent2016.day3;

import java.util.Arrays;

public class TriangleBuffer {
	private static final int DIMENSION = 3;
	
	private int[][] buffer;
	private int recordsCount;
	private int columnTriangles;
	private int rowTriangles;
	
	public TriangleBuffer() {
		this.columnTriangles = 0;
		this.rowTriangles = 0;
		this.recordsCount = 0;
		this.buffer = new int[DIMENSION][];
	}
	
	
	public void addDimensions(int[] sides) {
		buffer[recordsCount] = Arrays.copyOf(sides, sides.length);
		recordsCount++;
		
		// part 1 processing
		if (isTriangle(sides)) {
			this.rowTriangles++;
		}
		
		// part 2 processing
		if (recordsCount == 3) {
			this.investigateBuffer();
			recordsCount = 0;
		}
	}
	
	public int getColumnValidTriangles() {
		return columnTriangles;
	}
	
	public int getRowValidTriangles() {
		return rowTriangles;
	}
	
	private void investigateBuffer() {
		for (int col = 0; col < DIMENSION; col++) {
			int[] dimensions = new int[DIMENSION];
			for (int row = 0; row < DIMENSION; row++) {
				dimensions[row] = buffer[row][col];
			}
			
			if (isTriangle(dimensions)) {
				this.columnTriangles++;
			}
		}
	}
	
	private boolean isTriangle(int[] sides) {
		for (int i = 0; i < 3; i++) {
			int a = sides[i];
			int b = sides[ (i + 1) % 3];
			int c = sides[ (i + 2) % 3];
			
			if ((a + b) <= c) {
				return false;
			}
		}
		return true;
	}
}
