package org.tondo.Java7Features.exceptions;

public class StringBasedException extends GrandPaException{
	
	private static final long serialVersionUID = 1L;
	private String name;
	
	public StringBasedException(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
