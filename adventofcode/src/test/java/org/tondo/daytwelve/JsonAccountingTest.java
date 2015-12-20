package org.tondo.daytwelve;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class JsonAccountingTest {
	
	@Test
	public void testAccountingAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/daytwelve/partOne.txt");
		assertNotNull(is);
		
		
		long sum = getSumOfNubers(is);
		System.out.println("Day 12. First part: Sum of integer in jsons: " + sum);
		assertEquals("Day 12. First part: Sum of integer in jsons:", 191164L,  sum);
		
		
		// --- part 2, same input
		is = getClass().getResourceAsStream("/daytwelve/partOne.txt");
		assertNotNull(is);
		
		AccountProcessor ac = new AccountProcessor();
		sum = ac.getBalance(is);
		System.out.println("Day 12. Second part: Sum of integer in jsons: " + sum);
		assertEquals("Day 12. Second part: Sum of integer in jsons:", 87842,  sum);
	}
	
	@Test
	public void testAccountingSamples() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("[1,2,3] and {\"a\":2,\"b\":4}");
		ByteArrayInputStream is = new ByteArrayInputStream(sb.toString().getBytes());
		
		assertEquals(12L, getSumOfNubers(is));
	}
	
	@Test
	public void testMyJsonParserForArray() throws IOException {
		ByteArrayInputStream is = null;
		long balance = 0L;
		AccountProcessor processor = new AccountProcessor();
		
		is = new ByteArrayInputStream("[1,2,3]".getBytes());
		balance = processor.getBalance(is);
		assertEquals(6L, balance);
		
		is = new ByteArrayInputStream("[]".getBytes());
		balance = processor.getBalance(is);
		assertEquals(0L, balance);
		
		is = new ByteArrayInputStream("[1,2,\"pepek\",3]".getBytes());
		balance = processor.getBalance(is);
		assertEquals(6L, balance);
		
		try {
			is = new ByteArrayInputStream("[1,2,\"pepek\",3]".getBytes());
			balance = processor.getBalance(is);
			assertEquals(6L, balance);
			fail("Syntax error expected");
		} catch (IllegalStateException e) {}
	
	}
	
	@Test
	public void testMyJsonParserForObject() throws IOException {
		ByteArrayInputStream is = null;
		long balance = 0L;
		AccountProcessor processor = new AccountProcessor();
		
		is = new ByteArrayInputStream("{}".getBytes());
		balance = processor.getBalance(is);
		assertEquals(0L, balance);
		
		is = new ByteArrayInputStream("{\"a\":2,\"b\":4}".getBytes());
		balance = processor.getBalance(is);
		assertEquals(6L, balance);
		
		is = new ByteArrayInputStream("{\"a\":2, \"x\" : \"pipik\", \"b\":4}".getBytes());
		balance = processor.getBalance(is);
		assertEquals(6L, balance);
		
		is = new ByteArrayInputStream("{\"a\":2, \"x\" : \"red\", \"b\":4}".getBytes());
		balance = processor.getBalance(is);
		assertEquals(0L, balance);
	}
	
	@Test
	public void testMyJsonParserComplex() throws IOException {
		ByteArrayInputStream is = null;
		long balance = 0L;
		AccountProcessor processor = new AccountProcessor();
		
		is = new ByteArrayInputStream("[1,2,3, {\"a\":2, \"x\" : \"pipik\", \"b\":4}]".getBytes());
		balance = processor.getBalance(is);
		assertEquals(12L, balance);
		
		is = new ByteArrayInputStream("[1,2,3, {\"a\":2, \"x\" : \"red\", \"b\":4}]".getBytes());
		balance = processor.getBalance(is);
		assertEquals(6L, balance);
		
		is = new ByteArrayInputStream("{\"a\":2, \"x\" : \"d\", \"b\": [1,2,\"red\", 3]}".getBytes());
		balance = processor.getBalance(is);
		assertEquals(8L, balance);
		
		is = new ByteArrayInputStream("{\"a\":2, \"x\" : {\"aa\":2,\"bbb\":4}, \"b\":4}".getBytes());
		balance = processor.getBalance(is);
		assertEquals(12L, balance);
		
		is = new ByteArrayInputStream("{\"a\":2, \"x\" : {\"aa\":2,\"bbb\":\"red\"}, \"b\":4}".getBytes());
		balance = processor.getBalance(is);
		assertEquals(6L, balance);
	}
	
	@Test
	public void testNumberRegext() {
		String input = "494 fdf -52 fe dsf95f";
		String[] result = new String[3];
		
		Pattern p = Pattern.compile("-?\\d+");
		Matcher m = p.matcher(input);
		
		int i = 0;
		while(m.find()) {
			result[i++] = m.group();
		}
		
		assertEquals(3, i);
		assertEquals("494", result[0]);
		assertEquals("-52", result[1]);
		assertEquals("95", result[2]);
	}
	
	/**
	 * Solution based on finding numbers in text, completely ignoring that it is in json format.
	 * Rely on statement in assignment that numbers are not contained in strings 
	 */
	private long getSumOfNubers(InputStream is) throws IOException {
		long sum = 0;
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while((line = reader.readLine()) != null) {
			sb.append(line);
		}
		
		Pattern p = Pattern.compile("-?\\d+");
		Matcher m = p.matcher(sb);
		
		while (m.find()) {
			sum += Long.parseLong(m.group());
		}
		
		
		return sum;
	}

}
