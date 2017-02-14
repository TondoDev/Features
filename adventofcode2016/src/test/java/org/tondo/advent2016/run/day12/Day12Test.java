package org.tondo.advent2016.run.day12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.junit.Test;
import org.tondo.advent2016.day12.Program;

public class Day12Test {

	@Test
	public void testPart1() throws IOException {
		//day12Part1
		InputStream input = this.getClass().getResourceAsStream("/day12/day12Part1.txt");
		try (BufferedReader reader = new BufferedReader( new InputStreamReader(input, Charset.forName("UTF-8")))) {
			Program program = new Program();
			program.load(reader);
			program.execute();
			System.out.println("Day 12 - Part 1: Register 'a': " + program.getRegisterSet().getRegister("a").getValue());
		}
	}
}
