package org.tondo.advent2016.day14;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KeyCache {
	
	private static class MutableLong {
		private Set<Long> values;
		public MutableLong(long initVal) {
			this.values = new HashSet<>();
			this.values.add(initVal);
		}
		
		public Set<Long> getValues() {
			return values;
		}
		
		public void addValue(long value) {
			this.values.add(value);
		}
		@Override
		public String toString() {
			return this.values.toString();
		}
	}
	
	private final int KEY_LIFE;
	private Map<String, MutableLong> indexCache;
	private long highestTrippletIndex;
	
	public KeyCache(int life) {
		this.KEY_LIFE = life;
		this.highestTrippletIndex = 0;
		this.indexCache = new HashMap<>();
	}

	public void registerPossibleChar(String ch, long index) {
		MutableLong mutable = this.indexCache.get(ch);
		if (mutable == null) {
			this.indexCache.put(ch, new MutableLong(index));
		} else {
			mutable.addValue(index);
		}
	}
	
	public long getHighestTrippletIndex() {
		return highestTrippletIndex;
	}
	
	public List<Long> matchedTripples(String ch, long index) {
		MutableLong mutable = this.indexCache.get(ch);
		if (mutable == null) {
			return Collections.emptyList();
		}
		List<Long> rv = new ArrayList<>();
		Iterator<Long> iter = mutable.getValues().iterator();
		while (iter.hasNext()) {
			long val = iter.next();
			if (index > val) {
				if (((index - val) <= KEY_LIFE)) {
					rv.add(val);
					if (this.highestTrippletIndex < val) {
						this.highestTrippletIndex = val;
					}
				}
				
				iter.remove();
			}
		}
		
//		boolean inRange = (index - mutable.getValue()) <= KEY_LIFE;
//		// little maintenance
//		if (!inRange) {
//			this.indexCache.remove(ch);
//		} else if (mutable.getValue() > this.highestTrippletIndex){
//			this.highestTrippletIndex = mutable.getValue();
//		}
//		
//		return inRange;
		return rv;
	}
}
