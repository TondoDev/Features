package org.tondo.advent2016.day13;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Maze {

	private final int luckyNumber;
	private OpenList openList;
	private Set<Coord> closedList;
	
	public Maze(int luckyNum) {
		this.luckyNumber = luckyNum;
	}
	
	public int findFewestSteps(Coord start, Coord end) {
		this.openList = new OpenList();
		this.closedList = new HashSet<>();
		NodeEvaluator evaluator = new NodeEvaluator(end, this.luckyNumber, closedList);
		this.openList.addNote(new MazeNode(start));
		
		MazeNode finalNode = null;
		while(!openList.isEmpty()) {
			MazeNode current = openList.popLowest();
			this.closedList.add(current.getCoord());
			List<MazeNode> surround = evaluator.evaluateSurroundings(current);
			finalNode = processSurround(surround);
			if (finalNode != null) {
				break;
			}
		}
		
		return finalNode == null ? -1 : finalNode.getMovementCost();
	}
	
	
	public int countReachableNodes(Coord start, int maxSteps) {
		LinkedList<MazeNode> queue = new LinkedList<>(); 
		this.closedList = new HashSet<>();
		NodeEvaluator evaluator = new NodeEvaluator(start, this.luckyNumber, closedList);
		queue.add(new MazeNode(start));
		while (!queue.isEmpty()) {
			MazeNode current = queue.removeFirst();
			this.closedList.add(current.getCoord());
			List<MazeNode> surround = evaluator.evaluateSurroundings(current);
			
			for (MazeNode node : surround) {
				if (node.getMovementCost() < maxSteps) {
					queue.addLast(node);
				}
				closedList.add(node.getCoord());
			}
		}
		return closedList.size();
	}
	
	
	/**
	 *	Returns final node if found! 
	 */
	private MazeNode processSurround(List<MazeNode> surround) {
		for (MazeNode processed : surround) {
			if (processed.getHeuristic() == 0) {
				return processed;
			}
			
			if (this.openList.containsNodeAt(processed.getCoord())
					&& this.openList.getFValueAt(processed.getCoord()) > processed.getFValue()) {
				this.openList.popNodeAt(processed.getCoord());
			} 
			this.openList.addNote(processed);
		}
		return null;
	}
	
}
