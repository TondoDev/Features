package org.tondo.adventofcode2019.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.Test;
import org.tondo.adventofcode2019.DayTaskBase;

public class TaskDay1 extends DayTaskBase{

	public TaskDay1() {
		super(1);
	}
	
	
	@Test
	public void testPartOne() throws IOException {
		
		System.out.println(getPartOneSolution());
	}
	
	@Override
	public String getPartOneSolution() throws NumberFormatException, IOException  {
		BufferedReader r = getPartOneInput();
		
		String line = null;
		long sum = 0L;
		while ((line = r.readLine()) != null) {
			
			int mass = Integer.parseInt(line);
			sum += mass/3 - 2;
		}
		
		return String.valueOf(sum);
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		// input is the same
		BufferedReader r = getPartOneInput();
		
		String line = null;
		long sum = 0L;
		while ((line = r.readLine()) != null) {
			
			sum += getFuelForMass(Integer.parseInt(line));
		}
		
		return String.valueOf(sum);
	}
	
	@Test
	public void testPartOneSolution() {
		try {
			assertEquals("3256114", getPartOneSolution());
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testPartTwoSolution() throws Exception {
		
		try {
			assertEquals("4881302", getPartTwoSolution());
		} catch (Exception e) {
		}
	}
	
	
	
	public long getFuelForMass(int mass) {
		long sum = 0L;
		
		int part = mass/3 - 2;
		while (part > 0) {
			sum += part;
			part = part/3 - 2;
		}
		
		return sum;
	}
	
	@Test
	public void testFuelForMass() {
		assertEquals(2L, getFuelForMass(14));
		assertEquals(966L, getFuelForMass(1969));
		assertEquals(50346L, getFuelForMass(100756));
	}
	

}
