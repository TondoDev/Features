package org.tondo.adventofcode2023.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.junit.Test;
import org.tondo.adventofcode2023.DayTaskBase;

public class TaskDay12 extends DayTaskBase {
	
	public TaskDay12() {
		super(12);
	}
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("7753", getPartOneSolution());
	}
	
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws IOException {
		BufferedReader reader = getPartOneInput();
		return "" + getCountOfAllPossibleArrangements(reader, 1) ;
	}
	
	@Override
	public String getPartTwoSolution() throws IOException {
		BufferedReader reader = getPartOneInput();
		return "" + getCountOfAllPossibleArrangements(reader, 5) ;
	}
	
	private long getCountOfAllPossibleArrangements(BufferedReader reader, int factor) throws IOException {
		String line = null;
		long count = 0L;
		
		while ((line = reader.readLine()) != null) {
			SpringConfig config = parseConfig(line, factor);
			System.out.println(config.chars);
			int configCount = getPossibleConfigrations(config);
			count += configCount;
		}
		
		return count;
	}
	
	private int getPossibleConfigrations(SpringConfig config) {
		
		Stack<Token> tokens = new Stack<>();
		tokens.push(new Token(config, config.chars));
		
		Set<String> stateCache = new HashSet<>();
		
		int possibleConfigs = 0;
		int maxStack = -1;
		long iteration  = 0;
		long skipped = 0L;
		while (!tokens.isEmpty()) {
			
			Token token = tokens.pop();
			// quick check if investigation of this state is needed
			if (!isInvestigationNeeded(token)) {
				skipped++;
				continue;
			}
			
			iteration++;
			int stackSize = tokens.size();
			if (stackSize > maxStack) {
				maxStack = stackSize;
			}
			
			if (iteration % 10000000L == 0) {
				System.out.println("Iter: " + iteration + ", possible: " + possibleConfigs + ", stack size: " + stackSize + ", skipped: " + skipped);
			}
			
			SpringConfig currentConfig = token.config;
			char[] current = token.currentString.toCharArray();
			int numIdx = token.numIdx;
			int seq = token.sequence;
			int i = -1;
			for (i = token.stringIdx; i < current.length; i++) {
				if (current[i] == '.') {
					if (numIdx < currentConfig.config.length && seq > 0) {
						// we are terminating some sequence
						if (seq == currentConfig.config[numIdx]) {
							numIdx++;
						} else {
							// sequence ended earlier
							break;
						}
					}
					
					seq = 0;
				} else if (current[i] == '?') {
					// push possible configuration on stack
					if (seq == 0 || (numIdx < token.config.config.length && seq == token.config.config[numIdx])) {
						String newWithDot = createStringWithReplacement(current, i, '.');
						
						if (stateCache.add(newWithDot)) {
							tokens.push(token.from(newWithDot, i, numIdx, seq));
						} else {
							System.out.println("Boom: " + newWithDot);
						}
					}
					
					
					if (numIdx < token.config.config.length && seq < token.config.config[numIdx]) {
						// only add neew state with # if not exceeds the required sequence
						String newWithHash = createStringWithReplacement(current, i, '#');
						tokens.push(token.from(newWithHash, i, numIdx, seq));
					}
					// nothing to process from this configuration
					// processed will be the string with replacement
					break;
				} else if (current[i] == '#') {
					seq++;
					if (numIdx == currentConfig.config.length || seq > currentConfig.config[numIdx]) {
						// we can discard whole current configuration, because we overrun 
						// the expected sequence, or starting the sequence we don't expect
						break;
					} else if ((i == current.length - 1) && (seq == currentConfig.config[numIdx])) {
						// last character satisfies sequence (not ensured if it is last sequence)
						numIdx++;
					}
				}
			}
			// end of the string and exactly given number of sequences were satisfied
			if (i == current.length && numIdx == currentConfig.config.length) {
				//System.out.println(iteration + ". Recognized: " + token.currentString);
				possibleConfigs++;
			}
		}
		
		System.out.println("Configs: " + possibleConfigs + ", Max stack: " + maxStack + ", Iterations: " + iteration + ", skipped: " + skipped);
		return possibleConfigs;
	}
	
	
	private boolean isInvestigationNeeded(Token token) {
		
		// if configuration is already satisfied but string still contains # so it will 
		// destroy valid configuration - not worth
		if (token.numIdx == token.config.config.length 
				&& token.currentString.substring(token.stringIdx).contains("#")) {
			return false;
		}
		
		if (token.sequence == 0 && token.numIdx < token.config.config.length) {
			int neededChars = token.config.minChars[token.numIdx];
			
			int available = token.config.length - token.stringIdx;
			if (available < neededChars) {
				return false;
			}
		}
		
		return true;
	}

	private String createStringWithReplacement(char[] oldSeq, int replaceIDx, char newChar) {
		char[] newSeq = Arrays.copyOf(oldSeq, oldSeq.length);
		newSeq[replaceIDx] = newChar;
		return String.valueOf(newSeq);
	}
	
	private SpringConfig parseConfig(String stringConfig) {
		return parseConfig(stringConfig, 1);
	}
	
	private SpringConfig parseConfig(String stringConfig, int factor) {
		String[] parts = stringConfig.split("\\s+");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Configuration \"" + stringConfig + "\" is not valid!");
		}
		
		String characters = parts[0].trim();
		
		String[] numbers = parts[1].trim().split(",");
		int[] config = new int[numbers.length];
		for (int i = 0; i < config.length; i++) {
			config[i] = Integer.parseInt(numbers[i].trim());
		}
		
		// multiplication of elements in config 
		int[] unfoldedConfig = new int [config.length * factor];
		for (int f = 0; f < factor; f++) {
			int base = config.length * f;
			for (int i = 0; i < config.length; i++) {
				unfoldedConfig[base + i] = config[i];
			}
		}
		
		// multiplication of input by factor
		String unfoldedCharacters = String.join("?", Collections.nCopies(factor, characters));
		return new SpringConfig(unfoldedCharacters, unfoldedConfig);
	}
	
	private static final String[] SAMPLE_INPUT_P1 = {
			"???.### 1,1,3",
			".??..??...?##. 1,1,3",
			"?#?#?#?#?#?#?#? 1,3,1,6",
			"????.#...#... 4,1,1",
			"????.######..#####. 1,6,5",
			"?###???????? 3,2,1",
	};
	
	@Test
	public void testSamplePart1() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1);
		assertEquals(21L, getCountOfAllPossibleArrangements(reader, 1));
	}
	
	@Test
	public void testSamplePart2() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1);
		assertEquals(525152L, getCountOfAllPossibleArrangements(reader, 5));
	}
	
	@Test
	public void testSingleCinfiguration() {
		assertEquals(1, getPossibleConfigrations(parseConfig("???.### 1,1,3")));
		
		assertEquals(4, getPossibleConfigrations(parseConfig(".??..??...?##. 1,1,3")));
		
		assertEquals(1, getPossibleConfigrations(parseConfig("?#?#?#?#?#?#?#? 1,3,1,6")));
		
		assertEquals(1, getPossibleConfigrations(parseConfig("????.#...#... 4,1,1")));
		
		assertEquals(4, getPossibleConfigrations(parseConfig("????.######..#####. 1,6,5"))); 
		
		assertEquals(10, getPossibleConfigrations(parseConfig("?###???????? 3,2,1")));
	}
	
	@Test
	public void testUnfoldedConfiguration() {
		assertEquals(1, getPossibleConfigrations(parseConfig("???.### 1,1,3", 5)));
		
		assertEquals(16384, getPossibleConfigrations(parseConfig(".??..??...?##. 1,1,3", 5)));

		assertEquals(1, getPossibleConfigrations(parseConfig("?#?#?#?#?#?#?#? 1,3,1,6", 5)));

		assertEquals(16, getPossibleConfigrations(parseConfig("????.#...#... 4,1,1", 5)));
		
		assertEquals(2500, getPossibleConfigrations(parseConfig("????.######..#####. 1,6,5", 5)));
		
		assertEquals(506250, getPossibleConfigrations(parseConfig("?###???????? 3,2,1", 5)));
		
		// stats before optimization
//		Configs: 1, Max stack: 11, Iterations: 69
//		Configs: 16384, Max stack: 17, Iterations: 182563
//		Configs: 1, Max stack: 21, Iterations: 89
//		Configs: 16, Max stack: 22, Iterations: 471
//		Configs: 2500, Max stack: 9, Iterations: 23735
//		Configs: 506250, Max stack: 17, Iterations: 4643025
	}
	
	@Test
	public void testVerySlow() {
		assertEquals(1135341, getPossibleConfigrations(parseConfig("????#???????#????? 1,8,1,1,1", 5)));
		// opt: not adding unnecessary # 
		// Configs: 1135341, Max stack: 51, Iterations: 20177412, skipped: 1777641
		// opt: not adding unnecessary #  and .
		// Configs: 1135341, Max stack: 21, Iterations: 14902600, skipped: 1777641
	}
	
	@Test
	public void testVeryVerySlow() {
		assertEquals(1135341, getPossibleConfigrations(parseConfig("???.?#?????##?#????? 1,2,7,1", 5)));
	}
	
	private static class SpringConfig {
		String chars;
		int[] config;
		int minChars[];
		int length;
		
		public SpringConfig(String s, int[] config) {
			this.chars = s;
			this.config = config;
			this.length = s.length();
			
			int neededChars = 0;
			for (int i = 0; i < this.config.length; i++) {
				neededChars += this.config[i];
				neededChars += 1; // because following dot
			}
			neededChars--;
			this.minChars = new int[this.config.length];
			this.minChars[0] = neededChars;
			for (int i = 1; i < this.config.length ; i++) {
				this.minChars[i] = this.minChars[i - 1] - this.config[i-1] - 1;
			}
			
			//System.out.println("Calc min: " + Arrays.toString(minChars));
		}
	}
	
	
	private static class Token {
		SpringConfig config;
		int stringIdx = 0;
		int numIdx = 0;
		int sequence = 0;
		String currentString;
		
		public Token(SpringConfig conf, String currentString) {
			this.config = conf;
			this.currentString = currentString;
		}
		
		public Token from(String currString, int strI, int nI, int sq) {
			Token copy = new Token(this.config, currString);
			copy.numIdx = nI;
			copy.stringIdx = strI;
			copy.sequence = sq;
			return copy;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("[").append(this.currentString)
				.append(", StringIndex: ").append(this.stringIdx)
				.append(", Current sequence: ").append(this.sequence)
				.append(", NumberIndex: ").append(this.numIdx)
				.append(", expected sq: ").append(this.config.config.length >= this.numIdx ? "Not expecting" : this.config.config[this.numIdx])
				.append("]");
			return sb.toString();
		}
	}

}
