package org.tondo.Java7Features;

import static org.junit.Assert.assertArrayEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.tondo.testutils.ConsoleOutputCapturer;

public class SafeVarargsTest {
	
	@SafeVarargs
	// Not actually safe!
	static void merge(List<String>... stringLists) {
		Object[] array = stringLists;
		List<Integer> tmpList = Arrays.asList(42);
		array[0] = tmpList; // Semantically invalid, but compileswithout warnings
		String element = stringLists[0].get(0); // runtime ClassCastException
		System.out.println(element);
	}
	
	// probably treatt as Object...
	@SafeVarargs
	static <T> void displayValues(T... args) {
		int i = 1;
		for (T a : args) {
			System.out.println(i + ". Value is: " + a);
			i++;
		}
		
	}
	/**
	 * Heap polution is when variable of parametrized type is assigned a different
	 * type than that used to define it
	 */
	@Test(expected=ClassCastException.class)
	public void testHeapPollution() {
		List<String> list1 = new ArrayList<>();
		list1.add("One");
		list1.add("Two");
		list1.add("Three");
		List<String> list2 = new ArrayList<>();
		list2.add("Four");
		list2.add("Five");
		list2.add("Six");
		merge(list1,list2);
	}
	
	@Test
	public void testVargargsGenerics() {
		ConsoleOutputCapturer capturer = ConsoleOutputCapturer.getInstance();
		capturer.capture();
		displayValues(1, 2L, 3.0, "Ahoj", BigDecimal.valueOf(8));
		capturer.stopCapturing();
		String[] lines = capturer.getLines();
		assertArrayEquals(new String[] {
				"1. Value is: 1",  
				"2. Value is: 2", 
				"3. Value is: 3.0", 
				"4. Value is: Ahoj", 
				"5. Value is: 8"}, lines);
	}
	
	

}
