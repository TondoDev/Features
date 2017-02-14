package org.tondo.advent2016.day11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
			}  else if (processing.steps < 30 && ((minimum < 0 || (minimum - 1) > processing.steps))) {
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
		Set<String> curretnFloorState = currState.curr.getFloorConfiguration(currState.curr.getElevatorFloor());
		SubsetGen stateGen = new SubsetGen(curretnFloorState, ELEVATOR_CAPACITY);
		boolean belowEmpty = isBelowEmpty(currState.curr);
		while (stateGen.hasNext()) {
			List<String> state = stateGen.getNext();
			Set<String> nextSourceFloorState = new HashSet<>(curretnFloorState);
			nextSourceFloorState.removeAll(state);
			
			if (!isValidFloorConfiguration(nextSourceFloorState)) {
				continue;
			}
			
			for (int direction : new int[] {-1, 1}) {
				if (direction == -1 && belowEmpty) {
					continue;
				}
				
				int nextFloorNumber = currState.curr.getElevatorFloor() + direction;
				if (nextFloorNumber <= 4 && nextFloorNumber >= 1) {
					Set<String> currentTargetFloorState = currState.curr.getFloorConfiguration(nextFloorNumber);
					Set<String> nextTargetFloorState = new HashSet<>(currentTargetFloorState);
					nextTargetFloorState.addAll(state);
					if (isValidFloorConfiguration(nextTargetFloorState)) {
						Map<Integer, Set<String>> nextFloors = new HashMap<>(currState.curr.getFloors());
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
		return removeHomomorphic(rv);
	}
	
	private List<InternalState> removeHomomorphic(List<InternalState> s) {
		
		for (int out = 0; out < s.size(); out++) {
			for (int in = out + 1; in < s.size(); in++) {
				InternalState o = s.get(out);
				InternalState i = s.get(in);
				if ((o != null && i != null) && o.curr.isHomomorphic(i.curr)) {
					s.set(in, null);
				}
			}
		}
		
		Iterator<InternalState> iter = s.iterator();
		while (iter.hasNext()) {
			if (iter.next() == null) {
				iter.remove();
			}
		}
		
		return s;
	}
	
	private boolean isBelowEmpty(FloorState state) {
		if (state.getElevatorFloor() == 1) {
			return false;
		}
		for (int i = 1 ; i < state.getElevatorFloor(); i++) {
			if(!state.getFloorConfiguration(i).isEmpty()) {
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
	
	public boolean isValidFloorConfiguration(Set<String> devices) {
		for (String dev : devices) {
			char element = dev.charAt(0);
			char deviceType = dev.charAt(1);
			
			if (deviceType == 'm' && isChipInDanger(element, devices)) {
				return false;
			}
			
		}
		return true;
	}
	
	private boolean isChipInDanger(char elem, Set<String> devices) {
		
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
