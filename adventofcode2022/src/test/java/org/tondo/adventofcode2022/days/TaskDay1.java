package org.tondo.adventofcode2022.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.LongStream;

import org.junit.Test;
import org.tondo.adventofcode2022.DayTaskBase;

public class TaskDay1 extends DayTaskBase {
	
	private static final String[] SAMPLE_INPUT = {
			"1000",
			"2000",
			"3000",
			"\n",
			"4000",
			"\n",
			"5000",
			"6000",
			"\n",
			"7000",
			"8000",
			"9000",
			"\n",
			"10000"	
	};

	public TaskDay1() {
		super(1);
	}
	
	@Test
	public void testPartOne() throws IOException {
		assertEquals("70374", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws IOException {
		assertEquals("204610", getPartTwoSolution());
	}
	
	
	@Override
	public String getPartOneSolution() throws IOException {
		try(BufferedReader r = getPartOneInput()) {
			return getMaximumCalories(r) + "";
		}
	}
	
	@Override
	public String getPartTwoSolution() throws IOException {
		try(BufferedReader r = getPartOneInput()) {
			return getMaximumCaloriesOf3Elfs(r) + "";
		}
	}
	
	private long getMaximumCalories(BufferedReader r) throws IOException {
		String line = null;
		long maxCals = -1L;
		long currentSum = 0L;
		while ((line = r.readLine()) != null) {
			
			if (line.isBlank()) {
				if (currentSum > maxCals) {
					maxCals = currentSum;
				}
				currentSum = 0;
			} else {
				currentSum += Long.valueOf(line);
			}
		}
		
		// for last row
		if (currentSum > maxCals) {
			maxCals = currentSum;
		}
		
		return maxCals;
	}
	
	private long getMaximumCaloriesOf3Elfs(BufferedReader r) throws NumberFormatException, IOException {
		String line = null;
		long[] maxCals = {0,0,0};
		long currentSum = 0L;
		while ((line = r.readLine()) != null) {
			if (line.isBlank()) {
				updateTop3(currentSum, maxCals);
				currentSum = 0;
			} else {
				currentSum += Long.valueOf(line);
			}
		}
		
		// for last sum
		updateTop3(currentSum, maxCals);
		
		return LongStream.of(maxCals).sum();
	}
	
	private void updateTop3(long currentVal, long[] topThree) {
		for (int i =0; i < topThree.length; i++) {
			if (currentVal > topThree[i]) {
				for (int c = topThree.length - 2; c >=  i; c--) {
					topThree[c + 1] = topThree [c];
				}
				topThree[i] = currentVal;
				break;
			}
		}
	}
	
	@Test
	public void testSampleInputP1() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT);
		
		long maximumCalories = getMaximumCalories(reader);
		assertEquals(24000, maximumCalories);
		
	}
	
	@Test
	public void testSampleInputP2( ) throws NumberFormatException, IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT);
		long maximumCaloriesOf3Elfs = getMaximumCaloriesOf3Elfs(reader);
		assertEquals(45000, maximumCaloriesOf3Elfs);
	}
}
