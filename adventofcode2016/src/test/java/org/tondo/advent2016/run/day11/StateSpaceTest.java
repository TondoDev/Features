package org.tondo.advent2016.run.day11;

import org.junit.Test;
import org.tondo.advent2016.day11.FloorState;
import org.tondo.advent2016.day11.StateSpace;

import static org.junit.Assert.*;

public class StateSpaceTest {
	
	@Test
	public void testFloorStateBuilder() {
		FloorState begin = new FloorStateBuilder(1)
				.floor(1, "1m", "1g")
				.build();
		System.out.println(begin);
	}
	
	@Test
	public void testSimplestCase() {
		FloorState begin = new FloorStateBuilder(1)
				.floor(1, "1m")
				.build();
		
		FloorState end = new FloorStateBuilder(4)
				.floor(4, "1m")
				.build();
		
		StateSpace stateSpace = new StateSpace(begin);
		int minMoves = stateSpace.findMinimalSteps(end);
		assertEquals(3, minMoves);
	}
	
	@Test
	public void testSimpleCase() {
		FloorState begin = new FloorStateBuilder(1)
				.floor(1, "1m", "1g")
				.build();
		
		FloorState end = new FloorStateBuilder(4)
				.floor(4, "1m", "1g")
				.build();
		
		StateSpace stateSpace = new StateSpace(begin);
		int minMoves = stateSpace.findMinimalSteps(end);
		assertEquals(3, minMoves);
	}
	
	@Test
	public void testNoSolution() {
		FloorState begin = new FloorStateBuilder(2)
				.floor(1, "1m", "1g")
				.build();
		
		FloorState end = new FloorStateBuilder(4)
				.floor(4, "1m", "1g")
				.build();
		
		StateSpace stateSpace = new StateSpace(begin);
		int minMoves = stateSpace.findMinimalSteps(end);
		assertEquals(-1, minMoves);
	}
	
}
