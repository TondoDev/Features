package org.tondo.adventofcode2023.days;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.tondo.adventofcode2023.DayTaskBase;

public class TaskDay10 extends DayTaskBase{
	
	private static final Map<Character, int[]> PIPES_MAP;
	private static final int XAXIS = 0;
	private static final int YAXIS = 1;
	
	static {
		Map<Character, int[]> tmp = new HashMap<>();
		// first numbner is connection by X axis, -1 west, 1 east
		// second number is connection by Y axis, -1 north, 1 south
		// value 2 connects straight 2 sides
		tmp.put('|', new int[] {0, 2});
		tmp.put('-', new int[] {2, 0});
		tmp.put('L', new int[] {1, -1});
		tmp.put('J', new int[] {-1, -1});
		tmp.put('7', new int[] {-1, 1});
		tmp.put('F', new int[] {1, 1});
		
		PIPES_MAP = Collections.unmodifiableMap(tmp);
	}

	public TaskDay10() {
		super(10);
	}
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("6931", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("357", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			return ""  + getFarthestPointInLoop(reader);
		}
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			return "" + getTilesEnclosedByLoop(reader);
		}
	}
	
	private long getFarthestPointInLoop(BufferedReader reader) throws IOException {
		PipesMap pipesMap = loadPipesMap(reader);
		Loop loop = findLoop(pipesMap); 
		return loop.firstSteps > loop.secondSteps ? loop.firstSteps : loop.secondSteps;
	}
	
	private long getTilesEnclosedByLoop(BufferedReader reader) throws IOException {
		PipesMap pipesMap = loadPipesMap(reader);
		Loop loop = findLoop(pipesMap); 
		
		// replace start with the pipe
		replaceStartWithPipe(pipesMap);
		
		return getTilesInLoop(loop, pipesMap.pipes);
	}
	
	private void replaceStartWithPipe(PipesMap pipesMap) {
		String lineWithStart = pipesMap.pipes.get(pipesMap.startY);
		lineWithStart = lineWithStart.replace('S', pipesMap.startPipe);
		pipesMap.pipes.set(pipesMap.startY, lineWithStart);
		
	}

	private long getTilesInLoop(Loop loop, List<String> pipes) {
		// convert string coords to integers
		List<int[]> loopPoints = loop.loopPoints.stream().map(s -> s.split("_")).map(sa -> new int[]{
					Integer.parseInt(sa[0].trim()),
					Integer.parseInt(sa[1].trim())}).collect(Collectors.toList());
		
		long tilesCount = 0L;
		int linesCnt = pipes.size();
		for (int lineNumber = 0; lineNumber < linesCnt; lineNumber++) {
			String line = pipes.get(lineNumber);
			List<Integer> pipesLocation = getPipesLocation(loopPoints, line, lineNumber);
			
			for (int c = 0; c < line.length(); c++) {
				// investigation all tiles which are not part of the main loop
				if (!pipesLocation.contains(Integer.valueOf(c)) && isInLoop(c, line, pipesLocation)) {
					tilesCount++;
				}
			}
		}
		
		
		return tilesCount;
	}
	
	private boolean isInLoop(int xpos, String line, List<Integer> pipesLocation) {
		int intersect = 0;
		int idx = 0;
		char kneeFirst = '.';
		while (idx < pipesLocation.size() && xpos > pipesLocation.get(idx)) {
			char charAt = line.charAt(pipesLocation.get(idx));
			// '-' doesn't change in/out state
			if (charAt != '-') {
				// fot this combinations is intersection counted only once (during the first pipe) 
				if (!(charAt=='J' && kneeFirst == 'F') &&  !(charAt=='7' && kneeFirst == 'L')) {
					intersect++;
				}
				kneeFirst = charAt;
				
			}
			idx++;
		}
		// odd intersections means the point is in loop
		return (intersect % 2) != 0;
	}

	private List<Integer> getPipesLocation(List<int[]> loopPoints, String line,  int lineNumber) {
		// skipping horizontal pipes, because they don't change state of in/out state
		return loopPoints.stream().filter(p -> p[YAXIS] == lineNumber)
			//.filter(p -> line.charAt(p[XAXIS]) != '-') 
			.map(p -> p[XAXIS])
			.sorted().collect(Collectors.toList());
	}

	
	
	private PipesMap loadPipesMap(BufferedReader reader) throws IOException {
		PipesMap pipesMap = new PipesMap();
		List<String> pipes = new ArrayList<>();
		int sX = -1, sY = -1;
		String line = null;
		int lineCnt = -1;
		
		while ((line = reader.readLine()) != null) {
			lineCnt++;
			pipes.add(line);
			int startIndex = line.indexOf('S');
			if (startIndex >= 0 && sY < 0) {
				sX = startIndex;
				sY = lineCnt;
			}
		}
		
		pipesMap.pipes = pipes;
		pipesMap.startX = sX;
		pipesMap.startY = sY;
		
		return pipesMap;
	}
	
	private Loop findLoop(PipesMap pipesMap) {
		Loop loop =  startLoop(pipesMap);
		
		while(!isLoopEnded(loop)) {
			nextStape(pipesMap.pipes, loop);
		}
		
		return loop;
	}

	private void nextStape(List<String> pipes, Loop loop) {
		List<int[]> candidatesFirst = getCandidates(pipes, loop.first[XAXIS], loop.first[YAXIS]);
		int[] nextFirst = candidatesFirst.stream().filter(e -> !isSameCoord(e, loop.prevFirst)).findAny().orElseThrow(() -> new IllegalStateException("No next pipe found"));
		loop.addFirst(nextFirst);
		
		List<int[]> candidatesSecond = getCandidates(pipes, loop.second[XAXIS], loop.second[YAXIS]);
		int[] nextSecond = candidatesSecond.stream().filter(e -> !isSameCoord(e, loop.prevSecond)).findAny().orElseThrow(() -> new IllegalStateException("No next pipe found"));
		loop.addSecond(nextSecond);
	}
	
	private boolean isSameCoord(int[] a, int[] b) {
		if (a == null && b == null) {
			return true;
		} else if (a == null || b == null) {
			return false;
		} else {
			return a[XAXIS] == b[XAXIS] && a[YAXIS] == b[YAXIS];
		}
	}

	private boolean isLoopEnded(Loop loop) {
		if (loop.first ==  null || loop.second == null) {
			System.out.println("Strange situation loop not expatended");
			return true;
		}
		// return loop.first[0] == loop.second[0] && loop.first[1] == loop.second[1];
		return isSameCoord(loop.first, loop.second);
	}

	private Loop startLoop(PipesMap pipesMap) {
		List<String> pipes = pipesMap.pipes; 
		int sX = pipesMap.startX;
		int sY = pipesMap.startY;
		
		Loop loop = new Loop();
		List<int[]> candidates = getStartCandidates(pipes, sX, sY);
		
		
		if (candidates.size() == 2) {
			loop.addFirst(candidates.get(0));
			loop.addSecond(candidates.get(1));
		} else {
			throw new IllegalStateException("More than 2 pipse to Start point: " + candidates.size());
		}
		
		// resolves the pipe which can be used at the starting point
		pipesMap.startPipe = getStartAsAPipe(candidates, sX, sY);
	
		
		// set starting point as a previous step of booth paths
		int[] start = new int[] {sX, sY};
		loop.prevFirst = start;
		loop.prevSecond = start;
		loop.loopPoints.add("" + sX + "_" + sY);
		return loop;
	}
	
	/**
	 * From the position of steps adjacent to the start, determine the pipe replacing the start
	 */
	private char getStartAsAPipe(List<int[]> candidates, int sX, int sY) {
		int[] c1 = candidates.get(0);
		int[] c2 = candidates.get(1);
		
		int cnt = 0;
		while(cnt < 2) {
			if (c1[0] < sX && c2[0] > sX) {
				return '-';
			} else if (c1[1] < sY && c2[1] > sY) {
				return '|';
			} else if (c1[0] < sX && c2[1] > sY) {
				return '7';
			} else if (c1[0] < sX && c2[1] < sY) {
				return 'J';
			} else if (c1[0] > sX && c2[1] > sY) {
				return 'F';
			} else if (c1[0] > sX && c2[1] < sY) {
				return 'L';
			}
			
			int[] tmp = c1;
			c1 = c2;
			c2 = tmp;
			cnt++;
		}
		
		
		throw new IllegalStateException("Starting pipe not recognized");
		
	}

	private List<int[]> getCandidates(List<String> pipes, int sX, int sY) {
		List<int[]> candidates = new ArrayList<>();
		
		char currentChar = pipes.get(sY).charAt(sX);
		int[] currentMapping = PIPES_MAP.get(currentChar);
		
		// check from top (north)
		if (sY > 0) {
			char p = pipes.get(sY - 1).charAt(sX);
			if (PIPES_MAP.containsKey(p)) {
				int[] mapping = PIPES_MAP.get(p);
				// current node must be connectable from north and candidate from south
				if ( fromNorth(currentMapping[YAXIS]) && fromSouth(mapping[YAXIS])) {
					candidates.add(new int[] {sX, sY - 1});
				}
			}
		}
		
		// check for bottom (south)
		if (sY < pipes.size() - 1) {
			char p = pipes.get(sY + 1).charAt(sX);
			if (PIPES_MAP.containsKey(p)) {
				int[] mapping = PIPES_MAP.get(p);
				if (fromSouth(currentMapping[YAXIS]) && fromNorth(mapping[YAXIS])) {
					candidates.add(new int[] {sX, sY + 1});
				}
			}
		}
		
		// check for left side (west)
		if (sX > 0) {
			char p = pipes.get(sY).charAt(sX - 1);
			if (PIPES_MAP.containsKey(p)) {
				int[] mapping = PIPES_MAP.get(p);
				if (fromWest(currentMapping[XAXIS]) && fromEast(mapping[XAXIS])) {
					candidates.add(new int[] {sX - 1, sY});
				}
			}
		}
		
		// check for the right side (east)
		if (sX < pipes.get(sY).length() - 1) {
			char p = pipes.get(sY).charAt(sX + 1);
			if (PIPES_MAP.containsKey(p)) {
				int[] mapping = PIPES_MAP.get(p);
				if (fromEast(currentMapping[XAXIS]) && fromWest(mapping[XAXIS])) {
					candidates.add(new int[] {sX + 1, sY});
				}
			}
		}
		
		if (candidates.size() != 2) {
			System.out.println("Candaidates: " + candidates.size());
			printSurounding(pipes, sX, sY);
		}
		
		return candidates;
	}
	
	private boolean fromNorth(int yMap) {
		return yMap == 2 || yMap == -1;
	}
	
	private boolean fromSouth(int yMap) {
		return yMap == 2 || yMap == 1;
	}
	
	private boolean fromWest(int xMap) {
		return xMap == 2 || xMap == -1;
	}
	
	private boolean fromEast(int xMap) {
		return xMap == 2 || xMap == 1;
	}
	
	private List<int[]> getStartCandidates(List<String> pipes, int sX, int sY) {
		List<int[]> candidates = new ArrayList<>();
		// check from top (north)
		if (sY > 0) {
			char p = pipes.get(sY - 1).charAt(sX);
			if (PIPES_MAP.containsKey(p)) {
				int[] mapping = PIPES_MAP.get(p);
				if (mapping[YAXIS] == 2 || mapping[YAXIS] == 1) {
					candidates.add(new int[] {sX, sY - 1});
				}
			}
		}
		
		// check for bottom (south)
		if (sY < pipes.size() - 1) {
			char p = pipes.get(sY + 1).charAt(sX);
			if (PIPES_MAP.containsKey(p)) {
				int[] mapping = PIPES_MAP.get(p);
				if (mapping[YAXIS] == 2 || mapping[YAXIS] == -1) {
					candidates.add(new int[] {sX, sY + 1});
				}
			}
		}
		
		// check for left side (west)
		if (sX > 0) {
			char p = pipes.get(sY).charAt(sX - 1);
			if (PIPES_MAP.containsKey(p)) {
				int[] mapping = PIPES_MAP.get(p);
				if (mapping[XAXIS] == 2 || mapping[XAXIS] == 1) {
					candidates.add(new int[] {sX - 1, sY});
				}
			}
		}
		
		// check for the right side (east)
		if (sX < pipes.get(sY).length() - 1) {
			char p = pipes.get(sY).charAt(sX + 1);
			if (PIPES_MAP.containsKey(p)) {
				int[] mapping = PIPES_MAP.get(p);
				if (mapping[XAXIS] == 2 || mapping[XAXIS] == -1) {
					candidates.add(new int[] {sX + 1, sY});
				}
			}
		}
		
		return candidates;
	}
	
	private void printSurounding(List<String> pipes, int sX, int sY) {
		for (int y = -1; y<=1; y++) {
			int targetY = sY + y;
			if (targetY < 0 || targetY >= pipes.size()) {
				System.out.println("ooo");
				continue;
			}
			
			String line = pipes.get(targetY);
			for (int x = -1; x <= 1; x++) {
				int targetX = sX + x;
				if (targetX >= 0 && targetX < line.length()) {
					System.out.print(line.charAt(targetX));
				} else {
					System.out.print('o');
				}
			}
			System.out.println();
		}
		System.out.println("\n");
	}

	private static final String[] SAMPLE_INPUT_P1 = {
			"..F7.",
			".FJ|.",
			"SJ.L7",
			"|F--J",
			"LJ..."
	};
	
	
	@Test
	public void testSampleP1() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1);
		assertEquals(8L, getFarthestPointInLoop(reader));
	}
	
	@Test
	public void testGetCandidates() {
		List<String> pipes = new ArrayList<>(Arrays.asList(
				"L7L",
				"LL7",
				"7FJ"
				));
		printSurounding(pipes, 1, 1);
		assertEquals(2, getCandidates(pipes, 1, 1).size());
	}
	//[1, 2, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
	
	@Test
	public void testIsInLoop() {
		List<Integer> pipesLoc = Arrays.asList(1, 2, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
		assertFalse(isInLoop(16, ".|F--7||||||||FJ....", pipesLoc));
	}
	
	
	private static final String[] SAMPLE_INPUT_P2a = {
			".F----7F7F7F7F-7....",
			".|F--7||||||||FJ....",
			".||.FJ||||||||L7....",
			"FJL7L7LJLJ||LJ.L-7..",
			"L--J.L7...LJS7F-7L7.",
			"....F-J..F7FJ|L7L7L7",
			"....L7.F7||L7|.L7L7|",
			".....|FJLJ|FJ|F7|.LJ",
			"....FJL-7.||.||||...",
			"....L---J.LJ.LJLJ..."
	};
	
	
	@Test
	public void testSampleInputP2a() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P2a);
		assertEquals(8L, getTilesEnclosedByLoop(reader));
	}
	
	
	public static final String[] SAMPLE_INPUT_P2b = {
			"...........",
			".S-------7.",
			".|F-----7|.",
			".||.....||.",
			".||.....||.",
			".|L-7.F-J|.",
			".|..|.|..|.",
			".L--J.L--J.",
			"..........."
	};  
	
	@Test
	public void testSampleInputP2b() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P2b);
		assertEquals(4L, getTilesEnclosedByLoop(reader));
	}
	
	private static final String[] SAMPLE_INPUT_P2c = {
			"FF7FSF7F7F7F7F7F---7",
			"L|LJ||||||||||||F--J",
			"FL-7LJLJ||||||LJL-77",
			"F--JF--7||LJLJIF7FJ-",
			"L---JF-JLJIIIIFJLJJ7",
			"|F|F-JF---7IIIL7L|7|",
			"|FFJF7L7F-JF7IIL---7",
			"7-L-JL7||F7|L7F-7F7|",
			"L.L7LFJ|||||FJL7||LJ",
			"L7JLJL-JLJLJL--JLJ.L"
	};
	
	@Test
	public void testSampleInputP2c() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P2c);
		assertEquals(10L, getTilesEnclosedByLoop(reader));
	}
	
	private static class PipesMap {
		List<String> pipes;
		int startX;
		int startY;
		
		char startPipe;
	}

	private static class Loop {
		int[] first = null;
		int[] second = null;
		int[] prevFirst = null;
		int[] prevSecond = null;
		
		long firstSteps = 0L;
		long secondSteps = 0L;
		
		Set<String> loopPoints = new HashSet<>();
		
		
		
		private void addPoint(int[] step) {
			this.loopPoints.add("" + step[XAXIS] + "_" + step[YAXIS]);
		}
		
		public void addFirst(int[] step) {
			this.prevFirst = this.first;
			this.first = step;
			this.firstSteps++;
			addPoint(step);
		}
		
		public void addSecond(int[] step) {
			this.prevSecond = this.second;
			this.second = step;
			this.secondSteps++;
			addPoint(step);
		}
	}
}
