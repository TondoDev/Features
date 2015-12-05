package org.tondo.daythree;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class PresentRouteTest {

	@Test
	public void testPresentRouteAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/daythree/presentPath.txt");
		assertNotNull(is);
		int visitedHouses = countHousesWithPresents(is);
		System.out.println("Day 3. First part: Visited houses: " + visitedHouses);
		assertEquals("Visited houses:", 2081,  visitedHouses);
	}
	
	@Test
	public void testPresentRouteSamples() throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(">".getBytes());
		assertEquals(2, countHousesWithPresents(is));
		
		is = new ByteArrayInputStream("^>v<".getBytes());
		assertEquals(4, countHousesWithPresents(is));
		
		is = new ByteArrayInputStream("^v^v^v^v^v".getBytes());
		assertEquals(2, countHousesWithPresents(is));
	}
	
	private int countHousesWithPresents(InputStream is) throws IOException {
		int x = 0;
		int y = 0;
		Set<String> visitedHouses = new HashSet<>();
		visitedHouses.add("" + x + "," + y);

		int data = -1;
		while ((data = is.read()) != -1) {
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
		}
		
		return visitedHouses.size();
	}
}
