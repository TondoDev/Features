package org.tondo.adventofcode2019.days;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;
import org.tondo.adventofcode2019.DayTaskBase;

public class TaskDay4  extends DayTaskBase{

	public TaskDay4() {
		super(4);
	}
	
	@Test
	public void testPartOneSolution() throws Exception {
		assertEquals("1653", getPartOneSolution());
	}
	
	
	@Test
	public void testPartOneSolutionBF() throws Exception {
		assertEquals("1653", getPartOneSolutionBF());
	}
	
	@Test
	public void testPartTwoSolution() throws Exception {
		System.out.println(getPartTwoSolution());
		//assertEquals("", getPartTwoSolution());
	}
	
	
	@Override
	public BufferedReader getPartOneInput() {
		return createBufferedReader("206938-679128");
	}
	
	
	@Override
	public String getPartOneSolution() throws Exception {
		String line;
		try (BufferedReader reader = getPartOneInput()) {
			line = reader.readLine();
		}
		
		String[] parts = line.split("-");
		int minNum = Integer.parseInt(parts[0]);
		int maxNum = Integer.parseInt(parts[1]);
		
		return "" + algorithmicWay(minNum, maxNum, this::hasDoubbled);
	}
	
	public String getPartOneSolutionBF() throws Exception {
		String line;
		try (BufferedReader reader = getPartOneInput()) {
			line = reader.readLine();
		}
		
		String[] parts = line.split("-");
		int minNum = Integer.parseInt(parts[0]);
		int maxNum = Integer.parseInt(parts[1]);
		
		return "" + brutalForce(minNum, maxNum);
	}
	
	
	@Override
	public String getPartTwoSolution() throws Exception {
		String line;
		// same input
		try (BufferedReader reader = getPartOneInput()) {
			line = reader.readLine();
		}
		
		String[] parts = line.split("-");
		int minNum = Integer.parseInt(parts[0]);
		int maxNum = Integer.parseInt(parts[1]);
		
//		return "" + algorithmicWay(minNum, maxNum, chArr -> {
//			return getRepeatingGroups(chArr).contains(2);
//		});
		
		return "" + brutalForcePt2(minNum, maxNum);
	}
	
	
	public int algorithmicWay(int minNum, int maxNum, Predicate<char[]> checker) {
		int count = 0;
		int currNum = getFirstValidNumber(minNum, checker);
		while (currNum <= maxNum) {
			count++;
			System.out.println("" + count + ": " + currNum);
			currNum = getNextNumber(currNum, checker);
		}
		
		return count;
	}
	
	
	public int brutalForce (int from, int to) {
		
		int count = 0;
		for (int num = from; num <= to; num++) {
			if (isValid(num)) {
				count++;
				//System.out.println("" + count + ": " + num);
			}
		}
		
		return count;
	}
	
	private boolean isValid(int num) {
		char[] digits = String.valueOf(num).toCharArray();
		char prev = 0;
		
		boolean twin = false;
		
		for (int i = 0; i < digits.length; i++) {
			if ((int)digits[i] < (int)prev) {
				return false;
			} else if (!twin && digits[i] == prev) {
				twin = true;
			}
			
			prev = digits[i];
		}
		
		return twin;
	}
	
	public int brutalForcePt2 (int from, int to) {
		
		int count = 0;
		for (int num = from; num <= to; num++) {
			if (isValidPt2(num)) {
				count++;
				//System.out.println("" + count + ": " + num);
			}
		}
		
		return count;
	}
	
	
	private boolean isValidPt2(int num) {
		char[] digits = String.valueOf(num).toCharArray();
		char prev = 0;
		
		
		for (int i = 0; i < digits.length; i++) {
			if ((int)digits[i] < (int)prev) {
				return false;
			} 
			
			prev = digits[i];
		}
		
		return getRepeatingGroups(digits).contains(2);
	}
	
	public int getFirstValidNumber(int num, Predicate<char[]> succCheck) {
		char[] digits = String.valueOf(num).toCharArray();
		
		char prev = 0;
		boolean replaced = false;
		
		for (int i = 0; i < digits.length; i++) {
			if (replaced || (int)digits[i] < (int)prev) {
				digits[i] = prev;
				replaced = true;
			} else {
				prev = digits[i];
			}
			
		}
		
		while(!succCheck.test(digits)) {
			digits = getNextNonDecreasing(digits);
		}
		
		return Integer.parseInt(String.valueOf(digits));
	}
	
	public char[] getNextNonDecreasing(char[] digits) {
		
		int i =  digits.length - 1;
		while(digits[i] == '9') {
			i--;
		}
		
		char newChar = (char) (digits[i] + 1);
		
		for (int x = i; x < digits.length; x++) {
			digits[x] = newChar;
		}
		
		return digits;
	}
	
	
	public int getNextNumber(int num, Predicate<char[]> succChec) {
		char[] digits = String.valueOf(num).toCharArray();
		
		boolean hasTwin = false;
		while (!hasTwin) {
			digits = getNextNonDecreasing(digits);
			hasTwin = succChec.test(digits);
		}
		
		
		return  Integer.parseInt(String.valueOf(digits));
	}
	
	
	private List<Integer> getRepeatingGroups(char[] digits) {
		List<Integer> retVal = new ArrayList<>();
		char prev = 0;
		int cnt = 1;
		int max = 0;
		for (int i = 0; i < digits.length; i++) {
			if ((int)digits[i] ==  (int)prev) {
				cnt++;
			} else {
				if (cnt > 1) {
					retVal.add(cnt);
				}
				cnt = 1;
			}
			prev = digits[i];
		}
		
		if (cnt > 1) {
			retVal.add(cnt);
		}
		return retVal;
	}

	private boolean hasDoubbled(char[] digits) {
		return !getRepeatingGroups(digits).isEmpty();
	}
	
	@Test
	public void testIsValid() {
		
		assertTrue(isValid(111111));
		assertTrue(!isValid(223450));
		assertTrue(!isValid(123789));
		assertTrue(isValid(122345));
		assertTrue(isValid(111123));
		assertTrue(!isValid(135679));
	}
	
	
	@Test
	public void testMaxSuccSeq() {
		assertEquals(Arrays.asList(6), getRepeatingGroups(new char[] {'a','a','a','a','a','a'}));
		assertEquals(new ArrayList<>(), getRepeatingGroups(new char[] {'a','b','c','d','e','f'}));
		assertEquals(Arrays.asList(2), getRepeatingGroups(new char[] {'a','b','b','d','e','x'}));
		assertEquals(Arrays.asList(2, 2), getRepeatingGroups(new char[] {'a','b','b','d','e','e'}));
		assertEquals(Arrays.asList(2, 2, 2), getRepeatingGroups(new char[] {'a','a','b','b','e','e'}));
		assertEquals(Arrays.asList(2, 3), getRepeatingGroups(new char[] {'a','b','b','e','e','e'}));
	}
}
