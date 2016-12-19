package org.tondo.advent2016.day1;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Day 1, AoC 2016
 * 
 * @author TondoDev
 *
 */
public class Navigator {
	
	
	public static class Coordinate {
		private char direction;
		private int steps;
		
		private Coordinate(char d, int s) {
			this.direction = d;
			this.steps = s;
		}
		
		public char getDirection() {
			return direction;
		}
		
		public int getSteps() {
			return steps;
		}
		
	}
	
	private static final Map<String, Character> ROTATION;
	static {
		Map<String, Character> tmp = new HashMap<>();
		tmp.put("NL", 'W');
		tmp.put("EL", 'N');
		tmp.put("SL", 'E');
		tmp.put("WL", 'S');
		tmp.put("NR", 'E');
		tmp.put("ER", 'S');
		tmp.put("SR", 'W');
		tmp.put("WR", 'N');
		ROTATION = Collections.unmodifiableMap(tmp);
	}
	
	private int startX;
	private int startY;
	
	private Character currDirection;
	private int currX;
	private int currY;
	
	private Set<String> locationCache;
	// distance to location first visited twice
	private int bunnyLocationDistance = -1;
	
	public Navigator() {
		this.startX = 0;
		this.startY = 0;
		
		this.currX = 0;
		this.currY = 0;
		this.currDirection = 'N';
		locationCache = new HashSet<>();
		
	}
	
	private void markVisited(int sx, int sy, int ex, int ey) {
		if (this.bunnyLocationDistance < 0) {
			int ymul = sy > ey ? -1 : 1;
			int xmul = sx > ex ? -1 : 1;
			
			for (int yy = sy; yy != ey; yy = yy + ymul) {
				if(!this.locationCache.add(sx+","+yy)) {
					this.bunnyLocationDistance = Math.abs((sx - startX)) +  Math.abs((yy - startY));
					return;
				}
			}
			
			for (int xx = sx; xx != ex; xx = xx + xmul) {
				if(!this.locationCache.add(xx+","+sy)) {
					this.bunnyLocationDistance = Math.abs((xx - startX)) +  Math.abs((sy - startY));
					return;
				}
			}
		}
	}
	
	public void processStep(Coordinate step) {
		this.currDirection = ROTATION.get("" + this.currDirection + step.getDirection());
		int nx = currX;
		int ny = currY;
		//System.out.println(step.getDirection() + "" + step.getSteps());
		if (currDirection == 'N') {
			ny = currY + step.getSteps();
		} else if (currDirection == 'S') {
			ny = currY - step.getSteps();
		} else if (currDirection == 'W') {
			nx = currX - step.getSteps();
		} else { //east
			nx = currX + step.getSteps();
		}
		
		markVisited(currX, currY, nx, ny);
		currX = nx;
		currY = ny;
	}
	
	public int getDistance() {
		return Math.abs((currX - startX)) +  Math.abs((currY - startY));
	}
	
	public int getBunnyLocationDistance() {
		if (bunnyLocationDistance < 0) {
			throw new IllegalStateException("Bunny location not found yet!");
		}
		return bunnyLocationDistance;
	}
	
	public static Coordinate parse(String str) {
		if (str == null || str.isEmpty()) {
			throw new IllegalArgumentException("Coordinate can't be empty string!");
		}
		
		char dir = str.charAt(0);
		
		if (dir != 'L' && dir != 'R') {
			throw new IllegalArgumentException("'" + dir + "' is not supproted direction!");
		}
		
		int steps = 0;
		try {
			steps = Integer.parseInt(str.substring(1));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Expected numeric value for number of steps!");
		}
		
		return new Coordinate(dir, steps);
	}

}
