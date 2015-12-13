package org.tondo.dayseven;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

public class CircuitTest {
	
	@Test
	public void testCircuitAnswer() throws IOException {
		InputStream is = getClass().getResourceAsStream("/dayseven/circuit.txt");
		assertNotNull(is);
		
		Circuit circuit = new Circuit();
		initLogicalCircuit(is, circuit);
		circuit.enable();
		int wireAvalue = circuit.wire("a").getValue();
		System.out.println("Day 7. First part: wire 'a' value: " + wireAvalue);
		assertEquals("Wire 'a' value: ", 956,  wireAvalue);
		
		circuit.disable();
		circuit.wire("b").setValue(wireAvalue);
		circuit.enable();
		//circuit.printWires();
		
		int recalcWireA = circuit.wire("a").getValue();
		System.out.println("Day 7. Second part: wire 'a' recalculated: " + recalcWireA);
		assertEquals("Wire 'a ' recalculated: ", 40149,  recalcWireA);
	}
	
	@Test
	public void testCircuitSamples() throws IOException {
		Circuit circuit = new Circuit();
		
		StringBuilder sb = new StringBuilder();
		sb.append("123 -> x").append("\n")
		 .append("456 -> y").append("\n")
		 .append("x AND y -> d").append("\n")
		 .append("x OR y -> e").append("\n")
		 .append("x LSHIFT 2 -> f").append("\n")
		 .append("y RSHIFT 2 -> g").append("\n")
		 .append("NOT x -> h").append("\n")
		 .append("NOT y -> i").append("\n")
		 .append("y -> route").append("\n");
	
		ByteArrayInputStream is = new ByteArrayInputStream(sb.toString().getBytes());
		initLogicalCircuit(is, circuit);
		circuit.enable();
		
//		for(Signal s : this.wires.values()) {
//			System.out.println(s);
//		}
		
		assertEquals("Wire d", 72, circuit.wire("d").getValue());
		assertEquals("Wire e", 507, circuit.wire("e").getValue());
		assertEquals("Wire f", 492, circuit.wire("f").getValue());
		assertEquals("Wire g", 114, circuit.wire("g").getValue());
		assertEquals("Wire h", 65412, circuit.wire("h").getValue());
		assertEquals("Wire i", 65079, circuit.wire("i").getValue());
		assertEquals("Wire x", 123, circuit.wire("x").getValue());
		assertEquals("Wire y", 456, circuit.wire("y").getValue());
		assertEquals("Wire route same as y", 456, circuit.wire("route").getValue());
	}
	
	
	@Test
	public void testReinitValue() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("123 -> x").append("\n")
		 .append("456 -> y").append("\n")
		 .append("x AND y -> d").append("\n")
		 .append("d AND a -> e").append("\n")
		 .append("a  -> f").append("\n")
		 .append("8 -> a").append("\n");
		
		ByteArrayInputStream is = new ByteArrayInputStream(sb.toString().getBytes());
		Circuit circuit = new Circuit();
		initLogicalCircuit(is, circuit);
		circuit.enable();
		
//		assertEquals("Wire x", 123, circuit.wire("x").getValue());
//		assertEquals("Wire y", 456, circuit.wire("y").getValue());
//		assertEquals("Wire d", 72, circuit.wire("d").getValue());
		
		// reinitialized value
		circuit.disable();
		circuit.wire("a").setValue(5);
		circuit.printWires();
		circuit.enable();
		circuit.printWires();
	}
	
	@Test
	public void testNOT() {
		TransactionManager.newTx();
		Signal s = new Signal("out");
		assertFalse("Signal is not initialized", s.isInitialized());
		
		Gate bitwiseCompl = Gate.getNOT(s, "doesnt mattaer");
		bitwiseCompl.setInputValue("A", 123);
		bitwiseCompl.setEnabled(true);
		assertTrue("Signal has valid data", s.isInitialized());
		assertEquals("Bitwise complement", 65412, s.getValue());
		
		Signal s2 = new Signal("out2");
		Gate bwC2 = Gate.getNOT(s2, "doesnt mattaer");
		bwC2.setInputValue("A", 456);
		bwC2.setEnabled(true);
		assertEquals("Bitwise complement", 65079, s2.getValue());
	}
	
	@Test
	public void testSHIFTS() {
		TransactionManager.newTx();
		Signal lout = new Signal("Lout");
		Gate lshift = Gate.getLSHIFT(lout, "doesnt mattaer");
		lshift.setInputValue("A", 123);
		lshift.setInputValue("B", 2);
		assertEquals("Left shift", 492, lout.getValue());
		
		
		Signal rout = new Signal("Rout");
		Gate rshift = Gate.getRSHIFT(rout, "rout");
		rshift.setInputValue("A", 456);
		rshift.setInputValue("B", 2);
		assertEquals("Right shift", 114, rout.getValue());
	}
	
	@Test
	public void testAND() {
		TransactionManager.newTx();
		Signal out = new Signal("out");
		Signal awire = new Signal("awire");
		Gate andGate = Gate.getAND(out, "TEST AND");
		
		andGate.setInputValue("B", 456);
		awire.addListener("A", andGate);
		assertFalse("output is still not initialized, one input missing", out.isInitialized());
		
		// initializding a wire, listeners shoud propaget output to AND gate
		awire.setValue(123);
		assertTrue("output is now initialized", out.isInitialized());
		assertEquals("AND value", 72, out.getValue());
	}
	
	private void initLogicalCircuit(InputStream is, Circuit c) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		String line = null;
		while((line = reader.readLine()) != null) {
			String[] inout = line.split("->");
			if (inout.length < 2) {
				throw new IllegalStateException("Inpput/output badly defined " + line);
			}
			
			TransactionManager.newTx();
			String outID = inout[1].trim();
			String[] opParts = inout[0].split("\\s+");
			if (opParts.length == 1) {
				// wire is initialized by value
				try {
					c.wire(outID).setValue(Integer.parseInt(opParts[0].trim()));
					c.addConstant(c.wire(outID));
				} catch (NumberFormatException e) {
					c.wire(opParts[0].trim()).addListener(c.wire(outID));
				}
			
			} else if (opParts.length == 2 && "NOT".equals(opParts[0])) {
				// NOT is single operand
				Gate not = c.getGate("NOT", c.wire(outID), line);
				c.setGateOperand(not, "A", opParts[1]);
			} else if (opParts.length == 3) {
				Gate gate = c.getGate(opParts[1], c.wire(outID), line);
				c.setGateOperand(gate, "A", opParts[0]);
				c.setGateOperand(gate, "B", opParts[2]);
			} else {
				throw new IllegalStateException("Inpput/output badly defined " + line);
			}
		}
	}
}
