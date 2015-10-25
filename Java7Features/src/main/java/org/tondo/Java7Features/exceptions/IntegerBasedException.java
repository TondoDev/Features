package org.tondo.Java7Features.exceptions;

public class IntegerBasedException extends GrandPaException{

	private static final long serialVersionUID = 1L;
	private int integerNumber;
	
	public IntegerBasedException(int number) {
		this.integerNumber = number;
	}
	
	public int getIntegerNumber() {
		return integerNumber;
	}
	
	@Override
	public String toString() {
		return Integer.toString(integerNumber);
	}
}
