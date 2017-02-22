package org.tondo.advent2016.day13;

import java.util.Objects;

public final class Coord {

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
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof Coord)) {
			return false;
		}
		
		Coord other = (Coord)obj;
		return this.x == other.x && this.y == other.y;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.x, this.y);
	}
	
}
