package org.tondo.dayfourteen;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

public class ReindeerOlympicsTest {
	
	private static final int RACE_TIME = 2503;
	@Test
	public void testReindeerAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/dayfourteen/firstPart.txt");
		assertNotNull(is);
		
		int winningDistance = this.getWinningDistance(is, RACE_TIME);
		System.out.println("Day 14. First part: winning ditance " + winningDistance);
		assertEquals("Day 14. First part: winning ditance", 2660, winningDistance);
	}
	
	@Test
	public void testReindeerSample() {
		Reindeer comet = new Reindeer("Commet", 14, 10, 127);
		Reindeer dancer = new Reindeer("Dancer", 16, 11, 162);
		
		assertEquals("Commet after 1 sec", 14, comet.getDistanceAfterTime(1));
		assertEquals("Dancer after 1 sec", 16, dancer.getDistanceAfterTime(1));
		
		assertEquals("Commet after 10 sec", 140, comet.getDistanceAfterTime(10));
		assertEquals("Dancer after 10 sec", 160, dancer.getDistanceAfterTime(10));
		
		assertEquals("Commet after 11 sec", 140, comet.getDistanceAfterTime(11));
		assertEquals("Dancer after 11 sec", 176, dancer.getDistanceAfterTime(11));
		
		assertEquals("Commet after 1000 sec", 1120, comet.getDistanceAfterTime(1000));
		assertEquals("Dancer after 1000 sec", 1056, dancer.getDistanceAfterTime(1000));
	}
	
	
	private int getWinningDistance(InputStream is, int raceTime) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		int winningDistance = -1;
		
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
			int currentDistance = reindeer.getDistanceAfterTime(raceTime);
			if (currentDistance > winningDistance) {
				winningDistance = currentDistance;
			}
			
			//System.out.println(reindeerName + " - " + currentDistance);
		}
		
		return winningDistance;
	}

}
