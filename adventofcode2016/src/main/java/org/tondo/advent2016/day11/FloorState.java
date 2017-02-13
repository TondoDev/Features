package org.tondo.advent2016.day11;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author TondoDev
 *
 */
public class FloorState {
	private static class Floor {
		private Set<String> configuration;
		private int floorNum;
		private Map<Integer, Set<String>> floorInternal;
		
		public Floor(int fn, Set<String> config) {
			this.floorNum = fn;
			this.configuration = config == null ? Collections.<String>emptySet() : new HashSet<>(config);
			this.floorInternal = transform(this.configuration);
		}
		
		
		public Set<String> getConfiguration() {
			return this.configuration;
		}
		
		
		private Map<Integer, Set<String>> transform(Set<String> configuration) {
			Map<Integer, Set<String>> rv = new HashMap<>();
			
			for (String itm : configuration) {
				Integer element = Integer.parseInt(itm.substring(0, 1));
				String machine = itm.substring(1);
				Set<String> deviceSet = rv.get(element);
				if (deviceSet == null) {
					deviceSet = new HashSet<>();
					rv.put(element, deviceSet);
				}
				deviceSet.add(machine);
			}
			
			return rv;
		}
		
		public Map<Integer, Set<String>> getFloorInternal() {
			return floorInternal;
		}
		
		@Override
		public String toString() {
			return this.configuration.toString();
		}
	}
	
	private Map<Integer, Floor> floors;
	private int elevatorFloor;
	
	private static final int NUM_OF_FLOORS = 4;

	public FloorState(Map<Integer, Set<String>> floors, int elevatorFloor) {
		Map<Integer, Floor> tmp = new HashMap<>();
		for (int i = 1; i <= NUM_OF_FLOORS; i++) {
			Set<String> value = floors.get(i);
			tmp.put(i, new Floor(i, value));
		}
		
		this.floors = Collections.unmodifiableMap(tmp);
		this.elevatorFloor = elevatorFloor;
	}
	
	public Set<String> getFloorConfiguration(int num) {
		return this.floors.get(num).getConfiguration();
	}
	
	public int getElevatorFloor() {
		return elevatorFloor;
	}
	
	public Map<Integer, Set<String>> getFloors() {
		Map<Integer, Set<String>> rv = new HashMap<>();
		
		for (Map.Entry<Integer, Floor> entry: this.floors.entrySet()) {
			rv.put(entry.getKey(), entry.getValue().getConfiguration());
		}
		
		return rv;
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
		
		for (Map.Entry<Integer, Floor> currEntry : this.floors.entrySet()) {
			
			Set<String> othList = other.getFloorConfiguration(currEntry.getKey());
			
			if (othList == null || !currEntry.getValue().getConfiguration().equals(othList)) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isHomomorphic(FloorState other) {
		if(other == null) {
			throw new IllegalArgumentException("Other objectg can't be null!");
		}
		
//		for (Map.Entry<Integer, Set<String>> pair : this.floors.entrySet()) {
//			Set<String> otherSet = other.getFloors().get(pair.getKey());
//			if (pair.getValue().size() != otherSet.size()) {
//				return false;
//			}
//			
//			
//		}
		
		return true;
	}
	
	@Override
	public String toString() {
		return this.floors.toString() + "|| e = " + this.elevatorFloor;
	}
}
