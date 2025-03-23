package org.tondo.adventofcode2023.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.tondo.adventofcode2023.DayTaskBase;

public class TaskDay11 extends DayTaskBase {
	
	public TaskDay11() {
		super(11);
	}
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("9648398", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("618800410814", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {
		try (BufferedReader reader = getPartOneInput()) {
			// expansion rate = 2 ( one empty space become two empty spaces)
			return "" + getSumOfLenthBetweenGalaxies(reader, 2);
		}
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		try (BufferedReader reader = getPartOneInput()) {
			// expansion rate = 1000000 ( one empty space become milion empty spaces)
			return "" + getSumOfLenthBetweenGalaxies(reader, 1000000);
		}
	}
	
	private long getSumOfLenthBetweenGalaxies(BufferedReader reader, int expansionRate) throws IOException {
		SpaceMap spaceMap = new SpaceMap();
		String line = null;
		
		while ((line = reader.readLine()) != null) {
			spaceMap.addSpaceLine(line);
		}
		
		List<int[]> galaxiesInExpandedSpace = spaceMap.getGalaxiesInExpandedSpace(expansionRate);
		int galSize = galaxiesInExpandedSpace.size();
		long sumOfDistances = 0L;
		for (int fromGal = 0; fromGal < galSize - 1; fromGal++) {
			for (int toGal = fromGal + 1; toGal < galSize; toGal++) {
				sumOfDistances += orthoDistance(galaxiesInExpandedSpace.get(fromGal), galaxiesInExpandedSpace.get(toGal));
			}
		}
		return sumOfDistances;
	}
	
	private static final String[]  SAMPLE_INPUT_P1 = {
			"...#......",
			".......#..",
			"#.........",
			"..........",
			"......#...",
			".#........",
			".........#",
			"..........",
			".......#..",
			"#...#....."
	};
	
	@Test
	public void testSampleInputP1() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1);
		assertEquals(374L, getSumOfLenthBetweenGalaxies(reader, 2));
	}
	
	@Test
	public void testSampleInputP2a() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1);
		assertEquals(1030L, getSumOfLenthBetweenGalaxies(reader, 10));
	}
	
	@Test
	public void testSampleInputP2b() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1);
		assertEquals(8410L, getSumOfLenthBetweenGalaxies(reader, 100));
	}
	
	
	private long orthoDistance(int[] gal1, int[] gal2) {
		int xDist = absDist(gal2[0], gal1[0]);
		int yDist = absDist(gal2[1], gal1[1]);
		return xDist + yDist;
	}
	
	private int absDist(int p1, int p2) {
		int dist = p2 - p1;
		return dist < 0 ? dist*-1 : dist;
	}


	private static class SpaceMap {
		// [x,y]
		private List<int[]> galaxies = new ArrayList<>();
		private boolean[] emptyColumns;
		private List<Boolean> emptyRows = new ArrayList<>();
		
		private int lineIdx = -1;
		
		public void addSpaceLine(String line) {
			this.lineIdx++;
			if (emptyColumns == null) {
				emptyColumns = new boolean[line.length()];
				Arrays.fill(emptyColumns, true);
			}
			List<int[]> galaxiesForLine = getGalaxiesForLine(this.lineIdx, line);
			// store galaxies
			this.galaxies.addAll(galaxiesForLine);
			// (un)marking columns which have galaxies
			for (int[] gal : galaxiesForLine) {
				this.emptyColumns[gal[0]] = false;
			}
			
			// this line doesn't contain galaxies, so it is empty and expandable
			this.emptyRows.add(galaxiesForLine.isEmpty());
		}
		
		private List<int[]> getGalaxiesForLine(int lineIdx, String line) {
			List<int[]> galaxiesForLine = new ArrayList<>();
			int len = line.length();
			
			for (int i = 0; i < len; i++) {
				if (line.charAt(i) == '#') {
					galaxiesForLine.add(new int[] {i, lineIdx});
				}
			}
			return galaxiesForLine;
		}
		
		public List<int[]> getGalaxiesInExpandedSpace(int expansionRate) {
			List<int[]> expandedGalaxies = new ArrayList<>();
			
			for (int[] normalGal : this.galaxies) {
				int[] expGal = expand(normalGal, expansionRate);
				expandedGalaxies.add(expGal);
			}
			
			return expandedGalaxies;
		}

		private int[] expand(int[] normalGal, int expansionRate) {
			// x expansion is based on free columns;
			int freeColumns = 0;
			for (int i = 0; i < normalGal[0]; i++) {
				if (this.emptyColumns[i]) {
					freeColumns++;
				}
			}
			
			int freeRows = 0;
			for (int i = 0; i < normalGal[1]; i++) {
				if (this.emptyRows.get(i)) {
					freeRows++;
				}
			}
			// rate means to how many spaces will transform one empty space 
			int coef = expansionRate - 1;
			// free rows/columns are doubled
			return new int[] {normalGal[0] + coef*freeColumns, normalGal[1] + coef*freeRows};
		}
		
	}
}
