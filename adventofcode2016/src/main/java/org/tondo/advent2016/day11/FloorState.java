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
	private String id;

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
	
	public String getId() {
		if (this.id == null) {
			this.id = generateId();
		}
		
		return this.id;
	}
	
	private String generateId() {
		StringBuilder sb = new StringBuilder();
		sb.append("$" + this.elevatorFloor);
		
		for (int floor = 1; floor <= 4; floor++) {
			sb.append("#" + floor);
			List<String> floorContent = this.floors.get(floor);
			for (String dev : floorContent) {
				sb.append(dev);
			}
		}
		
		return sb.toString();
	}
}
