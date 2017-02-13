package org.tondo.advent2016.day11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * 
 * @author TondoDev
 *
 */
public class StateSpace {
	private static final int ELEVATOR_CAPACITY = 2;
	
	public static class InternalState {
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
	private Stack<InternalState> fifo;
	private LinkedList<FloorState> minimalPath;
	private Set<String> visited = new HashSet<>();
	
	
	public StateSpace(FloorState init) {
		this.initialState = init;
	}
	
	public int findMinimalSteps(FloorState endState) {
		int cntLoop = 0;
		
		this.visited = new HashSet<>();
		this.minimalPath = new LinkedList<>();
		if (initialState.equals(endState)) {
			this.minimalPath.add(initialState);
			return 0;
		}
		
		int solutions = 0;
		this.fifo = new Stack<>();
		this.fifo.add(new InternalState(initialState, null, 0));
		int minimum = -1;
		while(!this.fifo.isEmpty()) {
			InternalState processing = this.fifo.pop();
			if (endState.equals(processing.curr)) {
				solutions++;
				System.out.println("Solution: " + solutions + " steps: " + processing.steps);
				//System.out.println(constructMinPath(processing));
				if (minimum < 0 || minimum > processing.steps) {
					minimum = processing.steps;
					this.minimalPath = constructMinPath(processing);
					System.out.println("Min: " + minimum);
				}
			} else if (processing.steps < 50 && (minimum < 0 || (minimum - 1) > processing.steps)) {
				for (InternalState is : generateNextStates(processing)) {
					this.fifo.push(is);	
				}
			}
			cntLoop++;
			if (cntLoop == 1000000) {
				cntLoop = 0;
				System.out.println("Lifo: " + this.fifo.size() + " steps: " + processing.steps);
			}
		}
		System.out.println("Solutions found: " + solutions);
		System.out.println("Visited states: " + this.visited.size());
		return minimum;
	}
	
	public List<InternalState> generateNextStates(InternalState currState) {
		List<InternalState> rv = new ArrayList<>();
		
		List<String> curretnFloorState = currState.curr.getFloors().get(currState.curr.getElevatorFloor());
		SubsetGen stateGen = new SubsetGen(curretnFloorState, ELEVATOR_CAPACITY);
		
		while (stateGen.hasNext()) {
			List<String> state = stateGen.getNext();
			List<String> nextSourceFloorState = new ArrayList<>(curretnFloorState);
			nextSourceFloorState.removeAll(state);
			
			if (!isValidFloorConfiguration(nextSourceFloorState)) {
				continue;
			}
			
			for (int direction : new int[] {-1, 1}) {
				if (direction == -1 && isBelowEmpty(initialState)) {
					continue;
				}
				
				int nextFloorNumber = currState.curr.getElevatorFloor() + direction;
				if (nextFloorNumber <= 4 && nextFloorNumber >= 1) {
					List<String> currentTargetFloorState = currState.curr.getFloors().get(nextFloorNumber);
					List<String> nextTargetFloorState = new ArrayList<>(currentTargetFloorState);
					nextTargetFloorState.addAll(state);
					if (isValidFloorConfiguration(nextTargetFloorState)) {
						Map<Integer, List<String>> nextFloors = new HashMap<>(currState.curr.getFloors());
						nextFloors.put(nextFloorNumber, nextTargetFloorState);
						nextFloors.put(currState.curr.getElevatorFloor(), nextSourceFloorState);
						FloorState fs = new FloorState(nextFloors, nextFloorNumber);
						if (!isStateAlreadyVisited(fs, currState)) {
							rv.add(new InternalState(fs, currState, currState.steps+1));
						}
					}
				}
			}
		}
		return rv;
	}
	
	private boolean isBelowEmpty(FloorState state) {
		if (state.getElevatorFloor() == 1) {
			return false;
		}
		for (int i = 1 ; i < state.getElevatorFloor(); i++) {
			if(!state.getFloors().get(i).isEmpty()) {
				return false;
			}
			
		}
		return true;
	}
	
	private boolean isStateAlreadyVisited(FloorState newState, InternalState currentConfiguration) {
		InternalState tmp = currentConfiguration;
		
		while (tmp != null) {
			if (newState.equals(tmp.curr)) {
				return true;
			}
			
			tmp = tmp.prev;
		}
		
		return false;
	}
	
	public boolean isValidFloorConfiguration(List<String> devices) {
		for (String dev : devices) {
			char element = dev.charAt(0);
			char deviceType = dev.charAt(1);
			
			if (deviceType == 'm' && isChipInDanger(element, devices)) {
				return false;
			}
			
		}
		return true;
	}
	
	private boolean isChipInDanger(char elem, List<String> devices) {
		
		boolean inDanger = false;
		boolean hasProtection = false;
		for (String dev : devices) {
			if (dev.charAt(1) == 'g') {
				if (dev.charAt(0) != elem) {
					inDanger = true;
				} else {
					hasProtection = true;
				}
			}
		}
		
		return inDanger && !hasProtection;
	}
	
	private  LinkedList<FloorState>  constructMinPath(InternalState state) {
		LinkedList<FloorState> target = new LinkedList<>();
		InternalState tmp = state;
		do {
			target.addFirst(tmp.curr);
			tmp = tmp.prev;
		} while(tmp != null);
		return target;
	}
	
	public void printMinaml() {
		for (FloorState s : this.minimalPath) {
			System.out.println(s);
		}
	}
}