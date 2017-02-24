package org.tondo.advent2016.day13;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * 
 * @author TondoDev
 *
 */
public class OpenList {

	private TreeSet<MazeNode> orderedList;
	private Map<Coord, MazeNode> index;
	
	public OpenList() {
		this.orderedList = new TreeSet<>(MazeNode.COMPARATOR);
		this.index = new HashMap<>();
	}
	
	public boolean isEmpty() {
		return this.orderedList.isEmpty();
	}
	
	public MazeNode popLowest() {
		if (this.orderedList.isEmpty()) {
			throw new IllegalStateException("List is empty!");
		}
		MazeNode lowest = this.orderedList.pollFirst();
		this.index.remove(lowest.getCoord());
		return lowest;
	}
	
	public boolean containsNodeAt(Coord coord) {
		return this.index.containsKey(coord);
	}
	
	public MazeNode popNodeAt(Coord coord) {
		MazeNode popped = safeNodeGet(coord);
		
		this.orderedList.remove(popped);
		return popped;
	}
	
	/**
	 * optimisation to prevent re-inserting nodes
	 */
	public int getFValueAt(Coord coord) {
		MazeNode popped = safeNodeGet(coord);
		return popped.getFValue();
	}
	
	public void addNote(MazeNode node) {
		this.orderedList.add(node);
		this.index.put(node.getCoord(), node);
	}
	
	private MazeNode safeNodeGet(Coord coord) {
		MazeNode popped = this.index.get(coord);
		if (popped == null) {
			throw new IllegalStateException("Node at " + coord + " doesn't exists!");
		}
		return popped;
	}
}
