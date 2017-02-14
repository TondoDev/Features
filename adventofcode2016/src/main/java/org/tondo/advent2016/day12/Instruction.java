package org.tondo.advent2016.day12;

public abstract class Instruction {
	
	private static class ConstantRegister extends Register {
		
		private int value;

		public ConstantRegister(String name, int value) {
			super(name);
			this.value = value;
		}
		
		@Override
		public void setValue(int value) {
			throw new UnsupportedOperationException("Constant register! Value can't be changed!");
		}
		
		@Override
		public int getValue() {
			return this.value;
		}
		
	}

	private Register paramA;
	private Register paramB;
	
	
	public abstract void execute();
	
	public void setParamA(int value) {
		this.paramA = new ConstantRegister("constA", value);
	}
	
	public void setParamA(Register reg) {
		this.paramA = reg;
	}
	
	public void setParamB(int value) {
		this.paramB = new ConstantRegister("constB", value);
	}
	
	public void setParamB(Register reg) {
		this.paramB = reg;
	}
	
	protected Register getParamA() {
		return paramA;
	}
	
	protected Register getParamB() {
		return paramB;
	}
	
	public int moveProgram() {
		return 1;
	}
}
