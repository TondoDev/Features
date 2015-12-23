package org.tondo.daythirteen;

import static org.junit.Assert.*;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.tondo.daynine.PermutationGen;


public class SeatingArrangementTest {
	
	@Test
	public void testSeatingArragementAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/daythirteen/firstPart.txt");
		assertNotNull(is);
		
		int highestHappiness = this.getOptimalHapiness(is);
		System.out.println("Day 13. First part. Maximum happiness: " + highestHappiness);
		assertEquals("Day 13. First part. Maximum happiness: ", 664, highestHappiness);
		
		// same input for second part
		is = getClass().getResourceAsStream("/daythirteen/firstPart.txt");
		assertNotNull(is);
		int happinessWithMe = this.getOptimalHapinessWithMe(is);
		System.out.println("Day 13. Second part. Maximum happiness: " + happinessWithMe);
		assertEquals("Day 13. Second part. Maximum happiness: ", 664, happinessWithMe);
	}
	
	@Test
	public void testSeatingArragementSample() throws IOException {
		
		StringBuilder sb = new StringBuilder();
		sb
		.append("Alice would gain 54 happiness units by sitting next to Bob.").append('\n')
		.append("Alice would lose 79 happiness units by sitting next to Carol.").append('\n')
		.append("Alice would lose 2 happiness units by sitting next to David.").append('\n')
		.append("Bob would gain 83 happiness units by sitting next to Alice.").append('\n')
		.append("Bob would lose 7 happiness units by sitting next to Carol.").append('\n')
		.append("Bob would lose 63 happiness units by sitting next to David.").append('\n')
		.append("Carol would lose 62 happiness units by sitting next to Alice.").append('\n')
		.append("Carol would gain 60 happiness units by sitting next to Bob.").append('\n')
		.append("Carol would gain 55 happiness units by sitting next to David.").append('\n')
		.append("David would gain 46 happiness units by sitting next to Alice.").append('\n')
		.append("David would lose 7 happiness units by sitting next to Bob.").append('\n')
		.append("David would gain 41 happiness units by sitting next to Carol.").append('\n');
		
		ByteArrayInputStream is = new ByteArrayInputStream(sb.toString().getBytes());
		int happiness = this.getOptimalHapiness(is);
		assertEquals("Sample seating arragement happiness", 330, happiness);
	}
	
	/**
	 * brute force aproach 
	 * @throws IOException 
	 */
	private int getOptimalHapiness(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		Map<String, Map<String, Integer>> happinessTable = this.createHappinesTable(reader);
		
		ArrangementHandler handler = new ArrangementHandler(happinessTable);
		PermutationGen gen = new PermutationGen();
		gen.generatePermutations(happinessTable.keySet(), handler);
		
		return handler.getBestHappiness();
	}
	
	/**
	 * brute force aproach 
	 * @throws IOException 
	 */
	private int getOptimalHapinessWithMe(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		Map<String, Map<String, Integer>> happinessTable = this.createHappinesTable(reader);
		
		ArrangementHandler handler = new ArrangementHandler(happinessTable, false);
		PermutationGen gen = new PermutationGen();
		Set<String> visitors = new HashSet<>(happinessTable.keySet());
		visitors.add("TondoDev");
		gen.generatePermutations(visitors, handler);
		
		return handler.getBestHappiness();
	}
	
	private Map<String, Map<String, Integer>> createHappinesTable(BufferedReader reader) throws IOException {
		
		Map<String, Map<String, Integer>> retVal = new HashMap<>();
		String line = null;
		while((line = reader.readLine()) != null) {
			String[] parts = line.split("\\s+");
			if (parts.length != 11) {
				throw new IllegalStateException("invalid tokens count: " + parts.length);
			}
			
			String who = parts[0].trim();
			String whom = parts[parts.length - 1].trim();
			//remowing dot
			whom = whom.substring(0, whom.length() - 1);
			String operation = parts[2];
			int happVal = Integer.parseInt(parts[3]);
			
			Map<String, Integer> relation = retVal.get(who);
			if (relation == null) {
				relation = new HashMap<>();
				retVal.put(who, relation);
			}
			
			if ("lose".equals(operation)) {
				happVal *= -1; 
			} else if (!"gain".equals(operation)) {
				throw new IllegalStateException("Invalid operation " + operation);
			}
			
			relation.put(whom, happVal);
		}
		return retVal;
	}

}
