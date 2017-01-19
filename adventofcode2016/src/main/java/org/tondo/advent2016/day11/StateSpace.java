package org.tondo.advent2016.day11;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author TondoDev
 *
 */
public class StateSpace {
	
	private static class InternalState {
		int steps;
		FloorState curr;
		InternalState prev;
		
		public InternalState(FloorState c, InternalState p, int s) {
			this.steps = s;
			this.curr = c;
			this.prev = p;
		}
	}
	
	private FloorState initialState;
	private FloorState endState;
	private LinkedList<InternalState> fifo;
	private LinkedList<FloorState> minimalPath;
	
	
	public StateSpace(FloorState init) {
		this.initialState = init;
	}
	
	
	public int findMinimalSteps(FloorState endState) {
		
		this.minimalPath = new LinkedList<>();
		if (initialState.equals(endState)) {
			this.minimalPath.add(initialState);
			return 0;
		}
		
		this.fifo = new LinkedList<>();
		int minimum = -1;
		while(!this.fifo.isEmpty()) {
			InternalState processing = this.fifo.removeFirst();
			if (endState.equals(processing)) {
				if (minimum < 0 || minimum > processing.steps) {
					minimum = processing.steps;
					constructMinPath(processing, minimalPath);
				}
			} else {
				this.fifo.addAll(generateNextStates(processing));
			}
			
		}
		
		return 0;
	}
	
	private List<InternalState> generateNextStates(InternalState currState) {
		List<InternalState> rv = new ArrayList<>();
		
		if (currState.curr.getElevatorFloor() < 4) {
			int nextElevator = currState.curr.getElevatorFloor() + 1;
		}
		
		if (currState.curr.getElevatorFloor() > 0) {
			int nextElevator = currState.curr.getElevatorFloor() - 1;
		}
		
		
		
		return rv;
	}
	
	private void constructMinPath(InternalState state, LinkedList<FloorState> target) {
		target.clear();
		InternalState tmp = state;
		do {
			target.addFirst(tmp.curr);
			tmp = tmp.prev;
		} while(tmp != null);
	}
}
