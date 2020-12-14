package org.tondo.adventofcode2020.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.tondo.adventofcode2020.DayTaskBase;

public class TaskDay2 extends DayTaskBase {
	
	private static final Pattern POLICY_PATTERN = Pattern.compile("(\\d+)-(\\d+) (.): (.+)");

	public TaskDay2() {
		super(2);
	}
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("467", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("441", getPartTwoSolution());
	}

	
	@Override
	public String getPartOneSolution() throws Exception {
		int validPasswords = 0;
		try(BufferedReader r = getPartOneInput()) {
			String line;
			while ((line = r.readLine()) != null) {
				Matcher parsed = POLICY_PATTERN.matcher(line);
				if (parsed.find()) {
					int occ = (int) findOccurences(parsed.group(3).charAt(0), parsed.group(4));
					int min = Integer.parseInt(parsed.group(1));
					int max = Integer.parseInt(parsed.group(2));
					if (occ >= min && occ <= max)  {
						validPasswords++;
						
					}
				}  else {
					throw new  IllegalArgumentException("Input not valid: " + line);
				}
				
			}
		}
		
		return "" + validPasswords;
	}
	
	
	private BufferedReader testInptusForPart2() {

		String[] inputs = new String[] {
				"1-3 a: abcde", 
				"1-3 b: cdefg", 
				"2-9 c: ccccccccc",
				"2-9 c: c4cccccco",
				"2-5 w: vwwwwwwrf",
				"10-13 f: fffffffffffffff"
			};
				
		return  createBufferedReader(inputs);
	}
	
	
	@Override
	public String getPartTwoSolution() throws Exception {
		int validPasswords = 0;
		
		try(BufferedReader r = getPartOneInput()) {
			String line;
			while ((line = r.readLine()) != null) {
				Matcher parsed = POLICY_PATTERN.matcher(line);
				if (parsed.find()) {
					int occCounter = 0;
					String pwd = parsed.group(4);
					char investigated = parsed.group(3).charAt(0);
					int min = Integer.parseInt(parsed.group(1));
					int max = Integer.parseInt(parsed.group(2));
					
					if (pwd.charAt(min - 1) == investigated) {
						occCounter++;
					}
					if (pwd.length() >= max && pwd.charAt(max - 1) == investigated) {
						occCounter++;
					}
					
					// character must be exactly on one position to be password valid
					if (occCounter == 1) {
						//System.out.println("OK: " + line);
						validPasswords++;
					}
				}  else {
					throw new  IllegalArgumentException("Input not valid: " + line);
				}
			}
		}
		
		return "" + validPasswords;
	}
	
	protected long findOccurences(char c, String str) {
		return str.chars().filter(ccc -> ccc == c).count();
	}
}
