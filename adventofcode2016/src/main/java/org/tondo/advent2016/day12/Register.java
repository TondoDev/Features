package org.tondo.advent2016.day12;

public class Register {

	private String name;
	private int value;
	
	public Register(String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Register name can't be null or empty!");
		}
		this.name = name;
		this.value = 0;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
}
