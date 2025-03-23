package org.tondo.adventofcode2023.days;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.tondo.adventofcode2023.DayTaskBase;

public class TaskDay2 extends DayTaskBase {
	
	private static final Map<String, Integer> CUBE_BAG_P1;
	static {
		Map<String, Integer> cubeBag = new HashMap<>();
		cubeBag.put("red", 12);
		cubeBag.put("green", 13);
		cubeBag.put("blue", 14);
		CUBE_BAG_P1 = Collections.unmodifiableMap(cubeBag);
	}

	public TaskDay2() {
		super(2);
	}
	@Test
	public void testPartOne() throws Exception {
		assertEquals("2006", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("84911", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			return "" + getSumOfPossibleGames(reader, CUBE_BAG_P1);
		}
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		try(BufferedReader reader = getPartOneInput()) {
			return "" + getSumOfGamePowers(reader);
		}
	}
	
	
	private long getSumOfPossibleGames(BufferedReader reader, Map<String, Integer> bag) throws IOException {
		long sumOfIds = 0L;
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			CubeGame game = parseGame(line);
			if (isGamePossible(game, bag)) {
				sumOfIds += game.gameId;
			}
		}
		
		return sumOfIds;
	}
	
	private long getSumOfGamePowers(BufferedReader reader) throws IOException {
		long sumOfPowers = 0L;
		String line = null;
		
		while ((line = reader.readLine()) != null) {
			CubeGame game = parseGame(line);
			long gamePower = getGamePower(game);
			sumOfPowers += gamePower;
		}
		
		return sumOfPowers;
	}

	private long getGamePower(CubeGame game) {
		Map<String, Integer> minimalCubes = getMinimalGameCubes(game);
		
		// neutral item for multiplication
		long gamePower = 1L;
		
		for (Integer numOfCubes : minimalCubes.values()) {
			if (numOfCubes == 0) {
				System.out.println("Zero cubes in game: " + game.gameId);
			}
			gamePower *= numOfCubes;
		}
		
		return gamePower;
	}
	
	private Map<String, Integer> getMinimalGameCubes(CubeGame game) {
		Map<String, Integer> gameCubes = new HashMap<>();
		gameCubes.put("red", 0);
		gameCubes.put("green", 0);
		gameCubes.put("blue", 0);
		
		for (Map<String, Integer> draw : game.draws) {
			for (Entry<String, Integer> cubes : draw.entrySet()) {
				Integer currentMinCubesForColor = gameCubes.get(cubes.getKey());
				if (currentMinCubesForColor < cubes.getValue()) {
					gameCubes.put(cubes.getKey(), cubes.getValue());
				}
			}
		}
		
		return gameCubes;
	}
	private boolean isGamePossible(CubeGame game, Map<String, Integer> bag) {
		for (Map<String, Integer> single : game.draws) {
			for (Map.Entry<String, Integer> cubes : single.entrySet()) {
				int drawnCubes = cubes.getValue();
				if (!bag.containsKey(cubes.getKey())) {
					throw new IllegalStateException("UnsupportedColor: " + cubes.getKey());
				}
				// if any draw of cubes exceed the number of cubes of given color in back
				// the game is not possible
				if (drawnCubes > bag.get(cubes.getKey())) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private CubeGame parseGame(String line) {
		String[] gameParts = line.split(":");
		// remove String Game to get pure parseable number
		int gameId = Integer.parseInt(gameParts[0].replace("Game", "").trim());
		
		String[] games = gameParts[1].split(";");
		List<Map<String, Integer>> drawsFromBag = new ArrayList<>();
		for (String singleGame : games) {
			String[] cubes = singleGame.trim().split(",");
			
			Map<String, Integer> drawnCubes = new HashMap<>();
			for (String cube : cubes) {
				String[] cubeParts = cube.trim().split("\\s+");
				int numberOfCubes =  Integer.parseInt(cubeParts[0].trim());
				String color = cubeParts[1].trim();
				drawnCubes.put(color, numberOfCubes);
			}
			
			if (!drawnCubes.isEmpty()) {
				drawsFromBag.add(drawnCubes);
			}
		}
		CubeGame cubeGame = new CubeGame();
		cubeGame.gameId = gameId;
		cubeGame.draws = drawsFromBag;
		return cubeGame;
	}
	
	@Test
	public void testGameParse( ) {
		String input = "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green";
		
		CubeGame parsedGame = parseGame(input);
		assertEquals(1, parsedGame.gameId);
		assertEquals(3, parsedGame.draws.size());
		
		Map<String, Integer> cubes = parsedGame.draws.get(0);
		assertEquals(3, (int)cubes.get("blue"));
		assertEquals(4, (int)cubes.get("red"));
		assertNull(cubes.get("green"));
		
		cubes = parsedGame.draws.get(1);
		assertEquals(6, (int)cubes.get("blue"));
		assertEquals(1, (int)cubes.get("red"));
		assertEquals(2, (int)cubes.get("green"));
		
		cubes = parsedGame.draws.get(2);
		assertEquals(2, (int)cubes.get("green"));
		assertNull(cubes.get("blue"));
		assertNull(cubes.get("red"));
		
	}
	
	private final String[] SAMPLE_INPUT_1 =  {
			"Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green",
			"Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue",
			"Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red",
			"Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red",
			"Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"
	};
	
	@Test
	public void testSampleInputP1() throws IOException {
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_1);
		assertEquals(8L, getSumOfPossibleGames(reader, CUBE_BAG_P1));
	}
	
	@Test
	public void testSampleInputP2() throws IOException {
		// same sample input as for P1
		BufferedReader reader = createBufferedReader(SAMPLE_INPUT_1);
		assertEquals(2286L, getSumOfGamePowers(reader));
	}
	
	private static class CubeGame {
		int gameId;
		List<Map<String, Integer>> draws;
	}

}
