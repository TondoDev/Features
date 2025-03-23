package org.tondo.adventofcode2023.days;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.tondo.adventofcode2023.DayTaskBase;

public class TaskDay1 extends DayTaskBase {
	
	private static final Map<String, Integer> NUMBERS_MAP;
	static {
		Map<String, Integer> tmp = new HashMap<>();
		tmp.put("one", 1);
		tmp.put("two", 2);
		tmp.put("three", 3);
		tmp.put("four", 4);
		tmp.put("five", 5);
		tmp.put("six", 6);
		tmp.put("seven", 7);
		tmp.put("eight", 8);
		tmp.put("nine", 9);
		
		NUMBERS_MAP = Collections.unmodifiableMap(tmp);
		
	}

	public TaskDay1() {
		super(1);
	}

	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("53651", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("53894", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws IOException {
		try(BufferedReader r = getPartOneInput()) {
			return  "" + getSumOfCalubrationValues(r, false /* only digits */);
		}
	}
	
	@Override
	public String getPartTwoSolution() throws IOException {
		try(BufferedReader r = getPartOneInput()) {
			return  "" + getSumOfCalubrationValues(r, true /* textual numbers */);
		}
	}
	

	private long getSumOfCalubrationValues(BufferedReader reader, boolean useTextualSearch) throws IOException {
		String line = null;
		long sum = 0L;
		while((line = reader.readLine()) != null) {
			int[] firstAndLastNumbers =  getFirstAndLastNumbers(line, useTextualSearch);
			int calValue = getCalibrationValue(firstAndLastNumbers);
			sum+=calValue;
			
			//System.out.println(firstAndLastNumbers[0] + " " + firstAndLastNumbers[1] + " " + line);
		}
		
		return sum;
	}
	
	private int[] getFirstAndLastNumbers(String str, boolean textSearch) {
		//Character.isDigit()
		final char ZERO = '0';
		int[] retVal = new int[2];
		int[] indices = new int[] {-1,-1};
		boolean first = true;
		int length = str.length();
		for (int i = 0; i < length; i++) {
			char ch = str.charAt(i);
			if (Character.isDigit(ch)) {
				int digitValue = ch - ZERO;
				if (first) {
					retVal[0] = digitValue;
					retVal[1] = digitValue;
					indices[0] = i;
					indices[1] = i;
					first = false;
				} else {
					retVal[1] = digitValue;
					indices[1] = i;
				}
			}
		}
		
		if (textSearch){
			for (Map.Entry<String, Integer> number : NUMBERS_MAP.entrySet()) {
				int numIdx = -1; // ;
				// we must investigate every occurrence of that text
				while ((numIdx = str.indexOf(number.getKey(), numIdx + 1)) >=0) {
					if (indices[0] == -1 || numIdx < indices[0]) {
						indices[0] = numIdx;
						retVal[0] = number.getValue();
						// first found digit must be copied also to the last
						if (indices[1] == -1 ) {
							indices[1] = numIdx;
							retVal[1] = number.getValue();
						}
					} else if (numIdx > indices[1]) {
						indices[1] = numIdx;
						retVal[1] = number.getValue();
					}
				}
			}
		}
		
		// no digits found in string
		if (indices[0] == -1) {
			System.out.println("NULL: " + str);
			return null;
		}
		
		return retVal;
	}
	
	
	private int getCalibrationValue(int[] numbers) {
		// in case no numbers, returns neutral element
		if (numbers == null) {
			return 0;
		}
		return Integer.parseInt("" + numbers[0] + numbers[1]);
	}
	
	private final String[] SAMPLE_INPUT_1 =  {
			"1abc2",
			"pqr3stu8vwx",
			"a1b2c3d4e5f",
			"treb7uchet"
	};
	
	@Test
	public void testSampleIputP1() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_1);
		
		assertEquals(142L, getSumOfCalubrationValues(reader, false));
	}
	
	private final String[] SAMPLE_INPUT_2 =  {
			"two1nine",
			"eightwothree",
			"abcone2threexyz",
			"xtwone3four",
			"4nineeightseven2",
			"zoneight234",
			"7pqrstsixteen"	
	};
	
	@Test
	public void testSampleIputP2() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_2);
		
		assertEquals(281L, getSumOfCalubrationValues(reader, true));
	}
	
	@Test
	public void testIntChar() {
		char nula = '0';
		char devat = '9';
		
		assertEquals(48, (int)nula);
		assertEquals(9, devat - nula);
		
		int[] fAl = getFirstAndLastNumbers("1abc2", false);
		assertArrayEquals(new int[] {1,2}, fAl);
		assertEquals(12, getCalibrationValue(fAl));
		
		fAl = getFirstAndLastNumbers("pqr3stu8vwx", false);
		assertArrayEquals(new int[] {3,8}, fAl);
		assertEquals(38, getCalibrationValue(fAl));
		
		fAl = getFirstAndLastNumbers("a1b2c3d4e5f", false);
		assertArrayEquals(new int[] {1,5}, fAl);
		assertEquals(15, getCalibrationValue(fAl));
		
		fAl = getFirstAndLastNumbers("treb7uchet", false);
		assertArrayEquals(new int[] {7,7}, fAl);
		assertEquals(77, getCalibrationValue(fAl));
		
		fAl = getFirstAndLastNumbers("two1nine", true);
		assertArrayEquals(new int[] {2,9}, fAl);
		
		fAl = getFirstAndLastNumbers("4nineeightseven2", true);
		assertArrayEquals(new int[] {4,2}, fAl);
		
		fAl = getFirstAndLastNumbers("six", true);
		assertArrayEquals(new int[] {6,6}, fAl);
		
		fAl = getFirstAndLastNumbers("fiveight", true);
		assertArrayEquals(new int[] {5,8}, fAl);
		
		fAl = getFirstAndLastNumbers("fiveeight", true);
		assertArrayEquals(new int[] {5,8}, fAl);
	}
	
	@Test
	public void testErrorLine() {
		//eight8zlctbmsixhrvbpjb84nnmlcqkzrsix
		int[] fAl = getFirstAndLastNumbers("eight8zlctbmsixhrvbpjb84nnmlcqkzrsix", true);
		assertArrayEquals(new int[] {8,6}, fAl);
		
		String str = "sixfivesix";
		int idx = -1;
		while ((idx = str.indexOf("x", idx + 1))>=0) {
			System.out.println("" + idx);
		}
	}
	
}
