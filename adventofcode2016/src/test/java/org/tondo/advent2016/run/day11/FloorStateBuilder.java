package org.tondo.advent2016.run.day11;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tondo.advent2016.day11.FloorState;

public class FloorStateBuilder {
	private static final int MAX_FLOORS = 4;
	private int elevator = 1;
	private Map<Integer, Set<String>> floors = new HashMap<>();
	
	
	public FloorStateBuilder(int elevatorFloor) {
		this.elevator = elevatorFloor;
	}
	
	public FloorStateBuilder elevator(int elevatorFloor) {
		this.elevator = elevatorFloor;
		return this;
	}
	
	public FloorStateBuilder floor(int floor, String... items) {
		Set<String> set = new HashSet<>();
		for (String itm : items) {
			set.add(itm);
		}
		this.floors.put(floor, set);
		return this;
	}
	
	public FloorState build() {
		normalize();
		return new FloorState(floors, elevator);
	}
	
	private void normalize() {
		for (int i = 1; i <= MAX_FLOORS; i++) {
			Set<String> items = this.floors.get(i);
			if (items == null) {
				this.floors.put(i, Collections.<String>emptySet());
			}
		}
	}
}
