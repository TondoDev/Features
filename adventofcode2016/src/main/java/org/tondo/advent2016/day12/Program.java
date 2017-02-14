package org.tondo.advent2016.day12;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Program {
	
	private RegisterSet registers;
	private Instruction[] codeSegment;
	private int programCounter;
	
	
	public Program() {
		this.registers = new RegisterSet();
	}
	
	public void load(BufferedReader reader) throws IOException {
		
		String line = null;
		InstructionDecoder decoder = new InstructionDecoder(this.registers);
		List<Instruction> loadedInstructions = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			Instruction instr = decoder.decode(line);
			loadedInstructions.add(instr);
		}
		
		this.programCounter = 0;
		this.codeSegment = new Instruction[loadedInstructions.size()];
		this.codeSegment = loadedInstructions.toArray(this.codeSegment);
	}
	
	public void execute() {
		while (this.programCounter < this.codeSegment.length) {
			Instruction executed = this.codeSegment[this.programCounter];
			executed.execute();
			this.programCounter += executed.moveProgram();
		}
	}
	
	public RegisterSet getRegisterSet() {
		return this.registers;
	}
}
