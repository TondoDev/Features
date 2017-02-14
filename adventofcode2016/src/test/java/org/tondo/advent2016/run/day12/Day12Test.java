package org.tondo.advent2016.run.day12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.junit.Test;

public class Day12Test {

	@Test
	public void testPart1() throws IOException {
		InputStream input = this.getClass().getResourceAsStream("/day12/day12Part1.txt");
		try (BufferedReader reader = new BufferedReader( new InputStreamReader(input, Charset.forName("UTF-8")))) {
		
		}
	}
}
