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
		
		public int getFloorNum() {
			return floorNum;
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
	
	public boolean isHomomorphic(FloorState other) {
		if(other == null) {
			throw new IllegalArgumentException("Other objectg can't be null!");
		}
		
		Map<Integer, Integer> mapping = new HashMap<>();
		for (Floor elem : this.floors.values()) {
			Map<Integer, Set<String>> internalThis = elem.getFloorInternal();
			Map<Integer, Set<String>> internalOther = other.floors.get(elem.getFloorNum()).getFloorInternal();
			if (!checkInternal(internalThis, internalOther, mapping)) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean checkInternal(Map<Integer, Set<String>> first, Map<Integer, Set<String>> second, Map<Integer, Integer> mapping) {
		
		// marked elements already mapped from second (inner)
//		Set<Integer> mappedSecond = new HashSet<>();
		int mappedRemaining = first.size();
		for (Map.Entry<Integer, Set<String>> entryOuter : first.entrySet()) {
			for (Map.Entry<Integer, Set<String>> entryInner : second.entrySet()) {
				if (entryOuter.getValue().equals(entryInner.getValue())) {
					Integer translation = mapping.get(entryOuter.getKey());
					if (translation == null) {
						if (!mapping.values().contains(entryInner.getKey())) {
							mapping.put(entryOuter.getKey(), entryInner.getKey());
							mappedRemaining--;
							break;
						}
					} else if (translation.equals(entryInner.getKey())) {
						mappedRemaining--;
						break;
					}
				}
			}
		}
		return mappedRemaining == 0;
	}
	
	@Override
	public String toString() {
		return this.floors.toString() + "|| e = " + this.elevatorFloor;
	}
}
