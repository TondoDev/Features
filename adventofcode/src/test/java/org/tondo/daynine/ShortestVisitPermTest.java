package org.tondo.daynine;


import static org.junit.Assert.*;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.tondo.daynine.DistanceHandler.DistType;

public class ShortestVisitPermTest {
	
	@Test
	public void testShortestVisitAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/daynine/distances.txt");
		assertNotNull(is);
		
		int shortest = this.getDistanceByPathType(is, DistType.SHORTEST);
		assertEquals("Day 9. First part. Shortest path", 207, shortest);
		System.out.println("Day 9. First part. Shortest path " + shortest);
		
		// same input
		is = getClass().getResourceAsStream("/daynine/distances.txt");
		assertNotNull(is);
		
		int longest = this.getDistanceByPathType(is, DistType.LONGEST);
		assertEquals("Day 9. Second part. Longest path", 804, longest);
		System.out.println("Day 9. Second part. Longest path " + longest);
	}
	
	@Test
	public void testShortestVisitSample() throws IOException {
		StringBuilder builder = new StringBuilder();
		builder
			.append("London to Dublin = 464").append('\n')
			.append("London to Belfast = 518").append('\n')
			.append("Dublin to Belfast = 141").append('\n');
		
		ByteArrayInputStream input = new ByteArrayInputStream(builder.toString().getBytes());
		int shortest = this.getDistanceByPathType(input, DistType.SHORTEST);
		assertEquals("Shortest distance for sample", 605, shortest);
	}
	
	
	@Test
	public void testLongestPathSample() throws NumberFormatException, IOException {
		StringBuilder builder = new StringBuilder();
		builder
			.append("London to Dublin = 464").append('\n')
			.append("London to Belfast = 518").append('\n')
			.append("Dublin to Belfast = 141").append('\n');
		
		ByteArrayInputStream input = new ByteArrayInputStream(builder.toString().getBytes());
		int shortest = this.getDistanceByPathType(input, DistType.LONGEST);
		assertEquals("Longest distance for sample", 982, shortest);
	}
	
//	@Test
//	public void testPermutation() {
//		PermutationGen gen = new PermutationGen();
//		gen.generatePermutations(Arrays.asList("A", "B", "C"), new PermutationPrinter());
//	}
	
	
	private int getDistanceByPathType(InputStream is, DistType distanceType) throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		Map<String, Map<String, Integer>> distances = this.populateMap(reader);
		DistanceHandler handler = new DistanceHandler(distances, distanceType);
		PermutationGen gen = new PermutationGen();
		// expected that road exists between all places
		gen.generatePermutations(distances.keySet(), handler);
		
		return handler.getDistance();
	}
	
	private Map<String, Map<String, Integer>> populateMap(BufferedReader reader) throws NumberFormatException, IOException {
		Map<String, Map<String, Integer>> distances = new HashMap<>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("=");
			int dist = Integer.parseInt(parts[1].trim());
			String[] cities = parts[0].split(" to ");
			String from = cities[0].trim();
			String to = cities[1].trim();
			
			storeToMap(distances, from, to, dist);
			// storing reverse direction
			storeToMap(distances, to, from, dist);
		}
		
		return distances;
	}
	
	private void storeToMap(Map<String, Map<String, Integer>> distances, String from, String to, int distance) {
		Map<String, Integer> destination = distances.get(from);
		if (destination == null) {
			destination = new HashMap<>();
			distances.put(from, destination);
		}
		destination.put(to, distance);
	}

}
