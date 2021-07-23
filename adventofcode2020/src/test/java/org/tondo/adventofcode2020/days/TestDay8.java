package org.tondo.adventofcode2020.days;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.tondo.adventofcode2020.DayTaskBase;

public class TestDay8 extends DayTaskBase {
	
	private static final Pattern INSTR_PARSER = Pattern.compile("(acc|nop|jmp) ([+-](0|[1-9]\\d*))$");
	
	private static class Instruction {
		private String code;
		private int operand;
		private boolean visited = false;
		
		public Instruction(String code, int operand) {
			this.code = code;
			this.operand = operand;
		}

		public String getCode() {
			return code;
		}
		
		public void setCode(String code) {
			this.code  = code;
		}

		public int getOperand() {
			return operand;
		}

		public boolean isVisited() {
			return visited;
		}
		
		public void setVisited() {
			this.visited = true;
		}
		
		public void setUnvisited() {
			this.visited = false;
		}
		
	}
	
	private static class Computer {
		private long acc = 0L;
		private int pc = 0;
		
		public long getAcc() {
			return acc;
		}
		
		
		public boolean run(List<Instruction> program) {
			this.pc = 0;
			this.acc = 0;
			int size = program.size();
			while(size > this.pc && this.pc >= 0) {
				Instruction instr = program.get(this.pc);
				//debug(instr);
				if (instr.isVisited()) {
					return false;
				}
				instr.setVisited();
				
				if ("acc".equals(instr.getCode())) {
					this.acc += instr.getOperand();
					this.pc++;
				} else if ("jmp".equals(instr.getCode())) {
					this.pc += instr.getOperand();
				} else if ("nop".equals(instr.getCode())) {
					this.pc++;
				} else {
					throw new IllegalStateException("Unknown instruction code: '" + instr.getCode() + "'!");
				}
			}
			
			return this.pc == size;
		}
		
		public void repairProgram(List<Instruction> program) {
			int lastChangedInstruction = -1;
			int size = program.size();
			while (!run(program)) {
				 program.forEach(i -> i.setUnvisited());
				if (lastChangedInstruction >= 0) {
					swap(program.get(lastChangedInstruction));
				}
				
				lastChangedInstruction++;
				while(lastChangedInstruction < size
						&& !"nop".equals(program.get(lastChangedInstruction).getCode())
						&& !"jmp".equals(program.get(lastChangedInstruction).getCode())) {
					lastChangedInstruction++;
				}
				
				if (lastChangedInstruction >= size) {
					throw new IllegalStateException("Unrecoverable");
				}
				
				swap(program.get(lastChangedInstruction));
			}
			
			System.out.println(lastChangedInstruction);
		}
		
		private static void swap(Instruction instr) {
			if ("jmp".equals(instr.getCode())) {
				instr.setCode("nop");
			} else if ("nop".equals(instr.getCode())) {
				instr.setCode("jmp");
			}
		}
		
		private void debug(Instruction instr) {
			System.out.println("pc: " + this.pc + ", acc: " + this.acc + ", code: " + instr.getCode() + ", operand: " + instr.getOperand() + ", visited: " + instr.isVisited()) ;
		}
	}
	
	

	public TestDay8() {
		super(8);
	}

	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("1867", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("1303", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {
		List<Instruction> program;
		
		try (BufferedReader reader = getPartOneInput()) {
			program = parserProgram(reader);
		}
		
		Computer cpu = new Computer();
		cpu.run(program);
		return "" + cpu.getAcc();
	}
	
	@Override
	public String getPartTwoSolution() throws Exception {
		List<Instruction> program;
		
		try (BufferedReader reader = getPartOneInput()) {
			program = parserProgram(reader);
		}
		
		Computer cpu = new Computer();
		cpu.repairProgram(program);
		return "" + cpu.getAcc();
	}
	
	
	protected BufferedReader getTestInput() {
		String[] input = new String[] {
				"nop +0",
				"acc +1",
				"jmp +4",
				"acc +3",
				"jmp -3",
				"acc -99",
				"acc +1",
				"jmp -4",
				"acc +6"
		};
		
		return createBufferedReader(input);
	}
	
	private List<Instruction> parserProgram(BufferedReader reader) throws IOException {
		
		List<Instruction> program = new ArrayList<>();
		String line = null;
		int tmpCnt = 0;
		while ((line = reader.readLine()) != null) {
			Matcher matcher = INSTR_PARSER.matcher(line);
			if (matcher.find()) {
				//System.out.println("Code: " + matcher.group(1) + ", Op: " + matcher.group(2));
				if ("nop".equals(matcher.group(1)) || "jmp".equals(matcher.group(1))) {
					tmpCnt++;
				}
				program.add(new Instruction( matcher.group(1), Integer.parseInt( matcher.group(2))));
			} else {
				System.out.println("Error instruction: " + line);
			}
		}
		
		System.out.println("TMP: " + tmpCnt);
		return program;
	}
	
	@Test
	public void testParser() throws IOException {
		List<Instruction> program;
		
		try (BufferedReader reader = getTestInput()) {
			program = parserProgram(reader);
		}
		
		assertEquals(9, program.size());
	}
	
	
	@Test
	public void testSample( ) throws IOException {
		List<Instruction> program;
		
		try (BufferedReader reader = getTestInput()) {
			program = parserProgram(reader);
		}
		
		Computer cpu = new Computer();
		cpu.run(program);
		assertEquals(5,  cpu.getAcc());
	}
	
	@Test
	public void testDay2Sample() throws IOException {
		List<Instruction> program;
		try (BufferedReader reader = getTestInput()) {
			program = parserProgram(reader);
		}
		
		Computer cpu = new Computer();
		cpu.repairProgram(program);
		assertEquals(8,  cpu.getAcc());
	}
}
