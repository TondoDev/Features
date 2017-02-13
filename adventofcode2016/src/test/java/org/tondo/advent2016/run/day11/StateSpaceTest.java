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
	
	@Test
	public void testBasicHomomorph() {
		FloorState begin = new FloorStateBuilder(1)
				.floor(4, "1m")
				.build();
		
		FloorState end = new FloorStateBuilder(4)
				.floor(4, "1m")
				.build();
		
		assertTrue("Equal are homomorphic too", begin.isHomomorphic(end));
		
		end = new FloorStateBuilder(4)
					.floor(4, "4m")
					.build();
		assertTrue("Same device, different element", begin.isHomomorphic(end));
		
		end = new FloorStateBuilder(4)
				.floor(4, "4g")
				.build();
		assertFalse("Different device is not homom.", begin.isHomomorphic(end));
	}
	
	@Test
	public void testComplexHomo() {
		FloorState begin = new FloorStateBuilder(1)
				.floor(4, "1m", "2g")
				.floor(3, "1g", "3g")
				.floor(2, "3m", "2m")
				.floor(1, "4m", "4g")
				.build();
		
		FloorState end = new FloorStateBuilder(1)
				.floor(4, "3m", "4g")
				.floor(3, "3g", "1g")
				.floor(2, "1m", "4m")
				.floor(1, "2m", "2g")
				.build();
		
		assertTrue(begin.isHomomorphic(end));
	}
	
}
