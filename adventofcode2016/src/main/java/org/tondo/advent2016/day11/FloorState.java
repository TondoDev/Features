package org.tondo.advent2016.day11;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author TondoDev
 *
 */
public class FloorState {
	private Map<Integer, List<String>> floors;
	private int elevatorFloor;
	
	private static final int NUM_OF_FLOORS = 4;

	public FloorState(Map<Integer, List<String>> floors, int elevatorFloor) {
		Map<Integer, List<String>> tmp = new HashMap<>();
		
		for (int i = 1; i <= NUM_OF_FLOORS; i++) {
			List<String> value = floors.get(i);
			tmp.put(i, (value == null ? Collections.<String>emptyList() : Collections.unmodifiableList(value)));
		}
		
		this.floors = Collections.unmodifiableMap(tmp);
		this.elevatorFloor = elevatorFloor;
	}
	
	public Map<Integer, List<String>> getFloors() {
		return floors;
	}
	
	public int getElevatorFloor() {
		return elevatorFloor;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof FloorState)) {
			return false;
		}
		
		if (this == obj) {
			return true;
		}
		FloorState other = (FloorState) obj;
		
		if (other.floors.get(4).size() == 10) {
			System.out.println("---------");
		}
		
		if (this.elevatorFloor !=  other.elevatorFloor) {
			return false;
		}
		
		if (this.floors.size() != other.floors.size()) {
			return false;
		}
		
		for (Map.Entry<Integer, List<String>> currEntry : this.floors.entrySet()) {
			Set<String> othList = new HashSet<>(other.floors.get(currEntry.getKey()));
			
			if (othList == null || !new HashSet<>(currEntry.getValue()).equals(othList)) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		return this.floors.toString() + "|| e = " + this.elevatorFloor;
	}
}
