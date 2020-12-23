package org.tondo.adventofcode2020.days;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.tondo.adventofcode2020.DayTaskBase;

public class TestDay7  extends DayTaskBase{
	
	private static Pattern PARSER = Pattern.compile("(.*) bags contain (.* bag[s]?[,.])+");
	private static Pattern SUBBAG_PARSER = Pattern.compile("(\\d+) (.*) bag[s]?.?");
	
	private static final String SHINY_GOLD = "shiny gold";
	
	private static class SubBag {
		private int count;
		private String bag;
		
		public SubBag(int count , String bag) {
			this.count = count;
			this.bag = bag;
		}
		
		public int getCount() {
			return count;
		}
		
		public String getBag() {
			return bag;
		}
		
		@Override
		public String toString() {
			return "["+this.bag + ", " + this.count +"]";
		}
	}

	public TestDay7() {
		super(7);
	}
	
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("151", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("41559", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {
		
		Map<String, List<SubBag>> register = new HashMap<>();
		initBagRegister(register);
		
		Set<String> suitableBags = new HashSet<>();
		LinkedList<String> examinedBags = new LinkedList<>();
		// add bags which could directly contain GOLD bag
		populateList(SHINY_GOLD, examinedBags, register);
		
		while (!examinedBags.isEmpty()) {
			String bag = examinedBags.removeFirst();
			suitableBags.add(bag);
			populateList(bag, examinedBags, register);
		}
		
		return "" + suitableBags.size();
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		Map<String, List<SubBag>> register = new HashMap<>();
		initBagRegister(register);
		
		long containedBagsCount =  calculateNumberOfBags(SHINY_GOLD, register);
		
		return "" + containedBagsCount;
	}

	
	protected long calculateNumberOfBags(String bagName, Map<String, List<SubBag>> register) {
		List<SubBag> subbags = register.get(bagName);
		if (subbags == null) {
			throw new IllegalStateException("Bag with color \"" + bagName + "\" not found!");
		}
		
		// empty bag doesn't contain any subbag
		if (subbags.isEmpty()) {
			return 0;
		}
	
		long count = 0;
		for (SubBag elem : subbags) {
			// for each subbag type add these subbags + their content
			count += ( elem.getCount() + elem.getCount() * calculateNumberOfBags(elem.getBag(), register));
		}
		
		return count;
	}

	private void initBagRegister(Map<String, List<SubBag>> register) throws IOException {
		try (BufferedReader reader = getPartOneInput()) {
			
			String line;
			while ((line = reader.readLine()) != null) {
				if (!registerBag(line, register)) {
					throw new IllegalArgumentException("Unable to parse input: " + line);
				}
			}
		}
	}
	
	protected void populateList(String bagName, LinkedList<String> list, Map<String, List<SubBag>> bagRegister) {
		bagRegister.entrySet().stream().filter(entry -> entry.getValue().stream()
				.anyMatch(sb -> bagName.equals(sb.getBag())))
		.map(e -> e.getKey())
		.forEach(list::add);
	}
	
	protected boolean registerBag(String bagInput, Map<String, List<SubBag>> bagRegister) {
		Matcher matcher = PARSER.matcher(bagInput);
		if (!matcher.find()) {
			return false;
		}
		
		String bagName = matcher.group(1);
		List<SubBag> subbags = new ArrayList<>();
		for (String subBagStr : matcher.group(2).split("[,.]")) {
			Matcher subBagMatcher = SUBBAG_PARSER.matcher(subBagStr);
			if (!"no other bags".equals(subBagStr)) {
				if (!subBagMatcher.find()) {
					System.out.println("subbags: " + subBagStr);
					return false;
				}
				
				int count = Integer.parseInt(subBagMatcher.group(1));
				String subBagName = subBagMatcher.group(2);
				subbags.add(new SubBag(count, subBagName));
			}
		}
		
		if (bagRegister.put(bagName, subbags) != null) {
			throw new IllegalStateException("Bag \"" + bagName + "\" is already registered!");
		}
		
		return true;
	}
	
	
	protected BufferedReader getTestInput() {
		String[] input = new String[] {
				"shiny gold bags contain 2 dark red bags.",
				"dark red bags contain 2 dark orange bags.",
				"dark orange bags contain 2 dark yellow bags.",
				"dark yellow bags contain 2 dark green bags.",
				"dark green bags contain 2 dark blue bags.",
				"dark blue bags contain 2 dark violet bags.",
				"dark violet bags contain no other bags."
		};
		
		return createBufferedReader(input);
	}
	
	
	@Test
	public void testBagCounting() throws IOException {
		Map<String, List<SubBag>> register = new HashMap<>();
		try (BufferedReader reader = getTestInput()) {
			
			String line;
			while ((line = reader.readLine()) != null) {
				if (!registerBag(line, register)) {
					throw new IllegalArgumentException("Unable to parse input: " + line);
				}
			}
		}
		
		
		assertEquals(126L, calculateNumberOfBags(SHINY_GOLD, register));
	}
	
	@Test
	public void testParsingPattern() {
		String input = "drab indigo bags contain 1 bright coral bag, 5 plaid tomato bags, 3 muted chartreuse bags.";
		
		Matcher matcher = PARSER.matcher(input);
		System.out.println(matcher.find());
		System.out.println(matcher.groupCount());
		System.out.println(matcher.group(1));
		System.out.println(matcher.group(2));
		System.out.println(Arrays.asList(matcher.group(2).split("[,.]")));
		
		Arrays.asList(matcher.group(2).split("[,.]")).stream().map(str -> SUBBAG_PARSER.matcher(str)).forEach(m -> {
			if (m.find()) {
				System.out.println("Number: " + m.group(1));
				System.out.println("Bag: " + m.group(2));
			}
		});
		//System.out.println(matcher.group(3));
	}
	
	@Test
	public void testRegisterBag() {
		String input = "drab indigo bags contain 1 bright coral bag, 5 plaid tomato bags, 3 muted chartreuse bags.";
		Map<String, List<SubBag>> register = new HashMap<>();
		
		assertTrue(registerBag(input, register));
		assertEquals(1, register.size());
		
		Map.Entry<String, List<SubBag>> entry = register.entrySet().iterator().next();
		assertEquals("drab indigo", entry.getKey());
		assertEquals(3, entry.getValue().size());
	}
	
	
	
	
	

}
