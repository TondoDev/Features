package org.tondo.advent2016.day15;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MachineParser {

	private static final Pattern DISC_PARSER = Pattern.compile("Disc #([1-9]+) has ([0-9]+) positions; at time=0, it is at position ([0-9]+).");
	
	
	public BallMachine parse(BufferedReader reader) throws IOException {
		
		String line = null;
		TreeMap<Integer, Disc> orderingCollector = new TreeMap<>();
		while ((line = reader.readLine()) != null) {
			Matcher m = DISC_PARSER.matcher(line);
			if (!m.find()) {
				throw new IllegalStateException("Line: \"" + line + " \"is not valid input");
			}
			
			int order = Integer.parseInt(m.group(1));
			int numOfPos = Integer.parseInt(m.group(2));
			int curr = Integer.parseInt(m.group(3));
			
			if (curr >= numOfPos) {
				throw new IllegalStateException("Currecnt position is bounded from 0 to number of positions - 1!");
			}
			
			Disc disc = new Disc(numOfPos, curr, order);
			orderingCollector.put(disc.getOrder(), disc);
		}
		
		List<Disc> discs = new ArrayList<>(orderingCollector.values());
		if (discs.size() == 0 || (discs.size() != discs.get(discs.size() - 1).getOrder())) {
			throw new IllegalStateException("Bad discs configuration!");
		}
		
		return new BallMachine(discs);
	}
}
