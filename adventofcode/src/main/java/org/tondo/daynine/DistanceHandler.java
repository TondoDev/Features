package org.tondo.daynine;

import java.util.Arrays;
import java.util.Map;

public class DistanceHandler implements PermutationHadler{
	
	public static enum DistType {
		SHORTEST,
		LONGEST
	}

	private int shortestDistance;
	private String[] shortestPerm;
	private Map<String, Map<String,Integer>> distances;
	private DistType type;
	
	public  DistanceHandler(Map<String, Map<String,Integer>> distanceMap) {
		this(distanceMap, DistType.SHORTEST);
	}
	
	public  DistanceHandler(Map<String, Map<String,Integer>> distanceMap, DistType distanceType) {
		this.distances = distanceMap;
		this.type = distanceType;
	}
	
	@Override
	public void handle(String[] permutation) {
		int currentDistance = 0;
		for (int i = 0; i < permutation.length - 1; i++) {
			currentDistance += this.distances
					.get(permutation[i]) // destination from current location
					.get(permutation[i + 1]); // destination distance from current location
		}
		
		if (this.shortestPerm == null || 
				(this.type == DistType.SHORTEST ? currentDistance < this.shortestDistance : currentDistance > this.shortestDistance)) {
			this.shortestPerm = Arrays.copyOf(permutation, permutation.length);
			this.shortestDistance = currentDistance;
		}
	}
	
	
	public int getDistance() {
		return shortestDistance;
	}
	
	public String[] getShortestPath() {
		return Arrays.copyOf(this.shortestPerm, this.shortestPerm.length);
	}

}
