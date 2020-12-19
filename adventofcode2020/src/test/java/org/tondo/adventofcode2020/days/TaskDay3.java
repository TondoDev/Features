package org.tondo.adventofcode2020.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.tondo.adventofcode2020.DayTaskBase;

public class TaskDay3 extends DayTaskBase {

	public  TaskDay3() {
		super(3);
	}
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("225", getPartOneSolution());
	}
	
	@Test
	public void tesPartTwo() throws Exception {
		assertEquals("1115775000", getPartTwoSolution());
	}
	
	
	@Override
	public String getPartOneSolution() throws Exception {
		List<String> map = null;
		try(BufferedReader r = getPartOneInput()) {
			map = loadMap(r);
		}
		
		final int SLOPE_DOWN = 1;
		final int SLOPE_RIGHT = 3;
		
		int trees = slideDown(map, SLOPE_RIGHT, SLOPE_DOWN);
		return "" + trees;
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		
		List<int[]> slopes =new ArrayList<>();
		//                    R  D
		slopes.add(new int[] {1, 1});
		slopes.add(new int[] {3, 1});
		slopes.add(new int[] {5, 1});
		slopes.add(new int[] {7, 1});
		slopes.add(new int[] {1, 2});
		
		List<String> map = null;
		// input is the same
		try(BufferedReader r = getPartOneInput()) {
			map = loadMap(r);
		}
		
		
		int multRes = 1;
		for (int[] s : slopes) {
			int trees = slideDown(map, s[0], s[1]);
			//System.out.println("Trees: " + trees);
			multRes *= trees;
		}
		
		return "" + multRes;
	}
	
	
	
	public int slideDown(List<String> map, int right, int down) {
		int posX = -right; // initial compensation
		int lines = map.size();
		int sampleWidth = map.get(0).length();
		int trees = 0;
		
		for (int posY = 0; posY < lines; posY+=down) {
			String line = map.get(posY);
			posX = (posX + right) % sampleWidth;
			if (line.charAt(posX) == '#') {
				trees++;
				//System.out.println(setCharAt(posX, 'X', line));
			} else {
				//System.out.println(setCharAt(posX, '0', line));
			}
		}
		
		
		return trees;
	}
	
	private String setCharAt(int index, char c, String str) {
		StringBuilder sb = new StringBuilder(str);
		sb.setCharAt(index, c);
		return sb.toString();
	}
	
	protected  List<String> loadMap(BufferedReader reader) throws IOException {
		String line = null;
		int lineSize = -1; 
		List<String> retVal = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			if (lineSize < 0) {
				lineSize = line.length();
			} else if (lineSize != line.length()) {
				throw new IllegalStateException("Lines of map don't have same size!");
			}
			
			
			retVal.add(line);
		}
		return retVal;
	}
	
	
	@Test
	public void testLoadMap() {
		try (BufferedReader r = testMapReader()) {
			List<String> testMap = loadMap(r);
			testMap.forEach(l -> System.out.println(l));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSampleMapSlide() {
		try (BufferedReader r = testMapReader()) {
			List<String> map = loadMap(r);
			assertEquals(7, slideDown(map, 1, 2));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	protected BufferedReader testMapReader() {
		
		String[] map = new String[] { 	
				"..##.......",
				"#...#...#..",
				".#....#..#.",
				"..#.#...#.#",
				".#...##..#.",
				"..#.##.....",
				".#.#.#....#",
				".#........#",
				"#.##...#...",
				"#...##....#",
				".#..#...#.#"
		};
		
		return createBufferedReader(map);
	}

}
