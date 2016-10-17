package org.tondo.Java7Features.math;

import org.junit.Test;

public class BitOperationTest {
	
	@Test
	public void testBitOperations() {
		int N = 1025;
		System.out.println(String.format("%32s",Integer.toBinaryString(N)));
		System.out.println(Integer.toBinaryString(-N));
		int k = N & -N;
		System.out.println(String.format("%32s",Integer.toBinaryString(k)) + " " + k);
		System.out.println(Math.log(k));
		System.out.println("GAP: " + getGap(N));
		System.out.println("GAP: " + getGapLinear(N));
	}
	
	@Test
	public void testBinaryGapLog() {
		getGap(106);
	}
	
	@Test
	public void testLog2() {
		int n = 1;
		System.out.println("log2("+n+") = " +log2(n));
		n=2;
		System.out.println("log2("+n+") = " +log2(n));
		n=4;
		System.out.println("log2("+n+") = " +log2(n));
		n=8;
		System.out.println("log2("+n+") = " +log2(n));
	}
	
	public static int log2(int num) {
		return (int) (Math.log10(num)/Math.log10(2));
	}
	
	public static int getGap(int N) {
		int pre = -1;
		int len = 0;
	 
		while (N > 0) {
			System.out.println(String.format("%32s",Integer.toBinaryString(N)) + " N");
			System.out.println(String.format("%32s",Integer.toBinaryString(-N)) + " -N");
			System.out.println(String.format("%32s",Integer.toBinaryString(N-1)) + " N-1");
			
			int k = N & -N;
			System.out.println(k + " - k = N & -N");
			int curr = log2(k);
			N = N & (N - 1);
	 
			if (pre != -1 && Math.abs(curr - pre) > len) {
				len = Math.abs(curr - pre) - 1;
			}
			System.out.println("pre: " + pre);
			System.out.println("curr: " + curr);
			System.out.println("len: " + len);
			pre = curr;
		}
	 
		return len;
	}
	
	public static int getGapLinear(int N) {
		int max = 0;
		int count = -1;
		int r = 0;
	 
		while (N > 0) {
			// get right most bit & shift right
			r = N & 1;
			N = N >> 1;
	 
			if (0 == r && count >= 0) {
				count++;
			}
	 
			if (1 == r) {
				max = count > max ? count : max;
				count = 0;
			}
		}
	 
		return max;
	}
}
