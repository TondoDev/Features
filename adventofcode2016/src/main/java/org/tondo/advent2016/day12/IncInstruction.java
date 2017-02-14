package org.tondo.advent2016.day12;

public class IncInstruction extends Instruction {
	
	public IncInstruction(Register reg) {
		this.setParamA(reg);
	}

	@Override
	public void execute() {
		int val = getParamA().getValue() + 1;
		getParamA().setValue(val);
	}
	
	@Override
	public String getName() {
		return "inc";
	}
}
