package org.tondo.adventofcode2022.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.tondo.adventofcode2022.DayTaskBase;

public class TaskDay2  extends DayTaskBase {
	
	private static final String[] SAMPLE_INPUT = {
			"A Y",
			"B X",
			"C Z"
	};
	
	private static class GameRule {
		char shape;
		int score;
		// current shape beats shape in this member
		char beats;
		
		public GameRule(char s, int sc, char b) {
			this.shape = s;
			this.score = sc;
			this.beats = b;
		}
	}
	
	private static final int WIN = 6;
	private static final int LOS = 0;
	private static final int DRAW = 3;
	
	private static final Map<Character, GameRule> RULE_MAP;
	private static final Map<Character, Character> SHAPE_MAP;
	
	static {
		Map<Character, GameRule> tmp = new HashMap<>();
		tmp.put('A', new GameRule('A', 1, 'C'));
		tmp.put('B', new GameRule('B', 2, 'A'));
		tmp.put('C', new GameRule('C', 3, 'B'));
		RULE_MAP = Collections.unmodifiableMap(tmp);
		
		Map<Character, Character> tmpShape = new HashMap<>();
		tmpShape.put('X', 'A'); // rock
		tmpShape.put('Y', 'B'); // paper
		tmpShape.put('Z', 'C'); // scissors
		SHAPE_MAP = Collections.unmodifiableMap(tmpShape);
	}
	

	public TaskDay2() {
		super(2);
	}
	
	
	@Test
	public void testPartOne() throws IOException {
		assertEquals("13526", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws IOException {
		assertEquals("14204", getPartTwoSolution());
	}
	
	
	@Override
	public String getPartOneSolution() throws IOException {
		try (BufferedReader r = getPartOneInput()) {
			return getTotalScoreByInputs(r) + "";
		}
	}
	
	@Override
	public String getPartTwoSolution() throws IOException {
		try (BufferedReader r = getPartOneInput()) {
			return getTotalScoreByRequestedResult(r) + "";
		}
	}
	
	
	private long getTotalScoreByInputs(BufferedReader reader ) throws IOException {
		String line = null;
		long totalScore = 0L;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("\\s+");
			char opponent = parts[0].charAt(0);
			char me = SHAPE_MAP.get(parts[1].charAt(0));
			
			GameRule rule = RULE_MAP.get(opponent);
			// evaluated from my point of view
			if (rule.beats == me) {
				totalScore += LOS;
			} else if (rule.shape == me) {
				totalScore += DRAW;
			} else {
				// I won!
				totalScore += WIN;
			}
			
			// score by my shape
			totalScore += RULE_MAP.get(me).score;
			
		}
		
		return totalScore;
	}
	
	private long getTotalScoreByRequestedResult(BufferedReader reader) throws IOException {
		String line = null;
		long totalScore = 0L;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("\\s+");
			char opponent = parts[0].charAt(0);
			char result = parts[1].charAt(0);
			
			if (result == 'X') {
				// we need to lose
				char myChoiceToLose = RULE_MAP.get(opponent).beats;
				totalScore += RULE_MAP.get(myChoiceToLose).score;
				totalScore += LOS;
			} else if (result == 'Y') {
				// draw
				totalScore += RULE_MAP.get(opponent).score;
				totalScore += DRAW;
			} else { //Z
				// WIN
				int winningShapeScore = RULE_MAP.values().stream().filter(gr -> gr.beats == opponent)
					.map(gr -> gr.score).findFirst().orElse(null);
				totalScore += (WIN + winningShapeScore);
			}
			
		}
		return totalScore;
	}
	
	
	@Test
	public void testSampleInputP1() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT);
		assertEquals(15L, getTotalScoreByInputs(reader));
	}
	
	@Test
	public void testSampleInputP2() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT);
		assertEquals(12L, getTotalScoreByRequestedResult(reader));
	}

}
