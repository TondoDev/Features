package org.tondo.advent2016.day15;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BallMachine {
	private List<Disc> discs;
	
	public BallMachine(List<Disc> discs) {
			this.discs = new ArrayList<>(discs);
	}
	
	public void appendDisc(Disc disc) {
		this.discs.add(disc);
	}
	
	public int calculateInitialTime() {
		Map<Integer, Integer> container = new HashMap<>();
		for (int rotation = 0; rotation <= 10000000; rotation++) {
			for (int c = 0; c < discs.size(); c++) {
				Disc currDisc = this.discs.get(c);
				int time = currDisc.getNumOfPositions()*rotation + currDisc.getLoopOffset();
				Integer matchedBuckets = container.get(time);
				if (matchedBuckets == null) {
					matchedBuckets = 0;
				}
				container.put(time, ++matchedBuckets);
				
				if (matchedBuckets == this.discs.size()) {
					System.err.println("Rotation: " + rotation);
					return time;
				}
			}
		}
		// nothing found
		return -1;
	}
}
