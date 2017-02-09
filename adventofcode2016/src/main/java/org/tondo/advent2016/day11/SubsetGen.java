package org.tondo.advent2016.day11;

import java.util.ArrayList;
import java.util.List;

public class SubsetGen {

	// items to combine
	private List<String> items;
	private int size;
	
	// size of maximum subset
	private int maxK;
	// storage for indices
	private int[] indices;
	// currently processed subset size
	private int currentSetSize;
	
	
	public SubsetGen(List<String> items, int k) {
		this.maxK = items.size() >= k ? k : items.size();
		this.items = new ArrayList<>(items);
		this.size = this.items.size();
		this.indices = new int[this.maxK];
		this.currentSetSize = 1;
	}
	
	public boolean hasNext() {
		return this.currentSetSize <= this.maxK;
	}
	
	public List<String> getNext() {
		List<String> rv = new ArrayList<>();
		
		for (int i = 0 ; i < this.currentSetSize; i++) {
			rv.add(this.items.get(this.indices[i]));
		}
		
		if (!incrementIndices(this.currentSetSize)) {
			this.initIndices(++this.currentSetSize);
		}
		return rv;
	}
	
	/*
	 * Indices for K = 3, N = 5
	 * 0 1 2
	 * 0 1 3
	 * 0 1 4
	 * 0 2 3
	 * 0 2 4
	 * 0 3 4
	 * 1 2 3 
	 * 1 2 4
	 * 1 3 4
	 * 2 3 4 
	 */
	private boolean incrementIndices(int k) {
		int i = k - 1;
		// iterate back and finding lower than maximum index
		// which can be rised
		while (i>=0 && isMax(i)) {
			i--;
		}
		
		// for current K, all indices have maximum value
		// so K should be incremented
		if (i < 0) {
			return false;
		}
		
		// move indices by one
		int nextIndex = ++this.indices[i];
		while (++i < k) {
			this.indices[i] = ++nextIndex;
		}
		
		return true;
	}
	
	/**
	 * @param i index into indices ( == k - 1)
	 * @return true if index has maximum value for current K
	 */
	private boolean isMax(int i) {
		return this.indices[i] == this.size + (i - currentSetSize);
	}
	
	private void  initIndices(int k) {
		this.indices = new int[k];
		for (int i = 0; i < k; i++) {
			this.indices[i] = i;
		}
	}
}
