package org.tondo.advent2016.day2;

public abstract class Decryptor {

	private static final String DIRECTIONS = "UDLR";
	private StringBuilder password;
	
	public Decryptor() {
		this.password = new StringBuilder();
	}
	
	
	public abstract void decryptLine(String line);
	
	protected void validateDirection(Character c) {
		if (DIRECTIONS.indexOf(c) < 0 ) {
			throw new IllegalArgumentException("Character '" + c + "' is not valid direction!");
		}
	}
	
	protected void storeChar(char c) {
		this.password.append(c);
	}
	
	public String getPassword() {
		return this.password.toString();
	}
}
