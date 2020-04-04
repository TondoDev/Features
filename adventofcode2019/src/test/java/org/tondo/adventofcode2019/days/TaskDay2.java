package org.tondo.adventofcode2019.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;
import org.tondo.adventofcode2019.DayTaskBase;

public class TaskDay2 extends DayTaskBase {

	public TaskDay2() {
		super(2);
	}
	
	@Test
	public void testPartOneSolution() throws Exception {
		assertEquals("3790689", getPartOneSolution());
	}
	
	@Test
	public void testPartTwoSolution() throws Exception {
		assertEquals("6533", getPartTwoSolution());
	}

	@Override
	public String getPartOneSolution() throws Exception {
		
		List<Integer> program = loadProgram(getPartOneInput());
		// constants give by assignment
		program.set(1,  12);
		program.set(2,  2);
		
		compute(program);
		
		return String.valueOf(program.get(0));
	}
	
	
	@Override
	public String getPartTwoSolution() throws Exception {
		// iput is the same
		List<Integer> program = loadProgram(getPartOneInput());
		// from assignment
		Integer target = 19690720;
		
		
		for (int noun = 0; noun <= 99; noun++) {
			for (int verb = 0; verb <= 99; verb++) {
				List<Integer> progInstance = new ArrayList<Integer>(program);
				progInstance.set(1,  noun);
				progInstance.set(2,  verb);
				compute(progInstance);
				if (target.equals(progInstance.get(0))) {
					System.out.println("" + noun + ", " + verb);
					return String.valueOf(100 * noun + verb);
				}
			}
		}
		
		return "";
	}
	
	public void compute(List<Integer> program) {
		int pc = 0;
		while(true) {
			int code = get(pc, program);
			if (99 == code) {
				break;
			}
				
			int op1 = get(get(pc + 1, program), program);
			int op2 = get(get(pc + 2, program), program);
			int target = get(pc + 3, program);
			if (code == 1) {
				program.set(target, op1+op2);
			} else if (code == 2) {
				program.set(target, op1*op2);
			} else {
				throw new IllegalStateException("Unknow instruction");
			}
			
			pc += 4;
		}
	}

	private Integer get(int addr, List<Integer> prog) {
		if (addr < 0 || addr >= prog.size()) {
			throw new IllegalStateException("SIGSEGV: Index = " + addr);
		}
		return prog.get(addr);
	}

	protected List<Integer> loadProgram(BufferedReader reader) throws IOException {
		List<Integer> retval = new ArrayList<Integer>();

		String line = null;
		while ((line = reader.readLine()) != null) {
			Stream.of(line.split(",")).map(Integer::valueOf).forEachOrdered(retval::add);
		}

		return retval;
	}

	@Test
	public void testLoadProgram() throws IOException {
		System.out.println(loadProgram(createBufferedReader("1,2,5,")));
	}
	
	@Test
	public void testCompute() {
		List<Integer> program = Arrays.asList(1,9,10,3,2,3,11,0,99,30,40,50);
		compute(program);
		assertEquals(Arrays.asList(3500, 9, 10, 70, 2, 3, 11, 0, 99, 30, 40, 50), program);
		
		
		program = Arrays.asList(1,0,0,0,99);
		compute(program);
		assertEquals(Arrays.asList(2,0,0,0,99), program);
		
		program = Arrays.asList(2,3,0,3,99);
		compute(program);
		assertEquals(Arrays.asList(2,3,0,6,99), program);
		
		
		program = Arrays.asList(2,4,4,5,99,0);
		compute(program);
		assertEquals(Arrays.asList(2,4,4,5,99,9801), program);
		
		program = Arrays.asList(1,1,1,4,99,5,6,0,99);
		compute(program);
		assertEquals(Arrays.asList(30,1,1,4,2,5,6,0,99), program);
	}
	
}
