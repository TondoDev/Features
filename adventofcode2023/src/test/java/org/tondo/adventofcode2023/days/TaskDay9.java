package org.tondo.adventofcode2023.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.tondo.adventofcode2023.DayTaskBase;

public class TaskDay9 extends DayTaskBase {

	public TaskDay9() {
		super(9);
	}
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("1898776583", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("1100", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			return "" + getSumOfFutureValues(reader);
		}
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			return "" + getSumOfHistoryValues(reader);
		}
	}
	
	
	private long getSumOfFutureValues(BufferedReader reader) throws IOException {
		String line = null;
		
		long sum = 0L;
		while ((line = reader.readLine()) != null) {
			long[] inputValues = getInputValues(line);
			sum += getExtrapolatedNumbner(inputValues, false /* future */);
		}
		
		return sum;
	}
	
	private long getSumOfHistoryValues(BufferedReader reader) throws IOException {
		String line = null;
		
		long sum = 0L;
		while ((line = reader.readLine()) != null) {
			long[] inputValues = getInputValues(line);
			sum += getExtrapolatedNumbner(inputValues, true /* historic */);
		}
		
		return sum;
	}

	private long[] getInputValues(String line) {
		String[] parts = line.trim().split("\\s+");
		long[] result = new long[parts.length];
		for (int i = 0; i < parts.length; i++) {
			result[i] = Long.parseLong(parts[i].trim());
		}

		return result;
	}
	
	private long getExtrapolatedNumbner(long[] inputValues, boolean historic) {
		List<long[]> stack = new ArrayList<>();
		// initial values
		stack.add(inputValues);
		long[] currentSequence = inputValues;
		// this will prepare stack of sequences
		while (!hasSameValues(currentSequence)) {
			currentSequence = addNewSequence(stack, currentSequence);
		}
		
		return determineExtrapolatedNumberFromStack(stack, historic);
	}
	


	private long determineExtrapolatedNumberFromStack(List<long[]> stack, boolean historic) {
		
		int size = stack.size();
		long[] seq = stack.get(size - 1);
		// for the initial value is used the value from the last sequence
		// (last sequence has all values the same)
		long value = seq[0];
		int index = size - 2; // we will start with the next sequence
		while (index >= 0L) {
			seq = stack.get(index);
			if (historic) {
				long firstValue = seq[0];
				value = firstValue - value;
			} else {
				long lastItem = seq[seq.length - 1];
				value += lastItem;
			}
			index--;
		}
		return value;
	}

	private boolean hasSameValues(long[] currentSequence) {
		for (int i = 0; i < currentSequence.length; i ++) {
			if (currentSequence[0] != currentSequence[i]) {
				return false;
			}
		}
		
		return true;
	}

	private long[] addNewSequence(List<long[]> stack, long[] inputValues) {
		
		long[] result = new long[inputValues.length - 1];
		for (int i = 1; i < inputValues.length; i++) {
			result[i -1] = inputValues[i] - inputValues[i - 1];
		}
		
		stack.add(result);
		return result;
	}
	

	private static final String[] SAMPLE_INPUT_P1 = {
			"0 3 6 9 12 15",
			"1 3 6 10 15 21",
			"10 13 16 21 30 45"	
	};
	
	@Test
	public void testSampleInputP1() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1);
		assertEquals(114L, getSumOfFutureValues(reader));
	}
	
	@Test
	public void testSampleInputP2() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1);
		assertEquals(2L, getSumOfHistoryValues(reader));
	}
}
