package org.tondo.Java7Features;

 import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Test cases for demonstation of switch by string statement
 * @author TondoDev
 *
 */
public class StringSwitchTest {

	
	/**
	 * Test for Java 7 string based <code>switch</code> statement
	 */
	@Test
	public void testSwitchString() {
		String[] choices  = new String[]{"lopta", "Slnko", "LOPTA", "unknown", ""};
		String[] result = new String[choices.length];
		
		int index = 0;
		for (String choice : choices) {
			switch (choice) {
			case "Slnko":
				result[index] = "S";
				break;
				
			case "lopta":
				result[index] = "L";
				break;
				
			case "":
				result[index] = "E";
				break;
				
			case "LOPTA":
				result[index] = "VL";
				break;
				
			case "something":
				result[index] = "X";
				break;
				
			default:
				result[index] = "NF";
			}
			
			index++;
		}
		
		assertArrayEquals(new String[]{"L", "S", "VL", "NF", "E"}, result);
	}
	
	/**
	 * When null is passed to switch statement, NPE is thrown
	 */
	@Test(expected=NullPointerException.class)
	public void testSwitchWithNull() {
		String choice = getNull();
		switch (choice) {
		case "Slnko":
			System.out.println("Slniecko");
			break;
			
		case "lopta":
			System.out.println("Lopta");
			break;
		
		default:
			System.out.println("Default handling");
			break;
		}
	}
	
	/**
	 * In <code>case</code> statement can only be constant expression.
	 * Case expressions can't be null or duplicit
	 */
	@Test
	public void testSwitchExpression() {
		final String alternative = "CH";
		int result = 0;
		
		switch("CHB") {
		case alternative + "B":
				result = 1;
			break;
		default:
				result = 2;
			break;
			
		}
		assertEquals(1, result);
	}
	
	private String getNull() {
		return null;
	}
	
	@Test
	public void testSwithcDefaultNotLast() {
		List<String> collector = new ArrayList<>();
		
		switch("pes") {
		case "mama":
			collector.add("A");
			break;
		default:
			collector.add("B");
			break;
		case "pes":
			collector.add("C");
		}
		
		assertEquals(Arrays.asList("C"), collector);
	}
	
	@Test
	public void testSwithcDefaultNotLastWithoutBreak() {
		List<String> collector = new ArrayList<>();
		
		switch("xxx") {
		case "mama":
			collector.add("A");
			break;
		default:
			collector.add("B");
		case "pes":
			collector.add("C");
		}
		
		assertEquals(Arrays.asList("B", "C"), collector);
	}
			
}
