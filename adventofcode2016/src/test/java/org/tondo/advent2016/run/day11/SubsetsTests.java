package org.tondo.advent2016.run.day11;

import java.util.Arrays;

import org.junit.Test;
import org.tondo.advent2016.day11.SubsetGen;

public class SubsetsTests {
	
	
	@Test
	public void testSuasdasdTwo() {
		SubsetGen gen = new SubsetGen(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H"), 2);
		int cnt = 0;
		while (gen.hasNext()) {
			cnt++;
			System.out.println(gen.getNext());
		}
		
		System.out.println("Count: " + cnt);
	}

}

