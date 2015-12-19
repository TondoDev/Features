package org.tondo.daynine;

public class PermutationPrinter implements PermutationHadler{

	@Override
	public void handle(String[] permutation) {
		if (permutation.length == 0) {
			System.out.println("[]");
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (String s : permutation) {
			sb.append(s).append(", ");
		}
		// removing last comma
		sb.setLength(sb.length() - 2);
		sb.append("]");
		
		System.out.println(sb.toString());
	}

}
