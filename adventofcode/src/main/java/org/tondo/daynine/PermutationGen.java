package org.tondo.daynine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Generating permutations and calling processing callback for each of them
 * 
 * @author TondoDev
 *
 */
public class PermutationGen {
	private long permCount;

	/**
	 * recursive permutaion generator 
	 */
	public void generatePermutations(Collection<String> items, PermutationHadler handler) {
		Set<String> removedDuplicities = new HashSet<>(items);
		String[] perm = new String[removedDuplicities.size()];
		
		int i = 0;
		for (String s : removedDuplicities) {
			perm[i] = s;
			i++;
		}
		
		this.permCount = 0L;
		this.permute(perm, perm.length, handler);
	}
	
	
	private void permute(String[] items, int lastIndex, PermutationHadler handler) {
		this.permCount++;
		
		boolean even = lastIndex % 2 == 0;
		
		if ( lastIndex == 1) {
			handler.handle(items);
		} else {
			for (int i = 0; i < lastIndex - 1; i++) {
				permute(items, lastIndex - 1, handler);
				if (even) {
					swap(items, i, lastIndex - 1);
				} else {
					swap(items, 0, lastIndex - 1);
				}
			}
			
			permute(items, lastIndex - 1, handler);
		}
	}
	
	private void swap(String[] array, int a, int b) {
		String tmp = array[a];
		array[a] = array[b];
		array[b] = tmp;
	}
	
	/**
	 * 
	 * @return number of handled permutation
	 */
	public long getPermCount() {
		return permCount;
	}
}
