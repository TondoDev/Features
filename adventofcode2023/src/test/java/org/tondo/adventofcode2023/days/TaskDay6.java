package org.tondo.adventofcode2023.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.Test;
import org.tondo.adventofcode2023.DayTaskBase;

public class TaskDay6 extends DayTaskBase{

	public TaskDay6() {
		super(6);
	}

	@Test
	public void testPartOne() throws Exception {
		assertEquals("2612736", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("29891250", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			return "" + numberOfWaysMultiplied(reader) ;
		}
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			return "" + numberOfWaysInLongRace(reader) ;
		}
	}
	
	private long numberOfWaysMultiplied(BufferedReader reader) throws IOException {
		long product = 1;
		long[][] raceRecords = readInput(reader);
		
		for (int i = 0; i < raceRecords.length; i++) {
			long numOfWays = numberOfWaysToBeatRecord(raceRecords[i][0], raceRecords[i][1]);
			product *=numOfWays;
		}
		
		return product;
	}
	
	private long numberOfWaysInLongRace(BufferedReader reader) throws IOException {
		long[][] raceRecords = readInput(reader);
		
		StringBuilder sbTime = new StringBuilder();
		StringBuilder sbRecord = new StringBuilder();
		// concatenate to sring
		for (int i = 0; i < raceRecords.length; i++) {
			sbTime.append(raceRecords[i][0]);
			sbRecord.append(raceRecords[i][1]);
		}
		
		long time = Long.parseLong(sbTime.toString());
		long record = Long.parseLong(sbRecord.toString());
		
		return numberOfWaysToBeatRecord(time, record);
	}
	
	private long numberOfWaysToBeatRecord(long raceTime, long record) {
		// assume it is quadrati equation, to find zero points
		// first and last points achieving record
		
		long discriminant = raceTime * raceTime - (4*record);
		double square = Math.sqrt(discriminant);
		long lowerPoint = getIntegralPoint((raceTime - square)/2, true /* lower point*/);
		long upperPoint = getIntegralPoint((raceTime + square)/2, false /* higher point*/);
		
		return upperPoint - lowerPoint + 1;
	}
	
	private long getIntegralPoint(double value, boolean lowerPoint) {
		double floor = Math.floor(value);
		double ceil = Math.ceil(value);
		long floorLong = (long)floor;
		long ceilLong = (long)ceil;
		// it the distance is equal to record, values must be updated to beat the record
		boolean isExactPoint = (floorLong == ceilLong);
		if (lowerPoint) {
			return isExactPoint ? ceilLong + 1 : ceilLong;
		}  else {
			return isExactPoint ? floorLong - 1 : floorLong;
		}
	}
	
	private long[][] readInput(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		long[] times = parseNumbers(line);
		line = reader.readLine();
		long[] records = parseNumbers(line);
		if (times.length != records.length) {
			throw new IllegalStateException("Number of times and numbner of records doesn't mathc");
		}
		
		long[][] retVal = new long[times.length][2];
		for (int i = 0; i < times.length; i++) {
			retVal[i][0] = times[i];
			retVal[i][1] = records[i];
		}
		
		return retVal;
	}
	
	private long[] parseNumbers(String line) {
		String[] parts = line.split(":");
		// label is thrown away
		String[] strNums = parts[1].trim().split("\\s+");
		
		long[] retVal = new long[strNums.length];
		for (int i = 0; i < retVal.length; i++) {
			retVal[i] = Long.parseLong(strNums[i].trim());
		}
		return retVal;
	}
	
	private static final String[] INPUT_SAMPLE_P1 = {
		"Time:      7  15   30",
		"Distance:  9  40  200"
	};
	
	
	@Test
	public void testReadInput() throws IOException {
		BufferedReader reader = createBufferedReader(INPUT_SAMPLE_P1);
		long[][] readInput = readInput(reader);
		assertEquals(3L, readInput.length);
		assertEquals(15L, readInput[1][0]);
		assertEquals(40L, readInput[1][1]);
	}
	
	@Test
	public void testSampleIptuP1() throws IOException {
		BufferedReader reader = createBufferedReader(INPUT_SAMPLE_P1);
		assertEquals(288L, numberOfWaysMultiplied(reader));
	}
	
	@Test
	public void testSampleInputP2() throws IOException {
		BufferedReader reader = createBufferedReader(INPUT_SAMPLE_P1);
		assertEquals(71503L, numberOfWaysInLongRace(reader));
	}
	
	@Test
	public void testNumberOfWays() {
		assertEquals(4L, numberOfWaysToBeatRecord(7, 9));
		assertEquals(8L, numberOfWaysToBeatRecord(15, 40));
		assertEquals(9L, numberOfWaysToBeatRecord(30, 200));
	}
}
