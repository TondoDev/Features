package org.tondo.dayfourteen;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ReindeerPointsTest {
	
	private static final int RACE_TIME = 2503;

	@Test
	public void testReindeerPointsAnswer() throws NumberFormatException, IOException {
		InputStream is = getClass().getResourceAsStream("/dayfourteen/firstPart.txt");
		assertNotNull(is);
		
		int winningDistance = this.getWinningPoints(is, RACE_TIME);
		System.out.println("Day 14. Second part: winning points " + winningDistance);
		assertEquals("Day 14. Second part: winning points", 1256, winningDistance);
	}
	
	@Test
	public void testReinderrPointsSample() throws NumberFormatException, IOException {
		StringBuilder inputBuilder = new StringBuilder();
		
		inputBuilder
			.append("Commet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds").append('\n')
			.append("Dancer can fly 16 km/s for 11 seconds, but then must rest for 162 seconds").append('\n');
		
		ByteArrayInputStream is = new ByteArrayInputStream(inputBuilder.toString().getBytes());
		int winningPts = getWinningPoints(is, 1000);
		assertEquals("winnint points sample", 689, winningPts);
	}
	
	
	private int getWinningPoints(InputStream is, int raceTime) throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		List<Reindeer> startingGrid = createStartList(reader);
		
		for (int second = 0; second < raceTime; second++) {
			raceSimulationStep(startingGrid);
		}
		
		int maxPoints = -1;
		for(Reindeer r  : startingGrid) {
			int currPts = r.getPoints();
			
			if (currPts > maxPoints) {
				maxPoints = currPts;
			}
		}
		
		return maxPoints;
	}
	
	private List<Reindeer> createStartList(BufferedReader reader) throws NumberFormatException, IOException {
		List<Reindeer> starting = new ArrayList<>();
		String line = null;
		
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("\\s+");
			if (parts.length != 15) {
				throw new IllegalStateException("Invalid input. Expected 15 tokens, but found " + parts.length);
			}
			
			String reindeerName = parts[0];
			int speed = Integer.parseInt(parts[3]);
			int travelTime = Integer.parseInt(parts[6]);
			int restTime = Integer.parseInt(parts[13]);
			
			Reindeer reindeer = new Reindeer(reindeerName, speed, travelTime, restTime);
			starting.add(reindeer);
		}
		
		return starting;
	}
	
	
	private void raceSimulationStep(List<Reindeer> reindeerRacers) {
		int maxDist = -1;
		List<Reindeer> awarded = new ArrayList<>();
		
		for (Reindeer reindeer : reindeerRacers) {
			reindeer.incrementTime();
			
			int currentDist = reindeer.getDistance();
			if (currentDist > maxDist) {
				awarded.clear();
				awarded.add(reindeer);
				maxDist = currentDist;
			} else if (maxDist == currentDist) {
				awarded.add(reindeer);
			}
		}
		
		for (Reindeer r : awarded) {
			r.incrementPoints();
		}
	}
}
