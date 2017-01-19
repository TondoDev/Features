package org.tondo.advent2016.day11;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author TondoDev
 *
 */
public class FloorState {
	private Map<Integer, List<String>> floors;
	private int elevatorFloor;

	public FloorState(Map<Integer, List<String>> floors, int elevatorFloor) {
		Map<Integer, List<String>> tmp = new HashMap<>();
		
		for (Map.Entry<Integer, List<String>> e : floors.entrySet()) {
			tmp.put(e.getKey(), (e.getValue() == null ? Collections.<String>emptyList() : Collections.unmodifiableList(e.getValue())));
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
		if (this.elevatorFloor !=  other.elevatorFloor) {
			return false;
		}
		
		if (this.floors.size() != other.floors.size()) {
			return false;
		}
		
		for (Map.Entry<Integer, List<String>> currEntry : this.floors.entrySet()) {
			List<String> othList = other.floors.get(currEntry.getKey());
			
			if (othList == null || !currEntry.getValue().equals(othList)) {
				return false;
			}
		}
		
		return true;
	}
}
