package org.tondo.advent2016.run.day13;

import static org.junit.Assert.assertEquals;

import java.util.TreeSet;

import org.junit.Test;
import org.tondo.advent2016.day13.Coord;
import org.tondo.advent2016.day13.Maze;
import org.tondo.advent2016.day13.MazeNode;
import org.tondo.advent2016.day13.NodeEvaluator;

public class Day13Test {
	
	private static final int LUCKY = 1352;
	private static final int SAMPLE_LUCKY = 10;
	private static final Coord START = Coord.c(1, 1);
	private static final Coord END = Coord.c(31, 39);

	@Test
	public void testPart1() {
		Maze maze  = new Maze(LUCKY);
		int shortest = maze.findFewestSteps(START, END);
		System.out.println("Day 13 - Part 1: Fewest steps: " +  shortest); //90
	}
	
	@Test
	public void testSample() {
		Maze maze  = new Maze(SAMPLE_LUCKY);
		int shortest = maze.findFewestSteps(START, Coord.c(7, 4));
		assertEquals(11, shortest);
	}
	
	@Test
	public void testPrintSimpleMaze() {
		for (int y = 0; y < 40; y++) {
			for (int x = 0; x <40; x++) {
				System.out.print(NodeEvaluator.isWall(LUCKY, x, y) ? "#" : ".");
			}
			System.out.println();
		}
	}
	
	@Test
	public void testTreeset() {
		TreeSet<MazeNode> aaa = new TreeSet<>(MazeNode.COMPARATOR);
		aaa.add(new MazeNode(Coord.c(0, 0)));
		aaa.add(new MazeNode(Coord.c(0, 0)));
		aaa.add(new MazeNode(Coord.c(0, 0)));
		aaa.add(new MazeNode(Coord.c(0, 1)));
	}
}
