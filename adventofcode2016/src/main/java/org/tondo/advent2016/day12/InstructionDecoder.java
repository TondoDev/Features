package org.tondo.advent2016.day12;

import java.util.regex.Matcher;
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
		Matcher cpy = CPY_CODE.matcher(instructionCode);
		if (cpy.matches()) {
			Instruction i = new CopyInstruction();
			setInstructionParams(i, cpy);
			return i;
		}
		
		Matcher inc = INC_CODE.matcher(instructionCode);
		if (inc.matches()) {
			return new IncInstruction(registers.getRegister(inc.group(1)));
		}
		
		Matcher dec = DEC_CODE.matcher(instructionCode);
		if (dec.matches()) {
			return new DecInstruction(registers.getRegister(dec.group(1)));
		}
		
		Matcher jnz = JNZ_CODE.matcher(instructionCode);
		if (jnz.matches()) {
			Instruction i = new JnzInstruction();
			setInstructionParams(i, jnz);
			return i;
		}
		
		return null;
	}
	
	private boolean isRegister(String token) {
		return "abcd".contains(token);
	}
	
	private void setInstructionParams (Instruction instr, Matcher tokens) {
		String tokenA = tokens.group(1);
		if (isRegister(tokenA)) {
			instr.setParamA(this.registers.getRegister(tokenA));
		} else {
			instr.setParamA(Integer.parseInt(tokenA));
		}
		
		String tokenB = tokens.group(2);
		if (isRegister(tokenB)) {
			instr.setParamB(this.registers.getRegister(tokenB));
		} else {
			instr.setParamB(Integer.parseInt(tokenB));
		}
		
	}
}
