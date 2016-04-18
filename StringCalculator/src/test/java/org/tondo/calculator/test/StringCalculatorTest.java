package org.tondo.calculator.test;


import static org.junit.Assert.*;

import org.junit.Test;
import org.tondo.calculator.CalculatorException;
import org.tondo.calculator.StringCalculator;

/**
 * 
 * @author TondoDev
 *
 */
public class StringCalculatorTest {

	private StringCalculator calculator = new StringCalculator();
	
	@Test
	public void testEmptyInput() {
		assertEquals("For null is result zero", 0L,  calculator.calculeteSum(null).longValue());
		assertEquals("For empty string is result zero", 0L,  calculator.calculeteSum("").longValue());
	}
	
	@Test
	public void testBasicSum() {
		assertEquals("Single value", 5L,  calculator.calculeteSum("5").longValue());
		assertEquals("space delimited values", 7L,  calculator.calculeteSum("1 1 2 3").longValue());
		assertEquals("hashtag delimited values", 7L,  calculator.calculeteSum("1#1#2#3").longValue());
		assertEquals("values with + sign", 7L,  calculator.calculeteSum("1 +1 +2 3").longValue());
		assertEquals("test boundary values ", 103L,  calculator.calculeteSum("1 100 2 0").longValue());
	}
	
	@Test
	public void testIgnoringBigNumbers() {
		assertEquals("Value is ignored", 6L,  calculator.calculeteSum("1 101 2 3").longValue());
		assertEquals("Big numbers with + sign", 199L,  calculator.calculeteSum("+100 +99 +200").longValue());
		assertEquals("Ignoring all", 0L,  calculator.calculeteSum("101 99999999999999999 200").longValue());
	}
	
	@Test
	public void testNegativeNumbers() {
		// single negative number
		try {
			calculator.calculeteSum("-5");
		} catch(CalculatorException e) {
			assertEquals("Negative numbers are not allowed!", e.getMessage());
		}
		
		// negative number after some positive
		try {
			calculator.calculeteSum("1 2 3 -5");
		} catch(CalculatorException e) {
			assertEquals("Negative numbers are not allowed!", e.getMessage());
		}
	}
	
	@Test
	public void testIncorrectSyntax() {
		// more delimiters between numbers
		try {
				calculator.calculeteSum("2  1");
		} catch(CalculatorException e) {
				assertEquals("Input not valid!", e.getMessage());
		}
		
		// not uniform delimiter
		try {
			calculator.calculeteSum("2 1,3");
		} catch (CalculatorException e) {
			assertEquals("Used invalid delimiter ',', expected ' '!", e.getMessage());
		}
		
		// with some text
		try {
			calculator.calculeteSum("hallo");
		} catch (CalculatorException e) {
			assertEquals("Input not valid!", e.getMessage());
		}
	}
	
	@Test
	public void testInputWithMoreNumbers() {
		// arithmetic progression from 0 to 100
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= 100; i++) {
			sb.append(i);
			if (i <100) {
				sb.append(" ");
			}
		}
		
		assertEquals("Many numbers in input string", 5050L,  calculator.calculeteSum(sb.toString()).longValue());
	}
	
}
