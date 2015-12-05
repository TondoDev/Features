package org.tondo.daytwo;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import org.junit.Test;

public class RibbonTest {
	
	@Test
	public void testRibbonAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/daytwo/ribbon.txt");
		assertNotNull(is);
		BigDecimal totalRibbon = getTotalRibbon(is);
		System.out.println("Day 2. Second part: Total ribbon needed: " + totalRibbon);
		assertEquals("Total ribbon needed:", 3812909,  totalRibbon.intValue());
	}
	
	@Test
	public void testRibbonSamples() throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream("2x3x4".getBytes());
		assertEquals(34, getTotalRibbon(is).intValue());
		
		is = new ByteArrayInputStream("1x1x10".getBytes());
		assertEquals(14, getTotalRibbon(is).intValue());
	}
	
	
	private BigDecimal getTotalRibbon(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		BigDecimal totoalRibbon = BigDecimal.ZERO;
		String line = null;
		while((line = reader.readLine()) != null) {
			String[] edges = line.split("x");
			int packageRibbon = ribbonPerPackage(Integer.valueOf(edges[0]), 
					Integer.valueOf(edges[1]), 
					Integer.valueOf(edges[2]));
			
			totoalRibbon = totoalRibbon.add(new BigDecimal(packageRibbon));
		}
		
		
		return totoalRibbon;
	}
	
	
	private int ribbonPerPackage(int a, int b, int c) {
		int bowLength = a*b*c; // masla
		
		int shortestDistance = a+b+c - Math.max(a, Math.max(b, c));
		shortestDistance *= 2;
		return shortestDistance + bowLength;
	}

}
