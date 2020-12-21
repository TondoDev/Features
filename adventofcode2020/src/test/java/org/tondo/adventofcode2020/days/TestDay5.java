package org.tondo.adventofcode2020.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.tondo.adventofcode2020.DayTaskBase;

public class TestDay5 extends  DayTaskBase{

	public TestDay5() {
		super(5);
	}
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("919", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("642", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {

		int maxId = -1;
		try (BufferedReader reader = getPartOneInput()) {
			String line;
			while ( (line = reader.readLine()) != null) {
				int currentId = getSeatId(line);
				if (currentId > maxId) {
					maxId = currentId;
				}
			}
		}
		
		return "" + maxId;
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		
		int[] seats = new int[1024];
		try (BufferedReader reader = getPartOneInput()) {
			String line;
			while ( (line = reader.readLine()) != null) {
				seats[getSeatId(line)]++;
			}
		}
		
		List<Integer> possibleSeats = new ArrayList<>();
		for (int i = 0; i < seats.length; i++) {
//			if (i != 0 && (i % 8) == 0) {
//				System.out.println();
//			}
//			System.out.print(seats[i] + " ");
			
			if (seats[i] == 0 
					&& (i - 1)> 0 && seats[i-1] == 1 
					&& (i + 1) < seats.length && seats[i + 1] == 1) {
				
				possibleSeats.add(i);
			}
		}
		
		if (possibleSeats.size() != 1) {
			throw new IllegalStateException("Exactly one seat should be empty for you! Found: " + possibleSeats);
		}
		
		return "" + possibleSeats.get(0) ;
	}
	
	
	@Test
	public void testCovnersion() {
		assertEquals(70, getRowNumber("BFFFBBF"));
		assertEquals(14, getRowNumber("FFFBBBF"));
		assertEquals(102, getRowNumber("BBFFBBF"));
		
		assertEquals(7, getSeatNumber("RRR"));
		assertEquals(4, getSeatNumber("RLL"));
		
		assertEquals(567, getSeatId("BFFFBBFRRR"));
		assertEquals(119, getSeatId("FFFBBBFRRR"));
		assertEquals(820, getSeatId("BBFFBBFRLL"));
		
		
		//assertEquals(0, getRowNumber("BBFFBBFBBB"));
		
		
	}
	
	protected int getRowNumber(String encoded) {
		String decoded = encoded.replace('B', '0').replace('F', '1');
		return 127 - Integer.parseInt(decoded, 2);
	}
	
	protected int getSeatNumber(String encoded) {
		String decoded = encoded.replace('R', '0').replace('L', '1');
		return 7 - Integer.parseInt(decoded, 2);
	}
	
	protected int getSeatId(String encodedPlace) {
		String[] encoded = split(encodedPlace);
		return 8 * getRowNumber(encoded[0]) + getSeatNumber(encoded[1]);
	}
	
	protected  String[] split(String encodedPlace) {
		String encodedRow = encodedPlace.substring(0, 7);
		String encodedSeat = encodedPlace.substring(7);
		
		return new String[] {encodedRow, encodedSeat};
	}

}
 