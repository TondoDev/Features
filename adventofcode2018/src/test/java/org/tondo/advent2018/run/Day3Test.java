package org.tondo.advent2018.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class Day3Test {
	private static final Pattern PARSER = Pattern.compile("^#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)$");
	private static final List<String> TEST_INPUT = Arrays.asList("#1 @ 1,3: 4x4", "#2 @ 3,1: 4x4", "#3 @ 5,5: 2x2");
	
	private static class Coordinate {
		private int id;
		private int left;
		private int top;
		private int sizeX;
		private int sizeY;
		
		
		public Coordinate(int i, int left, int top, int xs, int ys) {
			this.id = i;
			this.left = left;
			this.top = top;
			this.sizeX = xs;
			this.sizeY = ys;
		}
		
		public int getId() {
			return id;
		}

		public int getLeft() {
			return left;
		}

		public int getTop() {
			return top;
		}

		public int getSizeX() {
			return sizeX;
		}

		public int getSizeY() {
			return sizeY;
		}
	}
	
	private static class FabricArea {
		private Map<String, Integer> field = new HashMap<>();
		private Set<Integer> notOverlappedAreas = new HashSet<>();
		long overlapped = 0;
		
		public void process(Coordinate claim) {
			int xMax = claim.getLeft() + claim.getSizeX();
			int yMax = claim.getTop() + claim.getSizeY();
			for (int x = claim.getLeft(); x < xMax; x++) {
				for (int y = claim.getTop(); y < yMax; y++) {
					String key = x + "_" + y;
					int value = field.compute(key, (k, v) -> (v == null ? 1 : v + 1));
					if (value == 2) {
						this.overlapped++;
					}
				}
			}
		}
		
		public void process2(Coordinate claim) {
			int xMax = claim.getLeft() + claim.getSizeX();
			int yMax = claim.getTop() + claim.getSizeY();
			Set<Integer> conflicts = new HashSet<>();
			for (int x = claim.getLeft(); x < xMax; x++) {
				for (int y = claim.getTop(); y < yMax; y++) {
					String key = x + "_" + y;
					Integer previous = field.put(key, claim.getId());
					if (previous != null) {
						conflicts.add(previous);
					}
				}
			}
			
			if (conflicts.isEmpty()) {
				this.notOverlappedAreas.add(claim.getId());
			} else {
				conflicts.forEach(i -> this.notOverlappedAreas.remove(i));
			}
		}
		
		public long getOverlapped() {
			return overlapped;
		}
		
		public Set<Integer> getNotOverlappedAreas() {
			return notOverlappedAreas;
		}
	}
	
	
	@Test
	public void testPart1() throws IOException {
		FabricArea fabric = new FabricArea();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/day3/day3Part1.txt")))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				Coordinate claim = parseLine(line);
				fabric.process(claim);
			}
		}
		
		System.out.println("Day 3 - Part 1: overlapped: " + fabric.getOverlapped()); // 98005
	}
	
	@Test
	public void testPart2() throws IOException {
		FabricArea fabric = new FabricArea();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/day3/day3Part1.txt")))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				Coordinate claim = parseLine(line);
				fabric.process2(claim);
			}
		}
		Assert.assertEquals(1, fabric.getNotOverlappedAreas().size());
		System.out.println("Day 3 - Part 2: nonOverlapping claim ID: " + fabric.getNotOverlappedAreas().iterator().next()); // 331
	}
	
	@Test
	public void testFabricArea() {
		
		FabricArea fabric = new FabricArea();
		for (String line : TEST_INPUT) {
			Coordinate claim = parseLine(line);
			fabric.process(claim);
		}
		
		Assert.assertEquals(4L, fabric.getOverlapped());
	}
	
	@Test
	public void testNonOerlapped() {
		FabricArea fabric = new FabricArea();
		for (String line : TEST_INPUT) {
			Coordinate claim = parseLine(line);
			fabric.process2(claim);
		}
		
		Set<Integer> result = fabric.getNotOverlappedAreas();
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Integer.valueOf(3), result.iterator().next());
	}
	
	private static Coordinate parseLine(String line) {
		Matcher m = PARSER.matcher(line);
		if (!m.find()) {
			throw new IllegalArgumentException("Invalid input: " + line);
		}
		int id = Integer.parseInt(m.group(1));
		int left = Integer.parseInt(m.group(2));
		int top = Integer.parseInt(m.group(3));
		int xSize = Integer.parseInt(m.group(4));
		int ySize = Integer.parseInt(m.group(5));
		
		return new Coordinate(id, left, top, xSize, ySize);
	}
	
	@Test
	public void testParserRegexp() {
		Matcher m = PARSER.matcher("#1 @ 1,3: 4x4");
		System.out.println(m.find());
		System.out.println(m.group(0));
		System.out.println(m.group(1));
		System.out.println(m.group(2));
		System.out.println(m.group(3));
		System.out.println(m.group(4));
		System.out.println(m.group(5));
		System.out.println(m.group(6));
		System.out.println(m.find());
	}

}
