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
			if (o1.getFValue() > o2.getFValue()) {
				return 1;
			} else if (o1.getFValue() < o2.getFValue()) {
				return -1;
			} else {
				// little fake, because treeSet can't store object which are equals
				// according to this comparator. So equals can be only when coordinates are same(necessary condition).
				// Other cases can't be equal by other metrics.
				int comRes = Integer.valueOf(o1.heuristic).compareTo(o2.heuristic);
				return comRes != 0 ? comRes : o1.coord.equals(o2.coord) ? 0 : -1;
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
	
	@Override
	public int hashCode() {
		return this.coord.hashCode();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.movementCost).append("  ").append(this.heuristic).append("\n");
		sb.append("  ").append(this.getFValue()).append("  \n");
		sb.append(this.coord).append("\n");
		return sb.toString();
	}
}
