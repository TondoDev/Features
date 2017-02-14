package org.tondo.advent2016.day12;

public class JnzInstruction extends Instruction {
	public boolean jump;

	@Override
	public void execute() {
		this.jump = getParamA().getValue() != 0;
	}
	
	@Override
	public int moveProgram() {
		return this.jump ? getParamB().getValue() : 1;
	}
}
