package org.tondo.advent2016.run.day3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.tondo.advent2016.day3.TriangleBuffer;

public class Day3Test {
	
	
	@Test
	public void testPart1() throws UnsupportedEncodingException, IOException {
		InputStream is = getClass().getResourceAsStream("/day3/day3Part1.txt");
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
			String line = null;
			TriangleBuffer triangelBuffer = new TriangleBuffer();
			while ((line = reader.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					int[] sides = splitLineToTriangleSideLenghts(line);
					triangelBuffer.addDimensions(sides);
				}
			}
			
			System.out.println("Da3 - Part 1: valid triangles: " + triangelBuffer.getRowValidTriangles()); // 1032
			System.out.println("Da3 - Part 2: valid triangles: " + triangelBuffer.getColumnValidTriangles()); // 1838
		}
	}
	
	
	private int[] splitLineToTriangleSideLenghts(String line) {
		List<String> applicableValues = new ArrayList<>();
		for (String p : line.split("\\s+", -1)) {
			if (p != null && !p.trim().isEmpty()) {
				applicableValues.add(p);
			}
		}
		
		if (applicableValues.size() != 3) {
			throw new IllegalArgumentException("Expected line with 3 numbers, but found: '" + line +"'");
		}
		
		int[] sides = new int[applicableValues.size()];
		try {
			for (int i = 0; i < sides.length; i++) {
				sides[i] = Integer.parseInt(applicableValues.get(i));
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Expected line with 3 numbers, but found: '" + line +"'");
		}
		
		return sides;
	}
}
