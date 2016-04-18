package org.zsiros.calculator.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.zsiros.calculator.CalculatorException;
import org.zsiros.calculator.IntegerTokenizer;

/**
 * 
 * @author TondoDev
 *
 */
public class TokenizerTest {
	
	@Test
	public void testPositiveCases() {
		
		// empty string
		IntegerTokenizer tokenizer = new IntegerTokenizer("");
		List<String> parsedTokens = new ArrayList<>(); 
		String token = null;
		while ((token = tokenizer.getNext()) != null) {
			parsedTokens.add(token);
		}
		assertTrue("For empty input no tokens produced", parsedTokens.isEmpty());
		
		
		// null
		tokenizer = new IntegerTokenizer(null);
		parsedTokens = new ArrayList<>(); 
		token = null;
		while ((token = tokenizer.getNext()) != null) {
			parsedTokens.add(token);
		}
		assertTrue("For null input no tokens produced", parsedTokens.isEmpty());
		
		// single token
		tokenizer = new IntegerTokenizer("1");
		parsedTokens = new ArrayList<>(); 
		token = null;
		while ((token = tokenizer.getNext()) != null) {
			parsedTokens.add(token);
		}
		assertEquals("Single token is generated",
				Arrays.asList("1"),
				parsedTokens);
		
		
		// more tokens - space separator
		tokenizer = new IntegerTokenizer("1 2 3");
		parsedTokens = new ArrayList<>(); 
		token = null;
		while ((token = tokenizer.getNext()) != null) {
			parsedTokens.add(token);
		}
		assertEquals("More tokens delimited by space",
				Arrays.asList("1","2","3"),
				parsedTokens);
		
		
		// more tokens - comma separator
		tokenizer = new IntegerTokenizer("1,2,3");
		parsedTokens = new ArrayList<>(); 
		token = null;
		while ((token = tokenizer.getNext()) != null) {
			parsedTokens.add(token);
		}
		assertEquals("More tokens delimited by comma",
				Arrays.asList("1","2","3"),
				parsedTokens);
		
		
		// tokenizer can handle positive/negative signs
		tokenizer = new IntegerTokenizer("-1,+2,3");
		parsedTokens = new ArrayList<>();
		token = null;
		while ((token = tokenizer.getNext()) != null) {
			parsedTokens.add(token);
		}
		assertEquals("Tokens can have signs", 
				Arrays.asList("-1", "+2", "3"), 
				parsedTokens);
		
		
		// tokenizer can handle bigger numbers
		tokenizer = new IntegerTokenizer("85454444488995522114558745 -8984552211111111");
		parsedTokens = new ArrayList<>();
		token = null;
		while ((token = tokenizer.getNext()) != null) {
			parsedTokens.add(token);
		}
		assertEquals("Tokens with big numbers", 
				Arrays.asList("85454444488995522114558745", "-8984552211111111"), 
				parsedTokens);
		
		
		// delimiter same as sign
		tokenizer = new IntegerTokenizer("+10+-20++30+40");
		parsedTokens = new ArrayList<>();
		token = null;
		while ((token = tokenizer.getNext()) != null) {
			parsedTokens.add(token);
		}
		assertEquals("Tokens with big numbers", 
				Arrays.asList("+10", "-20", "+30", "40"), 
			parsedTokens);
	}
	
	@Test
	public void testNegativeCases() {
		// single not numeric characted
		IntegerTokenizer tokenizer = new IntegerTokenizer("A");
		List<String> parsedTokens = new ArrayList<>();
		String token = null;
		try {
			while ((token = tokenizer.getNext()) != null) {
				parsedTokens.add(token);
			}
			fail("Expected CalculatorException!");
		} catch (CalculatorException e) {}
		assertTrue("No tokes processed", parsedTokens.isEmpty());
		
		
		// not consistent delimiters
		tokenizer = new IntegerTokenizer("1 2,3");
		parsedTokens = new ArrayList<>();
		token = null;
		try {
			while ((token = tokenizer.getNext()) != null) {
				parsedTokens.add(token);
			}
			fail("Expected CalculatorException!");
		} catch (CalculatorException e) {
			assertEquals("Used invalid delimiter ',', expected ' '!", e.getMessage());
		}
		
		assertEquals("Tokens processed before error", 
				Arrays.asList("1"), 
				parsedTokens);
		
		
		// as delimiter is considered exactly one non digit characted
		tokenizer = new IntegerTokenizer("1  2");
		parsedTokens = new ArrayList<>();
		token = null;
		try {
			while ((token = tokenizer.getNext()) != null) {
				parsedTokens.add(token);
			}
			fail("Expected CalculatorException!");
		} catch (CalculatorException e) {}

		assertEquals("Tokens processed before error", 
				Arrays.asList("1"), 
				parsedTokens);
		
		
		//input ends with delimiter
		tokenizer = new IntegerTokenizer("1,2,");
		parsedTokens = new ArrayList<>();
		token = null;
		try {
			while ((token = tokenizer.getNext()) != null) {
				parsedTokens.add(token);
			}
			fail("Expected CalculatorException!");
		} catch (CalculatorException e) {}

		assertEquals("Tokens processed before error", 
				Arrays.asList("1","2"), 
				parsedTokens);
		
		//non digit element
		tokenizer = new IntegerTokenizer("1,2,A,4");
		parsedTokens = new ArrayList<>();
		token = null;
		try {
			while ((token = tokenizer.getNext()) != null) {
				parsedTokens.add(token);
			}
			fail("Expected CalculatorException!");
		} catch (CalculatorException e) {
		}

		assertEquals("Tokens processed before error", Arrays.asList("1", "2"), parsedTokens);
	}
}
