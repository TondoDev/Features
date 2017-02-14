package org.tondo.advent2016.run.day13;

import org.junit.Test;
import org.tondo.advent2016.day13.Coord;
import org.tondo.advent2016.day13.Maze;

public class Day13Test {
	
	private static final int LUCKY = 1352;
	private static final Coord START = Coord.c(1, 1);
	private static final Coord END = Coord.c(7,  4);

	@Test
	public void testPart1() {
		Maze maze  = new Maze(10);
		int shortest = maze.findFewestSteps(START, END);
		System.out.println("Day 13 - Part 1: Fewest steps: " +  shortest);
	}
}
