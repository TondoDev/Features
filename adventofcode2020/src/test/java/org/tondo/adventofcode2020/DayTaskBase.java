package org.tondo.adventofcode2020;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class DayTaskBase {

	private int day;
	
	protected DayTaskBase(int day) {
		this.day = day;
	}
	
	
	public BufferedReader inputForPart(int part) {
		return inputForDayAndPart(this.day, part);
	}
	
	public BufferedReader inputForDayAndPart(int day, int part) {
		String inputPath = "/day"+day +  "/inputPart"+part+".txt";
		
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(
							this.getClass().getResourceAsStream(inputPath)
							)
					);
			
			return bufferedReader;
		} catch (Exception e) {
			throw new IllegalStateException("input \"" + inputPath.toString() + "\" couldn't be read!", e );
		}
	}
	
	
	public BufferedReader getPartOneInput() {
		return inputForPart(1);
	}
	
	
	public BufferedReader getPartTwoInput() {
		return inputForPart(2);
	}
	
	
	public String getPartOneSolution() throws Exception {
		return null;
	}
	
	
	public String getPartTwoSolution() throws Exception {
		return null;
	}
	
	
	protected BufferedReader createBufferedReader(String... strings) {
		String data = Stream.of(strings).collect(Collectors.joining(System.lineSeparator()));
		return new BufferedReader(new StringReader(data));
	}
}
