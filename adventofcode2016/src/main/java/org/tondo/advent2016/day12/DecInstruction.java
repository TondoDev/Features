package org.tondo.advent2016.day12;

public class DecInstruction extends Instruction{

	public DecInstruction(Register reg) {
		setParamA(reg);
	}
	
	@Override
	public void execute() {
		int val = getParamA().getValue() - 1;
		getParamA().setValue(val);
	}
	
	@Override
	public String getName() {
		return "dec";
	}
}
