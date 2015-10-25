package org.tondo.Java7Features.math;

import org.junit.Test;

public class FloatingPointTest {
	
	@Test
	public void testFloatingPointRepresentation() {
		explainFloat(0.085f);
		System.out.println("--");
		explainFloat(-0.085f);
		System.out.println("--");
		explainFloat(100f);
		System.out.println("--");
		explainFloat(2.45f);
		System.out.println("--");
		explainFloat(0.1f);
		System.out.println("--");
		explainFloat(Float.NaN);
		System.out.println("--");
		explainFloat(Float.NEGATIVE_INFINITY);
		System.out.println("--");
		explainFloat(Float.POSITIVE_INFINITY);
		
		System.out.println(0.1 < 0.1000000010);
	}
	
	
	private void explainFloat(float number) {
		String binaryEffective = Long.toBinaryString(Float.floatToIntBits(number));
		String floatMask = "00000000000000000000000000000000";
		String fullBinary = (floatMask + binaryEffective).substring(binaryEffective.length());
		
		System.out.println(String.format("Formated %.15f", number));
		System.out.println("Decimal: " + number);
		System.out.println("Binary: " + fullBinary);
		
		int signValue = '0' == fullBinary.charAt(0) ? 1 : -1;
		System.out.println("Sign: "  + fullBinary.charAt(0) + ", Value: " + signValue);
		
		int mantisaDivisor = 8388608;
		
		String exponentBinary = fullBinary.substring(1, 9);
		int exponentValue = Integer.parseInt(exponentBinary, 2);
		int exponentBiased = exponentValue - 127;
		System.out.println("Exponent: " + exponentBinary + ", Value: " + exponentValue + ", Biased (E - 127): " + exponentBiased);
		
		String mantisaBinary = fullBinary.substring(9);
		int mantisaValue = Integer.parseInt(mantisaBinary, 2);
		System.out.println("Mantisa: " + mantisaBinary + ", Value: " + mantisaValue);
		System.out.println("-1^(sign) * 2^(E-127) * (1+m/2^23)");
		System.out.println(signValue + " * " + "2^(" + exponentBiased + ") * (1+" + mantisaValue + "/" + mantisaDivisor + ")");
		
		
		float mantisaEvaluated = 1 + ((float)mantisaValue/mantisaDivisor);
		System.out.println(signValue + " * " + Math.pow(2, exponentBiased) + " * " + mantisaEvaluated);
		System.out.println("Result: " +  (signValue* Math.pow(2, exponentBiased) * mantisaEvaluated));
	}

}
