package org.tondo.advent2018.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import org.junit.Assert;

public class Day2Test {
	
	private static class Accumulator {
		private int two = 0;
		private int three = 0;
		
		public void accept(String str) {
			Map<Integer, Long> acc = str.chars().boxed().collect(Collectors.groupingBy(c -> c, Collectors.counting()));
			boolean twoSet = false;
			boolean threeSet = false;
			for (Long cnt : acc.values()) {
				
				if (!twoSet && cnt == 2) {
					two++;
					twoSet = true;
				} else if (!threeSet && cnt == 3) {
					three++;
					threeSet = true;
				}
			}
		}
		
		public int getThree() {
			return three;
		}
		
		public int getTwo() {
			return two;
		}
		
	}

	@Test
	public void testPart1() throws IOException {
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/day2/day2Part1.txt")))) {
			String line = null;
			Accumulator acc = new Accumulator();
			while ((line = reader.readLine()) != null) {
				acc.accept(line);
			}
			
			System.out.println("Day 2 - Part 1: checksum: " + (acc.getThree() * acc.getTwo()));
		}
	}
	
	@Test
	public void testPart2() throws IOException {
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/day2/day2Part1.txt")))) {
			String line = null;
			Set<String> acc = new HashSet<>();
			String id = null;
			while ((line = reader.readLine()) != null && id == null) {
				
				for (String s : acc) {
					int index;
					if ((index = findSingleDifference(s, line)) > -1) {
						id = line.substring(0, index) + line.substring(index + 1);
						break;
					}
					
				}
				
				acc.add(line);
			}
			
			System.out.println("Day 2 - Part 2: ID: " + id);
		}
	}
	
	@Test
	public void testFindDifference() {
		Assert.assertEquals( 3, findSingleDifference("fghij", "fguij") +1);
	}
	
	/**
	 * @return index of single difference, -1 when strings are equal, other have more differences
	 */
	private static int findSingleDifference(String s1, String s2) {
		int len1 = s1.length();
		int len2 = s2.length();
		if (len1 != len2) {
			return -1;
		}
		
		int diff = -1;
		for (int i =0; i < len1; i++) {
			
			if (s1.charAt(i) != s2.charAt(i)) {
				if (diff > -1) {
					// more than one difference found
					return -1;
				} else {
					diff = i;
				}
			}
		}
		
		return diff;
		
	}
	
	@Test
	public void testAccumulator() {
		List<String> input = Arrays.asList("abcdef", "bababc", "abbcde", "abcccd", "aabcdd", "abcdee", "ababab");
		
		Accumulator acc = new Accumulator();
		for (String s : input) {
			acc.accept(s);
		}
		
		Assert.assertEquals(12, (acc.getThree() * acc.getTwo()));
	}
	
	@Test
	public void testCollector() {
		"bababc".chars().boxed().collect(Collectors.groupingBy(c -> c, Collectors.counting())).forEach( (k, v) -> {
			char x = (char)k.intValue();
			System.out.println(x + " : " + v);
		});
	}
}
