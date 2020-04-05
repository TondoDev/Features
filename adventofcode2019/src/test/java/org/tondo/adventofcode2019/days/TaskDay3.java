package org.tondo.adventofcode2019.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.tondo.adventofcode2019.DayTaskBase;

public class TaskDay3 extends DayTaskBase{
	
	private static class IntersectionHandler {
		
		private int orX;
		private int orY;
		
		private int intersectionCnt = 0;
		private Integer minDistance = null;
		private Integer minSteps = null;
		
		public IntersectionHandler(int orX, int orY) {
			this.orX = orX;
			this.orY = orY;
		}
		
		public void process(int x, int y, int steps) {
			this.intersectionCnt++;
			int curDist = absDist(this.orX, x) + absDist(this.orY, y);
			if (this.minDistance == null || this.minDistance > curDist) {
				this.minDistance = curDist;
			} 
			
			if (this.minSteps == null || this.minSteps > steps) {
				this.minSteps = steps;
			} 
		}
		
		public int getOrX() {
			return orX;
		}
		
		public int getOrY() {
			return orY;
		}
		
		public Integer getMinDistance() {
			return minDistance;
		}
		
		public Integer getMinSteps() {
			return minSteps;
		}
		
		private int absDist(int a, int b) {
			int r = b - a;
			return r < 0 ? (r*-1) : r; 
		}
	}

	public TaskDay3() {
		super(3);
	}
	
	@Test
	public void testPartOneSolution() throws Exception {
		assertEquals("1983", getPartOneSolution());
	}
	
	@Test
	public void testPartTwoSolution() throws Exception {
		assertEquals("107754", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {

		BufferedReader reader = getPartOneInput();
		Integer minDist = processInput(reader).getMinDistance();

		return "" + minDist;
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		// input is the same
		BufferedReader reader = getPartOneInput();
		Integer minSteps = processInput(reader).getMinSteps();

		return "" + minSteps;
	}

	private IntersectionHandler processInput(BufferedReader reader) throws IOException {
		String line = null;
		int lineNum = 1;
		int orX = 0, orY = 0;
		Map<String, Map<String, Integer>> grid = new HashMap<>();
		
		IntersectionHandler handler = new IntersectionHandler(orX, orY);
		while ((line = reader.readLine()) != null) {
			String[] instructions = line.split(",");
			markPath(grid, ""+lineNum, instructions, handler);
			lineNum++;
		}
		return handler;
	}
	
	
	public void markPath(Map<String, Map<String,Integer>> grid, String lineName, String[] instructions,  IntersectionHandler handler) {
		int curX = handler.getOrX();
		int curY = handler.getOrY();
		int steps = 0;
		for (String op : instructions) {
			char code = op.charAt(0);
			int dist = Integer.parseInt(op.substring(1));
			if (dist == 0) {
				continue;
			}

			if (code == 'R') {
				for (int i = 1; i <= dist; i++) {
					steps++;
					processCoordinates(grid, lineName, handler, curX + i, curY, steps);
				}
				curX += dist;
			} else if (code == 'D') {
				for (int i = 1; i <= dist; i++) {
					steps++;
					processCoordinates(grid, lineName, handler, curX, curY - i, steps);
				}
				curY -= dist;
			} else if (code == 'L') {
				for (int i = 1; i <= dist; i++) {
					steps++;
					processCoordinates(grid, lineName, handler, curX - i, curY, steps);
				}
				curX -= dist;
			} else if (code == 'U') {
				for (int i = 1; i <= dist; i++) {
					steps++;
					processCoordinates(grid, lineName, handler, curX, curY + i, steps);
				}
				curY += dist;
			} else {
				throw new IllegalStateException("Unrecognized instruction: " + code);
			}
		}
	}

	private void processCoordinates(Map<String, Map<String, Integer>> grid, String lineName, IntersectionHandler handler,
			int curX, int curY, int steps) {
		String key = "" + curX + "_" + curY;
		if (addLineLocation(key, lineName, steps, grid)) {
			String[] parts = key.split("_");
			int intX = Integer.parseInt(parts[0]);
			int intY = Integer.parseInt(parts[1]);
			handler.process(intX, intY, sumDistance(grid.get(key)));
		}
	}
	
	private int sumDistance(Map<String, Integer> lineSteps) {
		return lineSteps.values().stream().mapToInt(Integer::intValue).sum();
	}
	
	private boolean addLineLocation(String key, String lineName, int steps, Map<String, Map<String, Integer>> grid) {
		Map<String, Integer> linesAtPlace = grid.get(key);
		if (linesAtPlace == null) {
			linesAtPlace = new HashMap<>();
			linesAtPlace.put(lineName, steps);
			grid.put(key, linesAtPlace);
			return false;
		} else if (linesAtPlace.containsKey(lineName)) {
			// intersection with self
			return false;
		} else {
			linesAtPlace.put(lineName, steps);
			return true;
		}
	}
	
	@Test
	public void testSampeInput1() throws IOException {
		String[] data = new String[] {
				"R75,D30,R83,U83,L12,D49,R71,U7,L72",
				"U62,R66,U55,R34,D71,R55,D58,R83"
		};
		
		BufferedReader reader = createBufferedReader(data);
		IntersectionHandler handler =  processInput(reader);
		assertEquals(Integer.valueOf(159), handler.getMinDistance());
		assertEquals(Integer.valueOf(610), handler.getMinSteps());
		
	}
	
	@Test
	public void testSampeInput2() throws IOException {
		String[] data = new String[] {
				"R8,U5,L5,D3",
				"U7,R6,D4,L4"
		};
		
		BufferedReader reader = createBufferedReader(data);
		IntersectionHandler handler =  processInput(reader);
		assertEquals(Integer.valueOf(6), handler.getMinDistance());
		assertEquals(Integer.valueOf(30), handler.getMinSteps());
		
	}
	
	@Test
	public void testSampeInput3() throws IOException {
		String[] data = new String[] {
				"R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51",
				"U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"
		};
		
		BufferedReader reader = createBufferedReader(data);
		IntersectionHandler handler =  processInput(reader);
		assertEquals(Integer.valueOf(135), handler.getMinDistance());
		assertEquals(Integer.valueOf(410), handler.getMinSteps());
		
	}
}
