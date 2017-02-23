package org.tondo.advent2016.day13;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Maze {

	private final int luckyNumber;
	
	private static final int[][] SURROUNDING = {
			{-1,0},
			{1, 0},
			{0, -1},
			{0, 1}
	};
	
	private TreeSet<MazeNode> openList;
	
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
		
		this.openList = new TreeSet<>(MazeNode.COMPARATOR);
		this.openList.add(new MazeNode(start));
		
		MazeNode finalNode = null;
		while(!openList.isEmpty()) {
			MazeNode current = openList.pollFirst();
			List<MazeNode> surround = getSurrounding(current.getCoord());
			finalNode = processSurround(surround, end);
			if (finalNode != null) {
				break;
			}
		}
		
		return finalNode == null ? -1 : finalNode.getMovementCost();
	}
	
	
	/**
	 *	Returns final node if found! 
	 */
	private MazeNode processSurround(List<MazeNode> surround, Coord endCoord) {
		for (MazeNode processed : surround) {
			if (manhattanDistance(processed.getCoord(), endCoord) == 1) {
				
			}
		}
		return null;
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
	
	
	private List<MazeNode> getSurrounding(Coord currentNode) {
		List<MazeNode> retVal = new ArrayList<>();
		
		for (int[] dir : SURROUNDING) {
			int x = currentNode.getX() + dir[0];
			int y = currentNode.getY() + dir[1];
			if (x >=0 && y >= 0 && !isWall(x, y)) {
				retVal.add(new MazeNode(Coord.c(x, y)));
			}
		}
		
		return retVal;
	}
	
	public int manhattanDistance(Coord one, Coord two) {
		return Math.abs(one.getX() - two.getX()) + Math.abs(one.getY() - two.getY());
	}
}
