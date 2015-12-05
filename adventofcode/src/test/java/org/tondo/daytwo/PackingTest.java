package org.tondo.daytwo;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import org.junit.Test;

public class PackingTest {

	@Test
	public void testPackingAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/daytwo/boxes.txt");
		assertNotNull(is);
		BigDecimal totalPaper = getTotalPackingPaper(is);
		System.out.println("Day 2. First part: Total paper needed: " + totalPaper);
		assertEquals("Total paper needed:", 1598415,  totalPaper.intValue());
	}
	
	@Test
	public void testPackagingSamples() throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream("2x3x4".getBytes());
		assertEquals(58, getTotalPackingPaper(is).intValue());
		
		is = new ByteArrayInputStream("1x1x10".getBytes());
		assertEquals(43, getTotalPackingPaper(is).intValue());
	}
	
	
	private BigDecimal getTotalPackingPaper(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		BigDecimal totoalPaper = BigDecimal.ZERO;
		String line = null;
		while((line = reader.readLine()) != null) {
			String[] edges = line.split("x");
			int packageArea = paperPerBox(Integer.valueOf(edges[0]), 
					Integer.valueOf(edges[1]), 
					Integer.valueOf(edges[2]));
			
			totoalPaper = totoalPaper.add(new BigDecimal(packageArea));
		}
		
		
		return totoalPaper;
	}
	
	private int paperPerBox(int edgeA, int edgeB, int edgeC) {
		int planeA = edgeA*edgeB;
		int planeB = edgeA*edgeC;
		int planeC = edgeC*edgeB;
		
		int minPlane = Math.min(planeA, planeB);
		minPlane = Math.min(minPlane, planeC);
		
		return 2*planeA + 2*planeB + 2*planeC + minPlane; 
	}
}
