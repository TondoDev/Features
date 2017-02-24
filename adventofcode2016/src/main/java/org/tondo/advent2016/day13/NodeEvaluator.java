package org.tondo.advent2016.day13;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author TondoDev
 *
 */
public class NodeEvaluator {
	private static final int[][] SURROUNDING = {
			{-1,0},
			{1, 0},
			{0, -1},
			{0, 1}
	};
	
	private Coord endState;
	private int luckyNumber;
	private Set<Coord> closedList;
	
	public NodeEvaluator(Coord endState, int lucky, Set<Coord> closedList) {
		this.endState = endState;
		this.luckyNumber = lucky;
		this.closedList = Collections.unmodifiableSet(closedList);
	}
	
	public List<MazeNode> evaluateSurroundings(MazeNode currentNode) {
	List<MazeNode> retVal = new ArrayList<>();
		Coord currentCoord = currentNode.getCoord();
		for (int[] dir : SURROUNDING) {
			int x = currentCoord.getX() + dir[0];
			int y = currentCoord.getY() + dir[1];
			if (x >=0 && y >= 0 && !isWall(this.luckyNumber, x, y)) {
				Coord newC = Coord.c(x, y);
				if (!closedList.contains(newC)) {
					MazeNode node = new MazeNode(newC);
					node.setParent(currentNode);
					node.setMovementCost(currentNode.getMovementCost() + 1);
					node.setHeuristic(manhattanDistance(newC, this.endState));
					retVal.add(node);
				}
			}
		}
		
		return retVal;
	}
	
	public static boolean isWall(int luckyNumber, int x, int y) {
		Long expr = (long) (x*x + 3*x + 2*x*y + y + y*y);
		expr += luckyNumber;
		
		String binaryString = Long.toBinaryString(expr);
		int onesCnt = 0;
		for (int i =0; i < binaryString.length(); i++) {
			if (binaryString.charAt(i) == '1') {
				onesCnt++;
			}
		}
		
		return (onesCnt % 2) != 0; 
	}
	
	public static int manhattanDistance(Coord one, Coord two) {
		return Math.abs(one.getX() - two.getX()) + Math.abs(one.getY() - two.getY());
	}
	
}
