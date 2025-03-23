package org.tondo.adventofcode2023.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.tondo.adventofcode2023.DayTaskBase;

public class TaskDay8 extends DayTaskBase{

	public TaskDay8() {
		super(8);
	}
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("13301", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		// used help from internet
		assertEquals("7309459565207", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			String start = "AAA";
			String stop = "ZZZ";
			return ""  + getNumberOfStepsToTarget(start, stop, reader) ;
		}
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			// states ending on that letter
			String start = "A";
			String stop = "Z";
			return "" + getNumberOfStatesSimultaneous(start, stop, reader);
		}
	}
	
	private long getNumberOfStepsToTarget(String startState, String stopState, BufferedReader reader) throws IOException {
		DirectionProvider dirPorvider = readPrologue(reader);
		
		Map<String, String[]> directionMap = readMap(reader);
		String state = startState;
		long stepCounter = 0L;
		while (!stopState.equals(state)) {
			char nextDir = dirPorvider.nextDir();
			state = getNextState(nextDir, state, directionMap);
			stepCounter++;
		}
		
		return stepCounter;
	}
	
	private long getNumberOfStatesSimultaneous(String startState, String stopState, BufferedReader reader) throws IOException {
		DirectionProvider dirPorvider = readPrologue(reader);
		Map<String, String[]> directionMap = readMap(reader);
		
		// list of start states
		List<String> currentStates = directionMap.keySet().stream()
				.filter(k -> k.endsWith(startState))
				.collect(Collectors.toList());
		
		// will keep the steps count to reach end state for the first time
		// Note: got from internet, that these end states are repatively 
		// reached with the same steps count (periode)
		long[] firstEndSteps = new long[currentStates.size()];
		Arrays.fill(firstEndSteps, -1);
		
		int finishedStates = 0;
		int statesCount = currentStates.size();
		long stepsCounter = 0L;
		
		while (finishedStates != statesCount) {
			char nextDir = dirPorvider.nextDir();
			stepsCounter++;
			finishedStates = doTheMove(nextDir, currentStates, firstEndSteps, stopState, directionMap, stepsCounter);
		}
		
		for (int i = 0; i < firstEndSteps.length; i++) {
			System.out.println(firstEndSteps[i ]);
		}
		
		return lcmOfArray(firstEndSteps);
	}
	
	private long lcmOfArray(long[] numbers) {
		// lcm of more numbers is reduced by applying lcm() method to result and next element 
		long lcmCurrent = numbers[0];
		for (int i = 0; i < numbers.length; i++) {
			lcmCurrent = lcm(lcmCurrent, numbers[i]);
		}
		
		return lcmCurrent;
	}
	
	
	private long lcm(long n1, long n2) {
		if (n1 == 0L || n2 == 0L) {
			return 0L;
		}
		
		long higher, lower;
		if (n1 > n2) {
			higher = n1;
			lower = n2;
		} else {
			higher = n2;
			lower = n1;
		}
		
		long lcm = higher;
		while (lcm % lower != 0L) {
			lcm += higher;
		}
		
		return lcm;
	}
	
	private int firstEndsReached(long[] firstEndSteps) {
		int cnt = 0;
		for (int i = 0; i < firstEndSteps.length; i++) {
			if (firstEndSteps[i] >= 0) {
				cnt++;
			}
		}
		
		return cnt;
	}
	

	private String getNextState(char direction, String currentState, Map<String, String[]> directionMap) {
		int dirIndex = -1;
		if ('R' == direction) {
			dirIndex = 1;
		} else if ('L' == direction) {
			dirIndex = 0;
		} else {
			throw new IllegalStateException("Invalid directio character: " + direction);
		}
		
		String[] leftOrRight = directionMap.get(currentState);
		return leftOrRight[dirIndex];
	}
	
	private int doTheMove(char nextDir, List<String> currentStates, long[] firstEndStateReach,String stopState, Map<String, String[]> directionMap, long cnt) {
		int len = currentStates.size();
		
		int finishedCount  = 0;
		for (int i = 0; i < len; i++) {
			if (firstEndStateReach[i] >= 0) {
				continue;
			}
			
			String nextState = getNextState(nextDir, currentStates.get(i), directionMap);
			currentStates.set(i, nextState);
			if (nextState.endsWith(stopState)) {
				firstEndStateReach[i] = cnt;
				finishedCount = firstEndsReached(firstEndStateReach);
			}
		}
		// if no new ends state reached, it doesn't matter what it returns
		// it must be just different from number of states
		return finishedCount;
	}
	
	private DirectionProvider readPrologue(BufferedReader reader) throws IOException {
		// first lien directions
		String line = reader.readLine();
		DirectionProvider dirPorvider = new DirectionProvider(line);
		// empty line skipped
		reader.readLine();
		
		return dirPorvider;
	}
	
	
	private Map<String, String[]> readMap(BufferedReader reader) throws IOException {
		Map<String, String[]> directionMap = new HashMap<>();
		String line = null;
		while((line = reader.readLine()) != null) {
			String[] parts = line.split("=");
			String key = parts[0].trim();
			String[] targetParts = parts[1].trim().substring(1).split(",");
			String left = targetParts[0].trim();
			String right = targetParts[1].trim();
			right = right.substring(0, right.length() - 1);
			
			directionMap.put(key, new String[] {left, right});
		}
		
		return directionMap;
	}
	
	


	private static final String[] SAMPLE_INPUT_P1A = {
			"RL",
            "",
			"AAA = (BBB, CCC)",
			"BBB = (DDD, EEE)",
			"CCC = (ZZZ, GGG)",
			"DDD = (DDD, DDD)",
			"EEE = (EEE, EEE)",
			"GGG = (GGG, GGG)",
			"ZZZ = (ZZZ, ZZZ)"
	};
	
	@Test
	public void testSampleInputP1A() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1A);
		assertEquals(2, getNumberOfStepsToTarget("AAA", "ZZZ", reader));
	}
	
	private static final String[] SAMPLE_INPUT_P1B = {
			"LLR",
			"",
			"AAA = (BBB, BBB)",
			"BBB = (AAA, ZZZ)",
			"ZZZ = (ZZZ, ZZZ)"
	};
	
	@Test
	public void testSampleInputP1B() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1B);
		assertEquals(6, getNumberOfStepsToTarget("AAA", "ZZZ", reader));
	}
	
	private static final String[] SAMPLE_INPUT_P2 = {
			"LR",
            "",
			"11A = (11B, XXX)",
			"11B = (XXX, 11Z)",
			"11Z = (11B, XXX)",
			"22A = (22B, XXX)",
			"22B = (22C, 22C)",
			"22C = (22Z, 22Z)",
			"22Z = (22B, 22B)",
			"XXX = (XXX, XXX)"
	};
	
	@Test
	public void testSampleInputP2() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P2);
		assertEquals(6, getNumberOfStatesSimultaneous("A", "Z", reader));
	}
	
	
	@Test
	public void testLcm() {
		assertEquals(180L, lcm(45L, 60L));
		assertEquals(180L, lcm(60L, 45L));
	}

	private static class DirectionProvider {
		private String directions;
		private int length;
		private int index;
		
		public DirectionProvider(String directions) {
			this.directions = directions;
			this.length = directions.length();
			this.index = 0;
		}
		
		public char nextDir() {
			if (this.index >= this.length) {
				this.index = 0;
			}
			return this.directions.charAt(this.index++);
		}
	}
}
