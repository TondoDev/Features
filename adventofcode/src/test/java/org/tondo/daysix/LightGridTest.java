package org.tondo.daysix;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

public class LightGridTest {
	
	private static enum Operation {
		TOGGLE,
		TURNON,
		TURNOFF
	}
	
	private static class LightsAction {
		Operation op;
		int startX;
		int startY;
		int endX;
		int endY;
		
		public LightsAction(Operation operation, int startX, int startY, int endX, int endY) {
			this.op = operation;
			this.startX = startX;
			this.endX = endX;
			this.startY = startY;
			this.endY = endY;
		}
	}

	
	@Test
	public void testLightGridAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/daysix/lighsgrid.txt");
		assertNotNull(is);
		int litLightsCount = getLitBulbsCount(is);
		System.out.println("Day 6. First part: Lit lights count: " + litLightsCount);
		assertEquals("Lit lights count:", 543903,  litLightsCount);
	}
	
	
	@Test
	public void testLightGridSamples() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("turn off 448,208 through 645,684\n");
		InputStream input = new ByteArrayInputStream(sb.toString().getBytes());
		assertEquals("all bullbs are turned off", 0, getLitBulbsCount(input));
		
		sb.append("turn on 100,100 through 100,102\n");
		input = new ByteArrayInputStream(sb.toString().getBytes());
		assertEquals(3, getLitBulbsCount(input));
		
		sb.append("toggle 90,90 through 110,110\n");
		input = new ByteArrayInputStream(sb.toString().getBytes());
		assertEquals(438, getLitBulbsCount(input));
		
		sb.append("toggle 90,90 through 99,99\n");
		input = new ByteArrayInputStream(sb.toString().getBytes());
		assertEquals(338, getLitBulbsCount(input));
	}
	
	@Test
	public void testLightBrightnessAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/daysix/brightness.txt");
		assertNotNull(is);
		long totalBrightness = getLightGridBightness(is);
		System.out.println("Day 6. Second part: Total brightnesst: " + totalBrightness);
		assertEquals("Total brightnesst:", 14687245L,  totalBrightness);
	}
	
	@Test
	public void testBrightnessSamples() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("turn off 448,208 through 645,684\n");
		InputStream input = new ByteArrayInputStream(sb.toString().getBytes());
		assertEquals("all bullbs are turned off", 0, getLightGridBightness(input));
		
		sb.append("turn on 100,100 through 100,102\n");
		input = new ByteArrayInputStream(sb.toString().getBytes());
		assertEquals(3, getLightGridBightness(input));
		
		sb.append("toggle 90,90 through 110,110\n");
		input = new ByteArrayInputStream(sb.toString().getBytes());
		assertEquals(885, getLightGridBightness(input));
		
		sb.append("turn off 90,90 through 99,99\n");
		input = new ByteArrayInputStream(sb.toString().getBytes());
		assertEquals(785, getLightGridBightness(input));
		
		sb.append("turn off 90,90 through 90,99\n");
		input = new ByteArrayInputStream(sb.toString().getBytes());
		assertEquals(775, getLightGridBightness(input));
	}
	
	@Test
	public void testParsing() {
		LightsAction action = parserInput("turn on 952,417 through 954,845");
		assertEquals("operation", Operation.TURNON, action.op);
		assertEquals("Start X", 952, action.startX);
		assertEquals("Start Y", 417, action.startY);
		assertEquals("End X", 954, action.endX);
		assertEquals("End Y", 845, action.endY);
		
		action = parserInput("turn off 367,664 through 595,872");
		assertEquals("operation", Operation.TURNOFF, action.op);
		assertEquals("Start X", 367, action.startX);
		assertEquals("Start Y", 664, action.startY);
		assertEquals("End X", 595, action.endX);
		assertEquals("End Y", 872, action.endY);
		
		action = parserInput("toggle 532,276 through 636,847");
		assertEquals("operation", Operation.TOGGLE, action.op);
		assertEquals("Start X", 532, action.startX);
		assertEquals("Start Y", 276, action.startY);
		assertEquals("End X", 636, action.endX);
		assertEquals("End Y", 847, action.endY);
	}
	
	private int getLitBulbsCount(InputStream is) throws IOException {
		int count = 0;
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		boolean[][] lightGrid = new boolean[1000][1000];
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			LightsAction action = parserInput(line);
			
			for (int y = action.startY; y <= action.endY; y++) {
				for (int x = action.startX; x <= action.endX; x++) {
					if (action.op == Operation.TURNON && !lightGrid[y][x]) {
						count++;
						lightGrid[y][x] = true;
						
					} else if (action.op == Operation.TURNOFF && lightGrid[y][x]) {
						count--;
						lightGrid[y][x] = false;
						
					} else if (action.op == Operation.TOGGLE) {
						lightGrid[y][x] = !lightGrid[y][x];
						count += (lightGrid[y][x] ? 1 : -1); 
					} // else nothing to do
				}
			}
		}
		
		return count;
	}
	
	private long getLightGridBightness(InputStream is) throws IOException {
		long brightness = 0;
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		int[][] lightGrid = new int[1000][1000];
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			LightsAction action = parserInput(line);
			
			for (int y = action.startY; y <= action.endY; y++) {
				for (int x = action.startX; x <= action.endX; x++) {
					if (action.op == Operation.TURNON) {
						brightness++;
						lightGrid[y][x] +=1;
						
					} else if (action.op == Operation.TURNOFF) {
						if (lightGrid[y][x] > 0) {
							brightness--;
							lightGrid[y][x] -= 1;
						}
						
					} else if (action.op == Operation.TOGGLE) {
						brightness += 2; 
						lightGrid[y][x] +=2;
					} // else nothing to do
				}
			}
		}
		
		return brightness;
	}
	
	
	private LightsAction parserInput(String line) {
		String[] tokens = line.split("\\s+");
		Operation lightOp = null;
		
		if (tokens.length < 4 || tokens.length > 5) {
			throw new IllegalArgumentException("Bad input format");
		}
		
		String[] startCorner = tokens[tokens.length - 3].split(",");
		int sx = Integer.parseInt(startCorner[0]);
		int sy = Integer.parseInt(startCorner[1]);
		
		String[] endCorner = tokens[tokens.length - 1].split(",");
		int ex = Integer.parseInt(endCorner[0]);
		int ey = Integer.parseInt(endCorner[1]);
		
		if ("toggle".equals(tokens[0])) {
			lightOp = Operation.TOGGLE;
		} else if("turn".equals(tokens[0])){
			if ("on".equals(tokens[1])) {
				lightOp = Operation.TURNON;
			} else if ("off".equals(tokens[1])) {
				lightOp = Operation.TURNOFF;
			} else {
				throw new IllegalStateException("Illegal turn operation: " + tokens[1]);
			}
		} else {
			throw new IllegalStateException("Illegal operation : " + tokens[0]);
		}
		
		return new LightsAction(lightOp, sx, sy, ex, ey);
	}
	
	
}
