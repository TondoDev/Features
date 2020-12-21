package org.tondo.adventofcode2020.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.Test;
import org.tondo.adventofcode2020.DayTaskBase;

public class TestDay6 extends DayTaskBase {

	public TestDay6() {
		super(6);
	}
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("6335", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("3392", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {
		int count = 0;
		try (BufferedReader reader = getPartOneInput()) {
			int[] answers;
			while ((answers = readGropuAnswersAnyone(reader)) != null) {
				count += getYesAnswersCount(answers);
			}
		}
		
		return "" + count;
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		int count = 0;
		try (BufferedReader reader = getPartOneInput()) {
			int gropAnswers;
			while ((gropAnswers = getCountOfAnswersEvery(reader)) != -1) {
				count += gropAnswers;
			}
		}
		
		return "" + count;
	}
	
	
	protected int[] readGropuAnswersAnyone(BufferedReader reader) throws IOException {
		int[] answers = new int[26];
		
		boolean found = false;
		String line = reader.readLine();
		while (line != null && !line.isEmpty()) {
			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				answers[c - 'a'] = 1;
				found = true;
			}
			
			line = reader.readLine();
		}
		
		if (line == null && !found) {
			return null;
		}
		
		return answers;
	}
	
	protected int getCountOfAnswersEvery(BufferedReader reader) throws IOException {
		int[] answers = new int[26];
		int count = 0;
		int persons = 0;
		String line = reader.readLine();
		while (line != null && !line.isEmpty()) {
			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				answers[c - 'a']++;
			}
			
			persons++;
			line = reader.readLine();
		}
		
		if (line == null && persons == 0) {
			return -1;
		}
		
		for (int i : answers) {
			if (i == persons) {
				count++;
			}
		}
		
		return count;
	}
	
	protected int getYesAnswersCount( int[] answers) {
		int count = 0;
		for (int i = 0; i < answers.length; i++) {
			count += answers[i];
		}
		
		return count;
	}
	
	
	protected BufferedReader getTestInput() {
		String[] input = new String[] {
				"abc",
				"",
				"a",
				"b",
				"c",
				"",
				"ab",
				"ac",
				"",
				"a",
				"a",
				"a",
				"a",
				"",
				"b"
		};
		
		return createBufferedReader(input);
	}

	
	@Test
	public void testGroupRead() throws IOException {
		
		int count = 0;
		try (BufferedReader reader = getTestInput()) {
			int[] answers;
			while ((answers = readGropuAnswersAnyone(reader)) != null) {
				count += getYesAnswersCount(answers);
			}
		}
		
		assertEquals(11,  count);
	}
	
	@Test
	public void testGroupEveryOne() throws IOException {
		int count = 0;
	
		try (BufferedReader reader = getTestInput()) {
			int groupCount;
			
			while((groupCount = getCountOfAnswersEvery(reader)) != -1) {
				count += groupCount;
			}
		}
		
		assertEquals(6, count);
	}
}
