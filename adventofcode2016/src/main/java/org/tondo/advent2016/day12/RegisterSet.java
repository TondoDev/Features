package org.tondo.advent2016.day12;

import java.util.HashMap;
import java.util.Map;

public class RegisterSet {

	private Map<String, Register> registers;
	
	public RegisterSet() {
		this.registers = new HashMap<>();
		this.registers.put("a", new Register("a"));
		this.registers.put("b", new Register("b"));
		this.registers.put("c", new Register("c"));
		this.registers.put("d", new Register("d"));
		
	}
	
	public Register getRegister(String regName) {
		Register reg = this.registers.get(regName);
		if (reg == null) {
			throw new IllegalStateException("Register with name '" + regName + "' doesn't exists!");
		}
		
		return reg;
	}
}
