package org.tondo.adventofcode2023.days;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.tondo.adventofcode2023.DayTaskBase;

public class TaskDay5 extends DayTaskBase {
	

	public TaskDay5() {
		super(5);
	}
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("993500720", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("4917124", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			return "" + getLowestLocationNumber(reader);
		}
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			return "" + getLowestLocationNumberBySeedsInterval(reader);
		}
	}

	private long getLowestLocationNumber(BufferedReader reader) throws IOException {
		EnvironmentMapping envMap = readEnvironment(reader, false /* seeds as number */);
		List<Long> locations = getSeedsLocation(envMap);
		return locations.stream().mapToLong(i -> i ).min().orElseThrow();
	}
	
	private long getLowestLocationNumberBySeedsInterval(BufferedReader reader) throws IOException {
		EnvironmentMapping envMap = readEnvironment(reader, true /* seeds as interval */);
		return getLowsetSeedLocationByInterval(envMap);
	}
	

	private List<Long> getSeedsLocation(EnvironmentMapping envMap) {
		List<Long> seedLocations = new ArrayList<>();
		
		for (Long seed: envMap.seeds) {
			Long mappedValue = seed;
			// initial mapping from seed
			Mapping mapping = envMap.mapRegister.get("seed");
			// finding the mapping with the required destination
			while (mapping != null && !"location".equals(mapping.dest)) {
				mappedValue = getMappedValue(mapping, mappedValue);
				mapping = envMap.mapRegister.get(mapping.dest);
			}
			
			if (mapping == null) {
				System.out.println("Error doesn't reach the destination");
				seedLocations.add(null);
			} else {
				seedLocations.add(getMappedValue(mapping, mappedValue));
			}
		}
		
		return seedLocations;
	}
	
	private long getLowsetSeedLocationByInterval(EnvironmentMapping envMap) {
		long lowestLocation = -1;
		
		for (Range seedRange : envMap.seedsInterval) {
			List<Range> mappingIntervals = Arrays.asList(seedRange);
			// initial mapping from seed
			Mapping mapping = envMap.mapRegister.get("seed");
			while (mapping != null && !"location".equals(mapping.dest)) {
				//System.out.println("====================================");
				//System.out.println("Mapping " + mapping.src + " => " + mapping.dest);
				//System.out.println("Original size: " + mappingIntervals.size());
				mappingIntervals = createInputIntervals(mappingIntervals, mapping);
				//System.out.println("Input for Mapping size: " + mappingIntervals.size());
				mappingIntervals = mapIntervals(mappingIntervals, mapping);
				//System.out.println("Output from Mapping size: " + mappingIntervals.size());
				mapping = envMap.mapRegister.get(mapping.dest);
			}
			
			if (mapping == null) {
				System.out.println("Error doesn't reach the destination");
			} else {
				mappingIntervals = createInputIntervals(mappingIntervals, mapping);
				List<Range> locationIntervals = mapIntervals(mappingIntervals, mapping);
				long currentMin = getLowsetFromIntervals(locationIntervals);
				System.out.println("Currnet Min: " + currentMin);
				if (lowestLocation < 0 || lowestLocation > currentMin) {
					lowestLocation = currentMin;
				}
			}
		}
		
		
		return lowestLocation;
	}

	private long getLowsetFromIntervals(List<Range> locationIntervals) {
		long lowest = -1;
		for (Range range : locationIntervals) {
			if (lowest < 0 || range.from < lowest) {
				lowest = range.from;
			}
		}
		
		return lowest;
	}

	private List<Range> createInputIntervals(List<Range> inputIntervals, Mapping mapping) {
		
		List<Range> retVal = new ArrayList<>();
		Set<Range> keySet = mapping.intervals.keySet();
		
		Deque<Range> queue = new ArrayDeque<>(inputIntervals);
		
		while(!queue.isEmpty()) {
			Range range = queue.remove();
			boolean rangeTouched = false;
			for (Range key : keySet) {
				// when interval from mapping keys overlap the whole input interval
				if (key.from <= range.from && key.to>= range.to) {
					retVal.add(explRange(range.from, range.to));
					rangeTouched =true;
					break;
				} else if (key.from > range.from && key.from <=range.to && key.to >= range.to) {
					retVal.add(explRange(key.from, range.to));
					// for investigation with next
					queue.add(new Range(range.from, key.from - 1));
					rangeTouched =true;
					break;
				} else if (key.from <= range.from && key.to >= range.from && key.to < range.to) {
					retVal.add(explRange(range.from, key.to));
					// for investigation with next
					queue.add(new Range(key.to +1, range.to));
					rangeTouched =true;
					break;
				} else if (key.from > range.from && key.to < range.to) {
					// range interval covers whole key interval
					retVal.add(explRange(key.from, key.to));
					queue.add(new Range(range.from, key.from - 1));
					queue.add(new Range(key.to + 1, range.to));
					rangeTouched = true;
					break;
				} 
//				else if (key.to < range.from || key.from > range.to) {
//					// completely outside interval, passing range to the
//					// output fort implicit mapping
//					retVal.add(range);
//				}
			}
			
			// reminder: check if range was split by any of key interval
			// if range was not splitted by any input keys, it is passed
			// to the output as a implicit mapping
			if (!rangeTouched) {
				retVal.add(range);
			}
		}
		return retVal;
	}
	
	private static final Range explRange(long from, long to) {
		Range er = new Range(from, to);
		er.explicit = true;
		return er;
	}
	
	private List<Range> mapIntervals(List<Range> mappingIntervals, Mapping mapping) {
		List<Range> retVal = new ArrayList<>();
		for (Range range : mappingIntervals) {
			if (!range.explicit) {
				// implicit mappings propagated directly to output
				retVal.add(range);
			} else {
				for (Entry<Range, Range> entry : mapping.intervals.entrySet()) {
					Range key = entry.getKey();
					if (range.from >= key.from && range.to <= key.to) {
						long targetFromFrom = entry.getValue().from + (range.from - key.from);
						long targetTo = entry.getValue().from + (range.to - key.from);
						retVal.add(new Range(targetFromFrom, targetTo));
					}
				}
			}
		}
		
		return retVal;
	}

	private Long getMappedValue(Mapping mapping, Long keyValue) {
		
		for (Entry<Range, Range> entry : mapping.intervals.entrySet()) {
			Range srcRange = entry.getKey();
 			
			if (srcRange.from <= keyValue && srcRange.to>= keyValue) {
				long offset = keyValue - srcRange.from;
				return entry.getValue().from + offset;
			}
			
		}
		
		// if no explicit mapping found, return the same number
		return keyValue;
	}

	private EnvironmentMapping readEnvironment(BufferedReader reader, boolean seedsAsInterval) throws IOException {
		EnvironmentMapping envMap = new EnvironmentMapping();
		
		String line = null;
		// reading resources
		boolean resources = true;
		Mapping resMapping = null;
		while((line = reader.readLine()) != null) {
			line = line.trim();
			if(resources) {
				if (line.isEmpty()) {
					resources = false;
				} else {
					String[] res = line.split(":");
					if (seedsAsInterval) {
						envMap.setSeedsInterval(createSeedsInterval(res[1]));
					} else {
						List<Long> seeds = Arrays.asList(res[1].trim().split("\\s+")) 
								.stream().map(s -> Long.valueOf(s)).collect(Collectors.toList());
						envMap.setSeeds(seeds);
					}
				}
			} else {
				if (line.endsWith("map:")) {
					String[] mapHead = line.split("\\s+");
					String[] srcDest = mapHead[0].split("-");
					resMapping = new Mapping();
					resMapping.src = srcDest[0];
					resMapping.dest = srcDest[2];
					envMap.addMapping(resMapping);
				} else if (line.trim().isEmpty()) {
					// terminate current mapping
					resMapping = null;
				} else if (resMapping != null) {
					String[] values = line.split("\\s+");
					long destStart = Long.parseLong(values[0]);
					long srcStart = Long.parseLong(values[1]);
					long count = Long.parseLong(values[2]);
					
					Range srcRange = new Range(srcStart, srcStart + count - 1);
					Range destRange = new Range(destStart, destStart + count - 1);
					resMapping.addIntervalMapping(srcRange, destRange);
					
				} else {
					System.out.println("Cound not determine state for line: " + line);
				}
			}
		}
		
		return envMap;
	}
	
	private List<Range> createSeedsInterval(String seedsInString) {
		List<Long> collect = Arrays.asList(seedsInString.trim().split("\\s+")).stream()
			.map(s -> Long.valueOf(s.trim())).collect(Collectors.toList());
		
		List<Range> retVal = new ArrayList<>();
		for (int i = 0; i < collect.size() - 1; i += 2) {
			Long from = collect.get(i);
			Long count = collect.get(i + 1);
			
			Range r = new Range(from, from + count - 1);
			retVal.add(r);
		}
		
		return retVal;
	}

	private static class Range {
		long from;
		long to;
		boolean explicit = false;
		
		public Range(long f, long t) {
			this.from = f;
			this.to = t;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Range)) {
				return false;
			}
			Range param = (Range) obj;
			return param.from == this.from && param.to == this.to;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.from, this.to);
		}
		
		@Override
		public String toString() {
			String explicitMark = this.explicit ? "e, " : "";
			return "<" + explicitMark + this.from + ", " + this.to + ">";
		}
	}
	
	private static class EnvironmentMapping {
		List<Long> seeds = new ArrayList<>();
		List<Range> seedsInterval = new ArrayList<>();
		// mapped by source
		Map<String, Mapping> mapRegister = new HashMap<>();
		
		public void setSeeds(List<Long> newSeeds) {
			if (newSeeds == null) {
				this.seeds = new ArrayList<>();
			} else {
				this.seeds = newSeeds;
			}
		}
		
		public void setSeedsInterval(List<Range> interval) {
			if (interval != null) {
				this.seedsInterval = new ArrayList<>(interval);
			}
		}
		
		public void addMapping(Mapping mapping) {
			this.mapRegister.put(mapping.src, mapping);
		}
	}
	
	private static class Mapping {
		String src;
		String dest;
		
		Map<Range, Range> intervals = new HashMap<>();
		
		public void addIntervalMapping(Range  src, Range dest) {
			intervals.put(src, dest);
		}
	}
	
	
	private static final String[] INPUT_SAMPLE_P1 = {
			"seeds: 79 14 55 13",
            "\n",
			"seed-to-soil map:",
			"50 98 2",
			"52 50 48",
            "\n",
			"soil-to-fertilizer map:",
			"0 15 37",
			"37 52 2",
			"39 0 15",
            "\n",
			"fertilizer-to-water map:",
			"49 53 8",
			"0 11 42",
			"42 0 7",
			"57 7 4",
            "\n",
			"water-to-light map:",
			"88 18 7",
			"18 25 70",
            "\n",
			"light-to-temperature map:",
			"45 77 23",
			"81 45 19",
			"68 64 13",
            "\n",
			"temperature-to-humidity map:",
			"0 69 1",
			"1 0 69",
            "\n",
			"humidity-to-location map:",
			"60 56 37",
			"56 93 4"
	};
	
	@Test
	public void testSampleIptuP1() throws IOException {
		BufferedReader reader = createBufferedReader(INPUT_SAMPLE_P1);
		assertEquals(35, getLowestLocationNumber(reader));
	}
	
	@Test
	public void testSampleInputP2( ) throws IOException {
		BufferedReader reader = createBufferedReader(INPUT_SAMPLE_P1);
		assertEquals(46L, getLowestLocationNumberBySeedsInterval(reader));
	}
	
	@Test
	public void testSeedsIntervalParse() throws IOException {
		BufferedReader reader = createBufferedReader(INPUT_SAMPLE_P1);
		EnvironmentMapping env = readEnvironment(reader, true /* numbers as interval */);
		
		assertEquals(2, env.seedsInterval.size());
		assertEquals(new Range(79L, 92L), env.seedsInterval.get(0));
		assertEquals(new Range(55L, 67L), env.seedsInterval.get(1));
		
		Mapping seedMapping = env.mapRegister.get("seed");
		assertNotNull(seedMapping);
		System.out.println("seeds: " + env.seedsInterval);
		System.out.println("Seed input mapping: ");
		seedMapping.intervals.entrySet().forEach(es -> System.out.println(es.getKey() + " => " + es.getValue()));
		List<Range> inputInterval = createInputIntervals(env.seedsInterval, seedMapping);
		System.out.println("inputIntervals: " + inputInterval);
		List<Range> mapIntervals = mapIntervals(inputInterval, seedMapping);
		System.out.println("Mapped: " + mapIntervals);
	}
}
