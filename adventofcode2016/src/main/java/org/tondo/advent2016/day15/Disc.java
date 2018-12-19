package org.tondo.advent2016.day15;

public class Disc {

	private int initialPos;
	private int numOfPositions;
	private int order;
	private int loopOffset;

	public Disc(int num, int init, int order) {
		this.numOfPositions = num;
		this.initialPos = init;
		this.order = order;
		this.loopOffset = (this.numOfPositions - this.initialPos) - this.order;
	}
	
	public int getInitialPos() {
		return initialPos;
	}
	public int getNumOfPositions() {
		return numOfPositions;
	}
	public int getOrder() {
		return order;
	}
	public int getLoopOffset() {
		return loopOffset;
	}
}
