package org.tondo.advent2016.day12;

import java.util.regex.Pattern;

public class InstructionDecoder {
	private RegisterSet registers;
	
	private static final Pattern CPY_CODE = Pattern.compile("cpy (-?\\d+|(?:a|b|c|d)) (-?\\d+|(?:a|b|c|d))");
	private static final Pattern INC_CODE = Pattern.compile("inc (a|b|c|d)");
	private static final Pattern DEC_CODE = Pattern.compile("dec (a|b|c|d)");
	private static final Pattern JNZ_CODE = Pattern.compile("jnz (-?\\d+|(?:a|b|c|d)) (-?\\d+|(?:a|b|c|d))");
	
	
	public InstructionDecoder(RegisterSet registers) {
		this.registers = registers;
	}
	
	public Instruction decode(String instructionCode) {
		
		
		return null;
	}
}
