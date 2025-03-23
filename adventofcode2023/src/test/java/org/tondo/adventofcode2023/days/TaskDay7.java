package org.tondo.adventofcode2023.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;
import org.tondo.adventofcode2023.DayTaskBase;

public class TaskDay7 extends DayTaskBase {
	
	private static final String CARDS_POWERS = "23456789TJQKA";
	private static final String CARDS_POWERS_JOKER = "J23456789TQKA";
	// possible hands combinations
	private static final String KIND_5 = "kind5";
	private static final String KIND_4 = "kind4";
	private static final String FULL_HOUSE = "fullhouse";
	private static final String KIND_3 = "kind3";
	private static final String TWO_PAIR = "twopair";
	private static final String ONE_PAIR = "onepair";
	private static final String HIGH_CARD = "hc";
	
	private static final String[] HAND_POWER = {
		HIGH_CARD,
		ONE_PAIR,
		TWO_PAIR,
		KIND_3,
		FULL_HOUSE,
		KIND_4,
		KIND_5
	};
	

	public TaskDay7() {
		super(7);
	}
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("253603890", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("253630098", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			return ""  + getTotalWinnings(reader) ;
		}
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			return ""  + getTotalWinningsUsingJokers(reader);
		}
	}
	
	private static class Hand {
		String cards;
		int bid;
		
		public Hand(String c, int b) {
			this.cards = c;
			this.bid = b;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Hand)) {
				return false;
			}
			
			Hand tmp = (Hand) obj;
			
			if (this == tmp) {
				return true;
			}
			
			return Objects.equals(this.cards, tmp.cards) && this.bid == tmp.bid;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.cards, this.bid);
		}
		
		@Override
		public String toString() {
			return this.cards + " - " + this.bid;
		}
	}
	
	
	private static class HandComparator implements Comparator<Hand> {
		
		private boolean jokers = false;
		
		public HandComparator(boolean useJokers) {
			this.jokers = useJokers;
		}
		

		@Override
		public int compare(Hand o1, Hand o2) {
			int length = o1.cards.length();
			int i = 0;
			while (i < length && o1.cards.charAt(i) == o2.cards.charAt(i)) {
				i++;
			}
			// I assume it should not happend
			if (i == length){
				//System.out.println("Hands are equal: " + o1.cards);
				return 0;
			}
			
			String cardPowers = this.jokers ? CARDS_POWERS_JOKER : CARDS_POWERS;
			
			return getCardStrength(o1.cards.charAt(i), cardPowers) - getCardStrength(o2.cards.charAt(i), cardPowers)  ;
		}
		
	}
	
	private long getTotalWinnings(BufferedReader reader) throws IOException {
		
		Map<String, TreeSet<Hand>> hands = new HashMap<>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			Hand hand = readHand(line);
			String stregth =  getHandStrength(hand);
			addHand(hands, hand, stregth, false /* not using jokers*/);
		}
		
		return calculateSumOfBids(hands);
	}
	
	private long getTotalWinningsUsingJokers(BufferedReader reader) throws IOException {
		Map<String, TreeSet<Hand>> hands = new HashMap<>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			Hand hand = readHand(line);
			String stregth =  getHandStrengthJokers(hand);
			addHand(hands, hand, stregth, true /* use jokers */);
		}
		
		return calculateSumOfBids(hands);
	}
	
	private void addHand(Map<String, TreeSet<Hand>> hands, Hand hand, String handStrength, boolean useJokers) {
		hands.computeIfAbsent(handStrength, k -> new TreeSet<>(new HandComparator(useJokers))).add(hand);
	}

	private String getHandStrength(Hand hand) {
		Map<String, Long> counts = hand.cards.chars().mapToObj(i -> Character.toString((char)i))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		
		boolean three = false;
		int two = 0;
		for (Long cnt : counts.values()) {
			if (cnt == 5L) {
				return KIND_5;
			} else if (cnt == 4L) {
				return KIND_4;
			} else if (cnt == 3L) {
				three = true;
			} else if (cnt == 2L) {
				two++;
			}
		}
		
		if (three) {
			if (two == 1) {
				return FULL_HOUSE;
			} else {
				return KIND_3;
			}
		}
		
		if (two == 2) {
			return TWO_PAIR;
		} else if (two == 1) {
			return ONE_PAIR;
		}
		
		return HIGH_CARD;
	}
	
	
	private String getHandStrengthJokers(Hand hand) {
		Map<String, Long> counts = hand.cards.chars().mapToObj(i -> Character.toString((char)i))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		
		long jokers = 0L;
		Long j = counts.get("J");
		if (j != null) {
			jokers = j;
		}
		
		if (jokers == 5L) {
			return KIND_5;
		}
		
		boolean three = false;
		int two = 0;
		for (Map.Entry<String, Long> entry : counts.entrySet()) {
			// we read number of jockers before
			if ("J".equals(entry.getKey()) ) {
				continue;
			}
			
			Long cnt = entry.getValue();
			if (cnt == 5L) {
				return KIND_5;
			} else if (cnt == 4L) {
				if (jokers > 0) {
					return KIND_5;
				}
				return KIND_4;
			} else if (cnt == 3L) {
				if (jokers == 2L) {
					return KIND_5;
				} else  if (jokers == 1L){
					return KIND_4;
				}
				three = true;
			} else if (cnt == 2L) {
				two++;
			}
		}
		if (three) {
			if (two == 1) {
				return FULL_HOUSE;
			} else {
				return KIND_3;
			}
		}
		
		if (two == 2) {
			if (jokers > 0) {
				return FULL_HOUSE;
			}
			return TWO_PAIR;
		} else if (two == 1) {
			 if (jokers == 3L) {
				return KIND_5;
			} else if (jokers == 2L) {
				return KIND_4;
			} else if (jokers == 1L) {
				return KIND_3;
			}
			return ONE_PAIR;
		}
		
		if (jokers == 4L) {
			return KIND_5;
		} else if (jokers == 3L) {
			return KIND_4;
		} else if (jokers == 2L) {
			return KIND_3;
		} else if (jokers == 1L) {
			return ONE_PAIR;
		}
		
		return HIGH_CARD;
	}


	private static int getCardStrength(char c, String cardPowers) {
		int power = cardPowers.indexOf(c);
		if (power < 0) {
			throw new IllegalArgumentException("Invalid card: " + c);
		}
		
		return power;
	}
	
	private long calculateSumOfBids(Map<String, TreeSet<Hand>> hands) {
		long sum = 0L;
		int rank = 1;
		
		// from lower strength to higher
		for (String handPow : HAND_POWER) {
			TreeSet<Hand> treeSet = hands.get(handPow);
			if (treeSet != null) {
				for (Hand winningHand : hands.get(handPow)) {
					sum +=winningHand.bid*rank;
					rank++;
				}
			}
		}
		
		return sum;
	}
	
	private Hand readHand(String line) {
		String[] parts = line.split("\\s+");
		return new Hand(parts[0].trim(), Integer.parseInt(parts[1].trim()));
	}
	
	private static final String[] SAMPLE_INPUT_P1 = {
			"32T3K 765",
			"T55J5 684",
			"KK677 28",
			"KTJJT 220",
			"QQQJA 483"	
	};
	
	
	@Test
	public void testSampleInputP1() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1);
		assertEquals(6440L, getTotalWinnings(reader));
	}
	
	@Test
	public void testSampleInputP2() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1);
		assertEquals(5905L, getTotalWinningsUsingJokers(reader));
	}
	
	
	@Test
	public void testHandComparator( ) {
		TreeSet<Hand> ordered = new TreeSet<>(new HandComparator(false));
		ordered.add(new Hand("AAKKT", 0));
		ordered.add(new Hand("A3KKT", 0));
		ordered.add(new Hand("A3KKT", 1));
		ordered.add(new Hand("A3KK2", 0));
		
		System.out.println(ordered);
	}
	
	@Test
	public void testOfCounter() {
		String cards = "A3KKT";
		Map<String, Long> counts = cards.chars().mapToObj(i -> Character.toString((char)i))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		
		Map<String, Long> expected = new HashMap<>();
		expected.put("A", 1L);
		expected.put("3", 1L);
		expected.put("T", 1L);
		expected.put("K", 2L);
		
		assertEquals(expected, counts);
		
		counts.forEach((k,v) -> System.out.println("Key: " + k + ", Value: " + v));
	}
	
	
	
	
}
