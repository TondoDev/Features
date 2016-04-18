package org.tondo.calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokenizer used for extracting numbers (positive or negative) from input string.
 * Instances of this class are stateful and NOT thread safe.
 * 
 * 
 * @author TondoDev
 *
 */
public class IntegerTokenizer {
	
	private static final Pattern NUMBER_PARSER = Pattern.compile("([+|-]?\\d+)(\\D?)");

	private final String numbers;
	private Matcher tokenizer;
	private int lastEnd;
	private String firstUseddelimiter;
	private String currentdelimiter;
	private int inputSize;
	private boolean inError;
	
	public IntegerTokenizer(String stringWithNumbers) {
		this.numbers = stringWithNumbers;
		this.inError = false;
		// can be used for validate input on very beginning
		//Pattern.compile("([+|-]?\\d+(\\D))?([+|-]?\\d+\\2)*([+|-]?\\d+)$");
	}
	
	/**
	 * @return next valid token which represents integral number or null if
	 * no other elements are present 
	 * 
	 * @throws CalculatorException if found token which doesn't match required syntex
	 */
	public String getNext() {
		// consider null and empty string as valid input which produce zero tokens
		if (this.numbers == null || this.numbers.isEmpty()) {
			return null;
		}
		if (this.tokenizer == null) {
			this.inputSize = this.numbers.length();
			this.tokenizer = NUMBER_PARSER.matcher(this.numbers);
		}
		
		if (this.tokenizer.find()) {
			int start = this.tokenizer.start();
			if (start != this.lastEnd) {
				this.inError = true;
				throw new CalculatorException("Input not valid!");
			} else {
				lastEnd = this.tokenizer.end();
			}
			String number = this.tokenizer.group(1);
			currentdelimiter = this.tokenizer.group(2);
			
			if (this.firstUseddelimiter == null) {
				this.firstUseddelimiter = this.currentdelimiter;
			// currentdelimiter is empty when last number without trailing delimiter is processed
			} else if (!this.currentdelimiter.isEmpty() && !firstUseddelimiter.equals(currentdelimiter)) {
				this.inError = true;
				throw new CalculatorException("Used invalid delimiter '" + this.currentdelimiter + "', expected '" + this.firstUseddelimiter +"'!");
			}
			return number;
		} else {
			// whole string must be processed and must not end with delimiter
			if (this.lastEnd != this.inputSize || !this.currentdelimiter.isEmpty()) {
				this.inError = true;
				throw new CalculatorException("Input not valid!");
			}
			return null;
		}
	}
	
	/**
	 * Predicate if tokenizer is in error state
	 */
	public boolean isInError() {
		return inError;
	}
}
