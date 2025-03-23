package org.tondo.adventofcode2023.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.tondo.adventofcode2023.DayTaskBase;

public class TaskDay4 extends DayTaskBase{

	public TaskDay4() {
		super(4);
	}

	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("21138", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("7185540", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			return "" + getTotalScratchCardPoints(reader);
		}
	}

	
	@Override
	public String getPartTwoSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			return "" + getTotalScratchCardsCount(reader);
		}
	}

	private long getTotalScratchCardPoints(BufferedReader reader) throws IOException {
		String line = null;
		long sumOfPoints = 0L;
		while ((line = reader.readLine()) != null) {
			ScratchCard card = parseCard(line);
			int cardPoints = getCardPoints(card);
			sumOfPoints +=cardPoints;
		}
		return sumOfPoints;
	}
	
	private long getTotalScratchCardsCount(BufferedReader reader) throws IOException {
		String line = null;
		Map<Integer, ScratchCard> cards = new HashMap<>();
		while ((line = reader.readLine()) != null) {
			ScratchCard card = parseCard(line);
			scratchCard(card, cards);
		}	
		
		
		return countCards(cards);
	}
	

	private long countCards(Map<Integer, ScratchCard> cards) {
		
		long sum = 0L;
		for (ScratchCard scratchCard : cards.values()) {
			// to determin that we sum only real cards from the input
			// because some may have been created redundanly
			if (scratchCard.matchingNumbers >=0) {
				sum += scratchCard.count;
			}
		}
		return sum;
	}


	private void scratchCard(ScratchCard card, Map<Integer, ScratchCard> cards) {
		
//		ScratchCard scratchCard = cards.computeIfAbsent(card.id, k -> card);
//		scratchCard.count++;
		
		ScratchCard scratchCard = cards.get(card.id);
		if (scratchCard == null) {
			cards.put(card.id, card);
			scratchCard = card;
		} else {
			scratchCard.winningNumbers = card.winningNumbers;
			scratchCard.myNumbers = card.myNumbers;
		}
		
		scratchCard.count++;
		if (scratchCard.matchingNumbers < 0) {
			scratchCard.matchingNumbers = getCountOFMathingNumbers(scratchCard);
		}
		
		for (int i = 1 ; i <= scratchCard.matchingNumbers; i++) {
			int carId = i + scratchCard.id;
			ScratchCard wonCard = cards.computeIfAbsent(carId, k -> {
				ScratchCard c = new ScratchCard();
				c.id = k;
				c.count = 0;
				return c;
			});
			
			// we in one shod add counts of new ticket according to number
			// of current ticket
			wonCard.count+= scratchCard.count;
		}
		
	}


	private ScratchCard parseCard(String line) {
		String[] mainParts = line.split(":");
		Integer cardId = Integer.valueOf(mainParts[0].split("\\s+")[1].trim());
		
		String[] numSplit = mainParts[1].split("\\|");
		
		Set<Integer> winningNumbers = Arrays.asList(numSplit[0].trim().split("\\s+"))
			.stream().map(s -> Integer.valueOf(s.trim())).collect(Collectors.toSet());
		
		Set<Integer> myNumbers = Arrays.asList(numSplit[1].trim().split("\\s+"))
				.stream().map(s -> Integer.valueOf(s.trim())).collect(Collectors.toSet());
		
		
		ScratchCard card = new ScratchCard();
		card.id = cardId;
		card.myNumbers = myNumbers;
		card.winningNumbers = winningNumbers;
		
		return card;
	}
	
	private int getCardPoints(ScratchCard card) {
		int count = getCountOFMathingNumbers(card);
		if (count == 0) {
			return 0;
		}
		int points = 1;
		
		for (int i = 1; i < count; i++) {
			points *=2;
		}
		return points;
	}
	
	private int getCountOFMathingNumbers(ScratchCard card) {
		Set<Integer> tmpMy = new HashSet<>(card.myNumbers);
		tmpMy.retainAll(card.winningNumbers);
		return tmpMy.size();
	}
	
	private static class ScratchCard {
		int id;
		Set<Integer> winningNumbers;
		Set<Integer> myNumbers;
		int count;
		int matchingNumbers = -1;
	}
	
	private static final String[] SAMPLE_INPUT_P1 = {
		"Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53",
		"Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19",
		"Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1",
		"Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83",
		"Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36",
		"Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11"
	};
	
	private static final String[] SAMPLE_MINI_P2 = {
			"Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53",
			"Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19"
	};
	
	@Test
	public void testSampleInputPart1() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1);
		assertEquals(13, getTotalScratchCardPoints(reader));
	}
	
	@Test
	public void testSampleInputPart2() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_P1);
		assertEquals(30L, getTotalScratchCardsCount(reader));
	}
	
	@Test
	public void testMiniSampleP2() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_MINI_P2);
		assertEquals(3L, getTotalScratchCardsCount(reader));
	}
	
	@Test
	public void testSplit() {
		String[] split = "Janko|Hrasko".split("\\|");
		assertEquals(2, split.length);
		assertEquals("Janko", split[0]);
		assertEquals("Hrasko", split[1]);
	}
}
