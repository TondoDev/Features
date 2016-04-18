package org.tondo.calculator;

/**
 * Calculates sum of numbers provided in input delimited by exactly one
 * non-digit character. 
 * 
 * Allowed are numbers are only non-negative numbers integral numbers.
 * Negative numbers are treated as error!
 * Numbers above 100, are ignored, but not considered as bad input.
 *  
 * @author TondoDev
 *
 */
public class StringCalculator {

	/**
	 * Execute sum operation above numbers in input string
	 * @param input
	 * 	contains number to sum, can be null or empty string
	 * @return
	 * 	sum of all valid numbers in input string. For null or empty string returns 0, never null
	 * @throws CalculatorException \
	 * 	if input is in bad format, or negative number is foud
	 */
	public Long calculeteSum(String input) {
		// quick exit
		if (input == null || input.isEmpty()) {
			return 0L;
		}
		
		IntegerTokenizer tokenizer = new IntegerTokenizer(input);
		Long totalSum = 0L;
		String token = null;
		while ((token = tokenizer.getNext()) != null) {
			totalSum = totalSum + this.parseNumber(token);
		}
		
		return totalSum;
	}
	
	private Long parseNumber(String number) {
		if (number.startsWith("-")) {
			throw new CalculatorException("Negative numbers are not allowed!");
		}
		
		// valid only for maximum three digit numbers (fourth character is for possible + sign)
		if (number.length() <= 4) {
			Long value = Long.parseLong(number);
			if (value <= 100L) {
				return value;
			}
		}
		
		return 0L;
	}
}
