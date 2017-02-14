package org.tondo.advent2016.day13;

public class Maze {

	private final int luckyNumber;
	
	public Maze(int luckyNum) {
		this.luckyNumber = luckyNum;
	}
	
	public int findFewestSteps(Coord start, Coord end) {
		for (int y = 0; y < 7; y++) {
			for (int x = 0; x <10; x++) {
				System.out.print(isWall(x, y) ? "#" : ".");
			}
			System.out.println();
		}
		
		return 0;
	}
	
	private boolean isWall(int x, int y) {
		Long expr = (long) (x*x + 3*x + 2*x*y + y + y*y);
		expr += this.luckyNumber;
		
		String binaryString = Long.toBinaryString(expr);
		int onesCnt = 0;
		for (int i =0; i < binaryString.length(); i++) {
			if (binaryString.charAt(i) == '1') {
				onesCnt++;
			}
		}
		
		return (onesCnt % 2) != 0; 
	}
}
