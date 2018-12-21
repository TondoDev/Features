package org.tondo.advent2018.run;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

public class Day1Test {

	
	@Test
	public void testPart1() throws IOException, URISyntaxException {
		
		
		int frequency = Files.lines(Paths.get(this.getClass().getResource("/day1/day1Part1.txt").toURI())).mapToInt(Integer::parseInt).sum();
		Assert.assertEquals(502, frequency);
		System.out.println("Day 1 Part 1: " + frequency);
	}
	
	@Test
	public void testPart2() throws IOException, URISyntaxException {
		List<Integer> list = Files.lines(Paths.get(this.getClass().getResource("/day1/day1Part1.txt").toURI())).collect(Collectors.mapping(Integer::parseInt, Collectors.toList()));
		Set<Integer> foundFrequencies = new HashSet<>();
		Integer found = null;
		Iterator<Integer> iter = null;
		int acc = 0;
		while (found == null) {
			if (iter == null) {
				iter = list.iterator();
			}
			
			if (iter.hasNext()) {
				acc  += iter.next();
				if (!foundFrequencies.add(acc)) {
					found = acc;
					//System.out.println("Size: " + foundFrequencies.size());
				}
			} else {
				iter = list.iterator();
			}
		}
		
		Assert.assertEquals(Integer.valueOf(71961), found);
		System.out.println("Day 1 Part 2: " + found);
	}
}
