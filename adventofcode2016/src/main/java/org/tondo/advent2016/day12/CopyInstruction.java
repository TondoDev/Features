package org.tondo.advent2016.day12;

public class CopyInstruction extends Instruction {
	@Override
	public void execute() {
		getParamB().setValue(getParamA().getValue());
	}
	
	@Override
	public String getName() {
		return "cpy";
	}
}

