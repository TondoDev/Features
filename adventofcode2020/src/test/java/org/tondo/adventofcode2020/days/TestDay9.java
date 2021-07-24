package org.tondo.adventofcode2020.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.tondo.adventofcode2020.DayTaskBase;

public class TestDay9 extends DayTaskBase {

	public TestDay9() {
		super(9);
	}
	
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("21806024", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		String sol = getPartTwoSolution();
		assertEquals("2986195", sol);
	}
	
	
	
	@Override
	public String getPartOneSolution() throws Exception {

		final int PREAMBLE = 25;
		try (BufferedReader reader = getPartOneInput()) {
			return "" + findFirstInvalidNumber(PREAMBLE, reader);
		}
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		// from part 1
		final long WEAK_NUMBER = 21806024L;
		List<Long> data;
		try (BufferedReader reader = getPartOneInput()) {
			data = reader.lines().map(s -> Long.valueOf(s)).collect(Collectors.toList());
		}
		List<Long> sequence = findSuccessiveCount(WEAK_NUMBER, data);
		Collections.sort(sequence);
		return  "" + (sequence.get(0) + sequence.get(sequence.size() - 1));
	}
	
	private List<Long> findSuccessiveCount(long weakNumber, List<Long> data) {
		
		int dataSize = data.size();
		List<Long> buffer = null;
		long sum = 0;
		for (int i = 0; i < dataSize - 1; i++) {
			sum = data.get(i);
			buffer = new ArrayList<>();
			buffer.add(data.get(i));
			int c = i + 1;
			while (c < dataSize && sum < weakNumber) {
				sum += data.get(c);
				buffer.add(data.get(c));
				c++;
			}
			
			if (sum == weakNumber && buffer.size() >=2) {
				return buffer;
			}
		}
		
		return new ArrayList<>();
	}
	
	
	private long findFirstInvalidNumber(int preableLen, BufferedReader reader) throws IOException {
		
		String line;
		int boundary = 0;
		List<Long> numbers = new ArrayList<>(1000);
		while ((line = reader.readLine()) != null) {
			long number = Long.parseLong(line);
			if(numbers.size() < preableLen) {
				numbers.add(number);
			} else if (!isValidNumner(number, numbers, boundary)){
				return number;
			} else {
				boundary++;
				numbers.add(number);
			}
		}
		
		return -1;
	}
	
	private boolean isValidNumner(long number, List<Long> array, int boundary) {
		int arrSize = array.size();
		for (int x = boundary; x < arrSize - 1; x++) {
			for (int y = boundary + 1; y < arrSize; y++) {
				if (array.get(x) + array.get(y) == number) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Test
	public void testSampleInput() throws IOException {
		try (BufferedReader reader = getTestInput()) {
			assertEquals(127L, findFirstInvalidNumber(5, reader));
		}
	}
	
	@Test
	public void testSequenceSumFromSampleInput() throws IOException {
		List<Long> data;
		try (BufferedReader reader = getTestInput()) {
			data = reader.lines().map(s -> Long.valueOf(s)).collect(Collectors.toList());
		}
		final long WEAK_FROM_TEST_INPUT = 127L;
		List<Long> sequence = findSuccessiveCount(WEAK_FROM_TEST_INPUT, data);
		assertEquals(4, sequence.size());
		Collections.sort(sequence);
		assertEquals(62L, sequence.get(0) + sequence.get(sequence.size() - 1));
		
	}
	
	
	protected BufferedReader getTestInput() {
		String[] data = new String[] {
			
				"35",
				"20",
				"15",
				"25",
				"47",
				"40",
				"62",
				"55",
				"65",
				"95",
				"102",
				"117",
				"150",
				"182",
				"127",
				"219",
				"299",
				"277",
				"309",
				"576"
		};
		
		return createBufferedReader(data);
	}
	
}
