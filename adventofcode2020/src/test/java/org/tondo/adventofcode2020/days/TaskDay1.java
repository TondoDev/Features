package org.tondo.adventofcode2020.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.tondo.adventofcode2020.DayTaskBase;

public class TaskDay1 extends DayTaskBase {
	
	public static final int SUM = 2020;

	public TaskDay1() {
		super(1);
	}
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("800139", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("59885340", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {
		List<Integer> list = new ArrayList<>();
		try(BufferedReader r = getPartOneInput()) {
			String line = null;
			while ((line = r.readLine()) != null) {
				Integer num = Integer.parseInt(line);
				
				Integer foundNum = list.stream().filter(li -> ((li + num) == SUM)).findAny().orElse(null);
				if (foundNum != null) {
					return "" + (foundNum * num);
				}
				list.add(num);
				
			}
			
		}
		
		return null;
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		try(BufferedReader r = getPartOneInput()) {
			List<Integer> sorted = r.lines().map(l -> Integer.parseInt(l)).collect(Collectors.toList());
			int len = sorted.size();
			
			for (int a = 0; a <= len - 3; a++) {
				int aVal = sorted.get(a);
				for (int b = a + 1; b <= len - 2; b++) {
					int bVal = sorted.get(b);
					if (aVal+bVal < SUM ) {
						for (int c = b+1; c <= len -1; c++) {
							if (aVal + bVal + sorted.get(c) == SUM) {
								return "" + (aVal * bVal * sorted.get(c));
							}
						}
					}
				}
			}
			
			return null;
			
		}
	}
	
}
