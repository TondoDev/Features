package org.tondo.adventofcode2019.days;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.tondo.adventofcode2019.DayTaskBase;
import org.tondo.adventofcode2019.day5.Computer;
import org.tondo.adventofcode2019.day5.Computer.Instruction;

public class TaskDay5 extends DayTaskBase {

	public TaskDay5() {
		super(5);
	}
	
	
	@Test
	public void testPartOneSolution() throws Exception {
		assertEquals("7692125", getPartOneSolution());
	}
	
	@Test
	public void testPartTwoSolution() throws Exception {
		assertEquals("14340395", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {
		Computer computer = new Computer();
		computer.loadProgram(getPartOneInput());
		computer.setInputBuffer("1");	// from assignment part 1
		computer.compute();
		List<String> buffer = computer.getOutputBuffer();
		
		return buffer == null ? null : buffer.get(buffer.size() -1);
	}
	
	
	@Override
	public String getPartTwoSolution() throws Exception {
		Computer computer = new Computer();
		computer.loadProgram(getPartOneInput());	// same input as for first part
		computer.setInputBuffer("5");	// from assignment part 2
		computer.compute();
		List<String> buffer = computer.getOutputBuffer();
		
		return buffer == null ? null : buffer.get(buffer.size() -1);
	}
	
	
	// test on iput from day 2 part 1
	@Test
	public void testOriginalComputer() throws IOException {
		Computer computer = new Computer();
		computer.loadProgram(inputForDayAndPart(2, 1));
		computer.getProgram().set(1, 12);
		computer.getProgram().set(2, 2);
		computer.compute();
		String result  = "" + computer.getProgram().get(0);
		assertEquals("3790689", result);
	}
	
	
	@Test
	public void testReadInput() {
		int[] rawmem = new int[] {
			3, 7,
			4, 7,
			99,
			0, 0, 0, 0, 0
		};
		
		List<Integer> program = Arrays.stream(rawmem).boxed().collect(Collectors.toList());
		Computer comp = new Computer();
		comp.setInputBuffer("25");
		comp.loadProgram(program);
		comp.compute();
		System.out.println(comp.getProgram());
	}
	
	

	
	@Test
	public void testAssignableFrom() {
		System.out.println(this.getClass().isAssignableFrom(DayTaskBase.class));
		System.out.println(DayTaskBase.class.isAssignableFrom(this.getClass()));
	}
	
	@Test
	public void testCharToInt() {
		char c  = '7';
		
		System.out.println(c);
		int a = c - 48;
		System.out.println(a);
	}
	
	@Test
	public void testInstructionDecode() {
		Computer computer = new Computer();
		
		Instruction instr = computer.decode(3);
		assertEquals(3, instr.code);
		assertArrayEquals(new int[] {0}, instr.modes);
		
		instr = computer.decode(104);
		assertEquals(4, instr.code);
		assertArrayEquals(new int[] {1}, instr.modes);
		
		instr = computer.decode(2);
		assertEquals(2, instr.code);
		assertArrayEquals(new int[] {0,0,0}, instr.modes);
		
		
		instr = computer.decode(102);
		assertEquals(2, instr.code);
		assertArrayEquals(new int[] {1,0,0}, instr.modes);
		
		instr = computer.decode(1102);
		assertEquals(2, instr.code);
		assertArrayEquals(new int[] {1,1,0}, instr.modes);
		
		instr = computer.decode(99);
		assertEquals(99, instr.code);
		assertArrayEquals(new int[] {}, instr.modes);
	}
	
	
	// test from day 2
	@Test
	public void testCompute() {
		Computer computer = new Computer();
		
		computer.loadProgram(Arrays.asList(1,9,10,3,2,3,11,0,99,30,40,50));
		computer.compute();
		assertEquals(Arrays.asList(3500, 9, 10, 70, 2, 3, 11, 0, 99, 30, 40, 50), computer.getProgram());
		
		
		computer.loadProgram(Arrays.asList(1,0,0,0,99));
		computer.compute();
		assertEquals(Arrays.asList(2,0,0,0,99), computer.getProgram());


		computer.loadProgram( Arrays.asList(2,3,0,3,99));
		computer.compute();
		assertEquals(Arrays.asList(2,3,0,6,99), computer.getProgram());
		
		computer.loadProgram(Arrays.asList(2,4,4,5,99,0));
		computer.compute();
		assertEquals(Arrays.asList(2,4,4,5,99,9801), computer.getProgram());
		
		computer.loadProgram(Arrays.asList(1,1,1,4,99,5,6,0,99));
		computer.compute();
		assertEquals(Arrays.asList(30,1,1,4,2,5,6,0,99), computer.getProgram());
	}
	
	@Test
	public void testConditionals() {
		Computer comp = new Computer();
		// if input == 8 -> 1, otherwise 0
		List<Integer> program = Arrays.asList(3,9,8,9,10,9,4,9,99,-1,8);
		comp.setInputBuffer("8");
		comp.loadProgram(program);
		comp.compute();
		assertEquals(Arrays.asList("1"), comp.getOutputBuffer());
		
		comp.programReset();
		comp.setInputBuffer("7");
		comp.compute();
		assertEquals(Arrays.asList("0"), comp.getOutputBuffer());
		
		//------------------------------------------
		// if input is less than 8 -> 1, otherwise 0
		program = Arrays.asList(3,9,7,9,10,9,4,9,99,-1,8);
		comp.setInputBuffer("8");
		comp.loadProgram(program);
		comp.compute();
		assertEquals(Arrays.asList("0"), comp.getOutputBuffer());
		
		comp.programReset();
		comp.setInputBuffer("7");
		comp.compute();
		assertEquals(Arrays.asList("1"), comp.getOutputBuffer());
		
		
		// immediate mode: if input == 8 -> 1, otherwise 0
		program = Arrays.asList(3,3,1108,-1,8,3,4,3,99);
		comp.setInputBuffer("8");
		comp.loadProgram(program);
		comp.compute();
		assertEquals(Arrays.asList("1"), comp.getOutputBuffer());

		comp.programReset();
		comp.setInputBuffer("7");
		comp.compute();
		assertEquals(Arrays.asList("0"), comp.getOutputBuffer());
		
		
		//------------------------------------------
		// immediate mode: if input is less than 8 -> 1, otherwise 0
		program = Arrays.asList(3,3,1107,-1,8,3,4,3,99);
		comp.setInputBuffer("8");
		comp.loadProgram(program);
		comp.compute();
		assertEquals(Arrays.asList("0"), comp.getOutputBuffer());

		comp.programReset();
		comp.setInputBuffer("7");
		comp.compute();
		assertEquals(Arrays.asList("1"), comp.getOutputBuffer());
	}
	
	@Test
	public void testJupms() {
		Computer comp = new Computer();
		// if input == 0 -> 0, otherwise 1
		List<Integer> program = Arrays.asList(3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9);
		comp.setInputBuffer("8");
		comp.loadProgram(program);
		comp.compute();
		assertEquals(Arrays.asList("1"), comp.getOutputBuffer());
		
		comp.programReset();
		comp.setInputBuffer("0");
		comp.compute();
		assertEquals(Arrays.asList("0"), comp.getOutputBuffer());
		
		// the same in immediate mode
		program = Arrays.asList(3,3,1105,-1,9,1101,0,0,12,4,12,99,1);
		comp.setInputBuffer("8");
		comp.loadProgram(program);
		comp.compute();
		assertEquals(Arrays.asList("1"), comp.getOutputBuffer());
		
		comp.programReset();
		comp.setInputBuffer("0");
		comp.compute();
		assertEquals(Arrays.asList("0"), comp.getOutputBuffer());
	}
	
	@Test
	public void testComplexProgram() {
		Computer comp = new Computer();
		List<Integer> program = Arrays.asList(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
				1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
				999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99);
		
		comp.setInputBuffer("7");
		comp.loadProgram(program);
		comp.compute();
		assertEquals(Arrays.asList("999"), comp.getOutputBuffer());
		
		comp.programReset();
		comp.setInputBuffer("8");
		comp.compute();
		assertEquals(Arrays.asList("1000"), comp.getOutputBuffer());
		
		comp.programReset();
		comp.setInputBuffer("10");
		comp.compute();
		assertEquals(Arrays.asList("1001"), comp.getOutputBuffer());
	}
}
