package org.tondo.advent2016.day1;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
	
	public Navigator() {
		this.startX = 0;
		this.startY = 0;
		
		this.currX = 0;
		this.currY = 0;
		this.currDirection = 'N';
	}
	
	public void processStep(Coordinate step) {
		this.currDirection = ROTATION.get("" + this.currDirection + step.getDirection());
		//System.out.println(step.getDirection() + "" + step.getSteps());
		if (currDirection == 'N') {
			currY += step.getSteps();
		} else if (currDirection == 'S') {
			currY -= step.getSteps();
		} else if (currDirection == 'W') {
			currX -= step.getSteps();
		} else { //east
			currX += step.getSteps();
		}
	}
	
	public int getDistance() {
		return Math.abs((currX - startX)) +  Math.abs((currY - startY));
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
