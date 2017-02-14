package org.tondo.advent2016.day13;

public class Coord {

	private int x;
	private int y;
	
	private Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public static Coord c(int x, int y) {
		return new Coord(x, y);
	}
	
}
