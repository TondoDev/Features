package org.tondo.adventofcode2023.days;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.tondo.adventofcode2023.DayTaskBase;

public class TaskDay3  extends DayTaskBase{

	public TaskDay3() {
		super(3);
	}
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("543867", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("79613331", getPartTwoSolution());
	}
	
	public String getPartOneSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			return "" + getSumOfAllPartsNumber(reader);
		}
	}
	
	
	public String getPartTwoSolution() throws IOException {
		try (BufferedReader reader = getPartOneInput()) {
			return "" + getSumOfAllGearPowers(reader);
		}
	}
	
	

	private long getSumOfAllPartsNumber(BufferedReader reader) throws IOException {
		
		String line = null;
		List<String> engineSchematic = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			engineSchematic.add(line);
		}
		
		long sumOfPartNumbers = 0L;
		int linesCount = engineSchematic.size();
		for (int i = 0; i < linesCount; i++) {
			String currentLine = engineSchematic.get(i);
			int fromIndex = 0;
			PartNumber partNumber = null;
			while ((partNumber = findPartNumber(currentLine, fromIndex)) != null) {
				
				if (isPartNumber(partNumber, engineSchematic, i)) {
					sumOfPartNumbers += partNumber.number;
				}
				
				fromIndex = partNumber.endIdx + 1;
			}
		}
		
		return sumOfPartNumbers;
	}
	
	private long getSumOfAllGearPowers(BufferedReader reader) throws IOException {
		String line = null;
		List<String> engineSchematic = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			engineSchematic.add(line);
		}
		
		int linesCount = engineSchematic.size();
		Map<String, List<Integer>> gearNumbers = new HashMap<>();
		for (int i = 0; i < linesCount; i++) {
			String currentLine = engineSchematic.get(i);
			int fromIndex = 0;
			PartNumber partNumber = null;
			while ((partNumber = findPartNumber(currentLine, fromIndex)) != null) {
				collectGearNumbers(partNumber, engineSchematic, i, gearNumbers);
				fromIndex = partNumber.endIdx + 1;
			}
		}
		
		return getSumOfGearRatios(gearNumbers);
	}
	
	private long getSumOfGearRatios(Map<String, List<Integer>> gearNumbers) {
		
		long sum=0L;
		
		for (Entry<String, List<Integer>> entry : gearNumbers.entrySet()) {
			List<Integer> value = entry.getValue();
			// gear must contain exactly two part numbers
			if (value != null && value.size() == 2) {
				sum += (value.get(0) * value.get(1));
			}
		}
		
		return sum;
	}

	private void collectGearNumbers(PartNumber partNumber, List<String> engineSchematic, int currentLineIndex,
			Map<String, List<Integer>> gearNumbersHolder) {

		// current line
		DeviceSymbol deviceSymbol = getAdjacentPartSymbolInLine(partNumber, engineSchematic.get(currentLineIndex), true);
		putGearInHolder(deviceSymbol, partNumber, currentLineIndex, gearNumbersHolder);
		
		// previous line
		if (currentLineIndex > 0) {
			deviceSymbol = getAdjacentPartSymbolInLine(partNumber, engineSchematic.get(currentLineIndex - 1), false);
			putGearInHolder(deviceSymbol, partNumber, currentLineIndex - 1, gearNumbersHolder);
		}
		
		// following line
		if ((currentLineIndex + 1) < engineSchematic.size()) {
			deviceSymbol = getAdjacentPartSymbolInLine(partNumber, engineSchematic.get(currentLineIndex + 1), false);
			putGearInHolder(deviceSymbol, partNumber, currentLineIndex + 1, gearNumbersHolder);
		}
	}
	
	private void putGearInHolder(DeviceSymbol symbol, PartNumber partNumber, int currentLineIndex, Map<String, List<Integer>> gearNumbersHolder) {
		if (symbol.symbol == '*') {
			// for all gears store the adjacent numbers
			// gear will be identified by its coordinates (map key)
			gearNumbersHolder.computeIfAbsent("" + symbol.posX + "_" + currentLineIndex , k -> new ArrayList<>())
				.add(partNumber.number);
		}
	}

	
	private boolean isPartNumber(PartNumber partNumber, List<String> engineSchematic, int lineIndex) {
		// check for current line
		if (getAdjacentPartSymbolInLine(partNumber, engineSchematic.get(lineIndex), true).symbol != '.') {
			return true;
		}
		
		// previosu line
		if (lineIndex > 0 
				&& getAdjacentPartSymbolInLine(partNumber, engineSchematic.get(lineIndex - 1 ), false).symbol != '.') {
			return true;
		}
		
		// next line
		if ((lineIndex + 1) < engineSchematic.size() 
				&& getAdjacentPartSymbolInLine(partNumber, engineSchematic.get(lineIndex + 1 ), false).symbol !='.') {
			return true;
		}
		
		return false;
	}
	
	// line relation:
	//  0  = same line as number
	// -1  = previous line;
	//  1  = next line
	private DeviceSymbol getAdjacentPartSymbolInLine(PartNumber partNumber, String line, boolean isSameLineAsNumner) {
		int len = line.length();
		if (isSameLineAsNumner) {
			// check left char
			int idx = partNumber.startIdx -1;
			if (idx >= 0) {
				char charAt = line.charAt(idx);
				if (charAt != '.' && !Character.isDigit(charAt)) {
					return new DeviceSymbol(charAt, idx);
				}
			}
			
			idx = partNumber.endIdx + 1;
			if (idx < len) {
				char charAt = line.charAt(idx);
				if (charAt != '.' && !Character.isDigit(charAt)) {
					return new DeviceSymbol(charAt, idx);
				}
			}
		} else {
			int start = partNumber.startIdx;
			if (start>0) {
				start--;
			}
			
			int end = (partNumber.endIdx + 1) >= len ? partNumber.endIdx : (partNumber.endIdx + 1);
			for (int i = start; i <=end; i++) {
				char charAt = line.charAt(i);
				if (charAt != '.' && !Character.isDigit(charAt)) {
					return new DeviceSymbol(charAt, i);
				}
			}
		}
		// dot is a symbol representing no part
		return new DeviceSymbol('.', -1);
	}

	private PartNumber findPartNumber(String line, int fromIndex) {
		// line search is ended
		int len = line.length();
		if (len <= fromIndex) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		int startingIndex = -1;
		for (int i = fromIndex; i < len; i++) {
			char charAt = line.charAt(i);
			if (Character.isDigit(charAt)) {
				if (startingIndex < 0) {
					startingIndex = i;
				}
				sb.append(charAt);
			} else if (startingIndex>=0) {
				// number completed
				break;
			} 
			// else - number not started
		}
		
		// no digit found in line
		if (startingIndex < 0) {
			return null;
		}
		String strNum = sb.toString();
		PartNumber partNumber = new PartNumber();
		partNumber.number = Integer.parseInt(strNum);
		partNumber.startIdx = startingIndex;
		// to keep last index inclusive
		partNumber.endIdx = startingIndex + strNum.length() - 1;
		
		return partNumber;
	}
	

	private static class PartNumber {
		int number;
		// indices are inclusive
		int startIdx;
		int endIdx;
	}
	
	private static class DeviceSymbol {
		public DeviceSymbol(char s, int x) {
			this.symbol = s;
			this.posX = x;
		}
		char symbol;
		int posX;
	}
	
	@Test
	public void testFindNumber() {
		String testLine = "617*....";
		
		PartNumber findPartNumber = findPartNumber(testLine, 0);
		assertEquals(617, findPartNumber.number);
		assertEquals(0, findPartNumber.startIdx);
		assertEquals(2, findPartNumber.endIdx);
		// no more number on that line
		assertNull(findPartNumber(testLine, findPartNumber.endIdx + 1));
		
		testLine = "...$.*....";
		assertNull(findPartNumber(testLine, 0));
		
		testLine = "..35..633.";
		findPartNumber = findPartNumber(testLine, 0);
		assertEquals(35, findPartNumber.number);
		assertEquals(2, findPartNumber.startIdx);
		assertEquals(3, findPartNumber.endIdx);
		findPartNumber = findPartNumber(testLine, findPartNumber.endIdx+1);
		assertNotNull(findPartNumber);
		assertEquals(633, findPartNumber.number);
		assertEquals(6, findPartNumber.startIdx);
		assertEquals(8, findPartNumber.endIdx);
		// no more number on that line
		assertNull(findPartNumber(testLine, findPartNumber.endIdx + 1));
	}
	
	private static final String[] SAMPLE_INPUT_P1 = {
			"467..114..",
			"...*......",
			"..35..633.",
			"......#...",
			"617*......",
			".....+.58.",
			"..592.....",
			"......755.",
			"...$.*....",
			".664.598.."
	};
	
	@Test
	public void testSampleInputP1() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1);
		assertEquals(4361L, getSumOfAllPartsNumber(reader));
	}

	@Test
	public void testSampleInputP2() throws IOException {
		// sample input is the same
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1);
		assertEquals(467835L, getSumOfAllGearPowers(reader));
	}

}
