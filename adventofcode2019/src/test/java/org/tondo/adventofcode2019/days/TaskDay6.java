package org.tondo.adventofcode2019.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.tondo.adventofcode2019.DayTaskBase;
import org.tondo.adventofcode2019.day6.Universum;

public class TaskDay6 extends DayTaskBase{
	
	

	public TaskDay6() {
		super(6);
	}
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("417916", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("523", getPartTwoSolution());
	}
	
	
	@Override
	public String getPartOneSolution() throws Exception {
		Universum universum = new Universum();
		try (BufferedReader reader = getPartOneInput()) {
			String line;
			while ( (line = reader.readLine()) != null) {
				String[] objects = line.split("\\)");
				universum.addOribt(objects[0], objects[1]);
			}
		}
		return "" + universum.getOrbitsCount();
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		Universum universum = new Universum();
		// input is the same
		try (BufferedReader reader = getPartOneInput()) {
			String line;
			while ( (line = reader.readLine()) != null) {
				String[] objects = line.split("\\)");
				universum.addOribt(objects[0], objects[1]);
			}
		}
		return "" + universum.getDistanceBetween("YOU", "SAN");
	}
	
	
	@Test
	public void testSampleInput() {
		List<String> inputs = Arrays.asList(
				"COM)B",
				"B)C",
				"C)D",
				"D)E",
				"E)F",
				"B)G",
				"G)H",
				"D)I",
				"E)J",
				"J)K",
				"K)L"
				);
		
		assertEquals(42L, getNumberOfAllOrbits(inputs));
	}
	
	@Test
	public void testSampleInputPartTwo() {
		List<String> inputs = Arrays.asList(
				"COM)B",
				"B)C",
				"C)D",
				"D)E",
				"E)F",
				"B)G",
				"G)H",
				"D)I",
				"E)J",
				"J)K",
				"K)L",
				"K)YOU",
				"I)SAN"
				);
		Universum universum = new Universum();
		for (String i : inputs)  {
			String[] objects = i.split("\\)");
			universum.addOribt(objects[0], objects[1]);
		}
		assertEquals(4L, universum.getDistanceBetween("YOU", "SAN"));
	}
	
	public long getNumberOfAllOrbits(List<String> inputs) {
		Universum universum = new Universum();
		for (String i : inputs) {
			String[] objects = i.split("\\)");
			universum.addOribt(objects[0], objects[1]);
		}
		return universum.getOrbitsCount();
	}

}
