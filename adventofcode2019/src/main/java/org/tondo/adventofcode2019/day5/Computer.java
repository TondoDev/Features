package org.tondo.adventofcode2019.day5;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;


public class Computer {
	
	private static final List<Integer> NO_TARGET_INSTR = Collections.unmodifiableList(Arrays.asList(4,5,6));

	private List<Integer> program;
	private List<Integer> rom;
	private LinkedList<String> inputBuffer = new LinkedList<>();
	private List<String> outputBuffer;
	
	public List<Integer> loadProgram(BufferedReader reader) throws IOException {
		List<Integer> retval = new ArrayList<Integer>();

		String line = null;
		while ((line = reader.readLine()) != null) {
			Stream.of(line.split(",")).map(Integer::valueOf).forEachOrdered(retval::add);
		}

		this.program = new ArrayList<>(retval);
		this.rom = new ArrayList<>(retval);
		return retval;
	}
	
	public void loadProgram(List<Integer> program) {
		this.program = program;
		this.rom = new ArrayList<Integer>(program);
	}
	
	public List<Integer> getProgram() {
		return this.program;
	}
	
	public List<Integer> getRom() {
		return this.rom;
	}
	
	public void setInputBuffer(String... lines) {
		this.inputBuffer = new LinkedList<>();
		if (lines != null && lines.length > 0) {
			for (String l : lines) {
				this.inputBuffer.add(l);
			}
		}
	}
	
	public static class Instruction {
		public int code;
		public int[] modes;
	}
	
	public Instruction decode(int instrCode) {
		String code = new StringBuilder().append(instrCode).reverse().toString();
		char c = code.charAt(0);
		
		int len = code.length();
		int paramCount = 0;
		int decoded = 0;
		
		if (c == '9' && len == 2 && code.charAt(1) == '9') {
			// halt
			decoded = 99;
		} else if (c == '1' && (len == 1 || code.charAt(1) == '0')) {
			// addition
			paramCount = 3;
			decoded = 1;
		} else if (c == '2' && (len == 1 || code.charAt(1) == '0')) {
			// multiplication
			paramCount = 3;
			decoded = 2;
		} else if (c == '3' && (len == 1 || code.charAt(1) == '0')) {
			// read
			paramCount = 1;
			decoded = 3;
		} else if (c == '4' && (len == 1 || code.charAt(1) == '0')) {
			// print
			paramCount = 1;
			decoded = 4;
		} else if (c == '5' && (len == 1 || code.charAt(1) == '0')) {
			// jump if true
			paramCount = 2;
			decoded = 5;
		} else if (c == '6' && (len == 1 || code.charAt(1) == '0')) {
			// jump if false
			paramCount = 2;
			decoded = 6;
		} else if (c == '7' && (len == 1 || code.charAt(1) == '0')) {
			paramCount = 3;
			decoded = 7;
		} else if (c == '8' && (len == 1 || code.charAt(1) == '0')) {
			paramCount = 3;
			decoded = 8;
		} else {
			throw new IllegalStateException("Inknown instruction code: " + instrCode);
		}
		
		int[] modes = new int[paramCount];
		for (int i = 2; i < code.length(); i++) {
			modes[i-2] = code.charAt(i) - 48; // convert digit in char to integral value of this digit
		}
		// last argument is usually the target which could be referenced only by address
		if (!NO_TARGET_INSTR.contains(decoded) && modes.length > 0 && modes[modes.length - 1] ==  1 ) {
		//if ((decoded == 3 && modes[0] == 1) || ((decoded == 1 || decoded == 2) && modes[2] == 1)) {
			throw new IllegalStateException("Instruction " + instrCode + " uses target mode as immediate, which is not allowed!");
		}
		
		Instruction instr = new Instruction();
		instr.code = decoded;
		instr.modes = modes;
		
		return instr;
	}
	
	public void programReset() {
		this.program = this.rom == null ? new ArrayList<>() : new ArrayList<>(this.rom);
	}
	
	public void compute() {
		int pc = 0;
		this.outputBuffer = new ArrayList<>();
		
		while(true) {
			int jump = -1; // set by jum instructions
			Instruction instr = decode(get(pc, this.program));
			if (99 == instr.code) {
				break;
			}
			
			if (instr.code == 1 || instr.code == 2) {
				int op1 = resolveOperand(pc + 1, instr.modes[0], this.program);
				int op2 = resolveOperand(pc + 2, instr.modes[1], this.program);
				int target = get(pc + 3, this.program);
				
				if (instr.code == 1) {
					this.program.set(target, op1+op2);
				} else if (instr.code == 2) {
					this.program.set(target, op1*op2);
				}
			} else if (instr.code == 3) {
				// read
				int value = readFromInput();
				int target = get(pc + 1, this.program);
				this.program.set(target, value);
			} else if (instr.code == 4) {
				int op1 = resolveOperand(pc + 1 , instr.modes[0],  this.program);
				this.writOutput(op1);
			} else if (instr.code == 5 || instr.code == 6) {
				// jump instructions
				int op = resolveOperand(pc + 1, instr.modes[0], this.program);
				int target = resolveOperand(pc + 2, instr.modes[1], this.program);
				if ((instr.code == 5 && op != 0) || 
						(instr.code == 6 && op == 0)) {
					jump = target;
				}
			} else if (instr.code == 7 || instr.code == 8) {
				int op1 = resolveOperand(pc + 1, instr.modes[0], this.program);
				int op2 = resolveOperand(pc + 2, instr.modes[1], this.program);
				int target = get(pc + 3, this.program);
				int value = 0;
				if ((instr.code == 7 && op1 < op2) ||
						(instr.code == 8 && op1 == op2)) {
					value = 1;
				}
				
				this.program.set(target, value);
			}
			
			
			if (jump < 0) {
				pc += (1 + instr.modes.length);
			} else {
				pc = jump;
			}
		}
	}
	
	
	private int resolveOperand(int pc, int mode, List<Integer> prog) {
		int op = get(pc, prog);
		if (mode == 0) {
			op = get(op, prog);
		}
		
		return op;
	}
	
	
	public void writOutput(int value) {
		if (this.outputBuffer == null) {
			this.outputBuffer = new ArrayList<>();
		}
		
		this.outputBuffer.add(String.valueOf(value));
		//System.out.println("== " + value);
	}
	
	public List<String> getOutputBuffer() {
		return this.outputBuffer;
	}
	
	public int readFromInput() {
		
		String line = this.inputBuffer.pollFirst();
		if (line == null) {
			throw new IllegalStateException("Input buffer is empty");
		}
		
		return Integer.parseInt(line);
	}
	
	private Integer get(int addr, List<Integer> prog) {
		if (addr < 0 || addr >= prog.size()) {
			throw new IllegalStateException("SIGSEGV: Index = " + addr);
		}
		return prog.get(addr);
	}
}
