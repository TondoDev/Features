package org.tondo.daythirteen;

import java.util.Arrays;
import java.util.Map;

import org.tondo.daynine.PermutationHadler;

public class ArrangementHandler implements PermutationHadler {
	private Map<String, Map<String, Integer>> happinesTable;
	private int bestHappiness;
	private String[] bestSeating;
	private boolean strict;
	
	public  ArrangementHandler(Map<String, Map<String, Integer>> table) {
		this(table, true);
	}
	
	public  ArrangementHandler(Map<String, Map<String, Integer>> table, boolean strict) {
		this.happinesTable = table;
		this.bestHappiness = -1;
		this.strict = strict;
	}
	
	

	@Override
	public void handle(String[] permutation) {
		int currHapp = 0;
		
		for (int i = 0; i < permutation.length; i++) {
			int right = (i+1) < permutation.length ? (i+1) : 0;
			int left = i == 0 ? permutation.length - 1 : (i-1);
			
			// validation of record existence in happiness table
			if (strict) {
				this.validate(permutation[i], permutation[left], permutation[right]);
				currHapp += this.happinesTable.get(permutation[i]).get(permutation[left]);
				currHapp += this.happinesTable.get(permutation[i]).get(permutation[right]);	
			} else {
				currHapp += getHappinessRelaxed(permutation[i], permutation[left]);
				currHapp += getHappinessRelaxed(permutation[i], permutation[right]);
			}
			
		}
		
		if (currHapp > this.bestHappiness) {
			this.bestHappiness = currHapp;
		}
		
	}
	
	/**
	 * when person is missing in happiness table (maybe not invited person) zero is returned, otherwise
	 * standard value from table is returned 
	 */
	private int getHappinessRelaxed(String who, String whom) {
		Map<String, Integer> relation = this.happinesTable.get(who);
		if (relation == null) {
			return 0;
		}
		
		Integer value = relation.get(whom);
		return value == null ? 0 : value;
	}
	
	private void validate(String who, String left, String right) {
		if (this.happinesTable.get(who) == null) {
			throw new IllegalStateException("No record in table for: " + who);
		}
		
		if (this.happinesTable.get(who).get(left) == null) {
			throw new IllegalStateException("No happiness record for: " + who + " (left) " + left);
		}
		
		if (this.happinesTable.get(who).get(right) == null) {
			throw new IllegalStateException("No happiness record for: " + who + " (right) " + right);
		}
		
	}
	
	public String[] getBestSeating() {
		if (bestSeating == null) {
			return null;
		}
		return Arrays.copyOf(bestSeating, bestSeating.length);
	}
	
	public int getBestHappiness() {
		return this.bestHappiness;
	}

}
