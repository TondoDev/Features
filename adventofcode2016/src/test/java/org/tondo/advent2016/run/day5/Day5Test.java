package org.tondo.advent2016.run.day5;

import java.security.NoSuchAlgorithmException;

import org.junit.Test;
import org.tondo.advent2016.day5.PasswordCreator;

/**
 * 
 * @author TondoDev
 *
 */
public class Day5Test {

	@Test
	public void tesPart1() throws NoSuchAlgorithmException {
		final String INPUT = "ojvtpuvg";
		PasswordCreator pwdCreator = new PasswordCreator();
		System.out.println("Day 5 Part 1: password " + pwdCreator.createPasswordSimple(INPUT)); // 4543c154
		System.out.println("Day 5 Part 2: complex password " + pwdCreator.createPasswordComplex(INPUT)); //1050cbbd
	}	
}
