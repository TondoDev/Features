package org.tondo.advent2016.day13;

import java.util.Comparator;

/**
 * 
 * @author TondoDev
 *
 */
public class MazeNode {

	private static class NodeComparator implements Comparator<MazeNode> {
		private  NodeComparator() {
		}
		
		@Override
		public int compare(MazeNode o1, MazeNode o2) {
			if (o1.getMovementCost() > o2.getMovementCost()) {
				return 1;
			} else if (o1.getMovementCost() < o2.getMovementCost()) {
				return -1;
			} else {
				return Integer.valueOf(o1.heuristic).compareTo(o2.heuristic);
			}
		}
	}
	
	public static final Comparator<MazeNode> COMPARATOR = new NodeComparator();
	

	private int movementCost;
	private int heuristic;
	private MazeNode parent;
	private Coord coord;
	
	public MazeNode(Coord coord) {
		this.coord = coord;
		this.movementCost = 0;
		this.heuristic = 0;
		this.parent = null;
	}
	
	
	public Coord getCoord() {
		return coord;
	}
	
	public MazeNode getParent() {
		return parent;
	}
	
	public void setParent(MazeNode parent) {
		this.parent = parent;
	}
	
	public int getMovementCost() {
		return movementCost;
	}
	
	public void setMovementCost(int movementCost) {
		this.movementCost = movementCost;
	}
	
	public int getHeuristic() {
		return heuristic;
	}
	
	public void setHeuristic(int heuristic) {
		this.heuristic = heuristic;
	}
	
	public int getFValue() {
		return this.movementCost + this.heuristic;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (!(obj instanceof MazeNode)) {
			return false;
		}
		MazeNode other = (MazeNode)obj;
		
		return this.coord.equals(other.coord);
	}
}
