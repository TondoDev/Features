package org.tondo.daythree;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class RobotHelperTest {

	@Test
	public void testRebotHelperAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/daythree/pathWithRobot.txt");
		assertNotNull(is);
		int visitedHouses = countHousesTogetherWithRobot(is);
		System.out.println("Day 3. Second part: Visited houses: " + visitedHouses);
		assertEquals("Visited houses:", 2341,  visitedHouses);
	}
	
	@Test
	public void testRobotHelperSamples() throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream("^>".getBytes());
		assertEquals(3, countHousesTogetherWithRobot(is));
		
		is = new ByteArrayInputStream("^>v<".getBytes());
		assertEquals(3, countHousesTogetherWithRobot(is));
		
		is = new ByteArrayInputStream("^v^v^v^v^v".getBytes());
		assertEquals(11, countHousesTogetherWithRobot(is));
	}
	
	private int countHousesTogetherWithRobot(InputStream is) throws IOException {
		// santa coords
		int sx = 0;
		int sy = 0;
		// robot coords
		int rx = 0;
		int ry = 0;
		
		boolean stantaTurn = true;
		
		Set<String> visitedHouses = new HashSet<>();
		visitedHouses.add("0,0");

		int data = -1;
		while ((data = is.read()) != -1) {
			// work coords
			int x = 0;
			int y = 0;
			if (stantaTurn) {
				x = sx;
				y = sy;
			} else {
				x = rx;
				y = ry;
			}
			
			char direction = (char)data;
			if( direction == '^') {
				y++;
			} else if (direction == 'v') {
				y--;
			} else if (direction == '>') {
				x++;
			} else if (direction == '<') {
				x--;
			} else {
				throw new IllegalStateException("Invalid input characted");
			}
			
			visitedHouses.add("" + x + "," + y);
			if (stantaTurn) {
				sx = x;
				sy = y;
			} else {
				rx = x;
				ry = y;
			}
			
			// changing thurn between them
			stantaTurn = !stantaTurn;
		}
		
		return visitedHouses.size();
	}
}
