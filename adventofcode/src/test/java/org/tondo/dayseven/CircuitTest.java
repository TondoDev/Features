package org.tondo.dayseven;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class CircuitTest {
	
	private Map<String, Signal> wires;
	
	private List<Gate> gateRegister;

	@Test
	public void testCircuitAnswer() throws IOException {
		this.gateRegister = new ArrayList<>();
		InputStream is = getClass().getResourceAsStream("/dayseven/circuit.txt");
		assertNotNull(is);
		
		initLogicalCircuit(is);
		int wireAvalue = wire("a").getValue();
		System.out.println("Day 7. First part: wire 'a' value: " + wireAvalue);
		assertEquals("Wire 'a' value: ", 956,  wireAvalue);
		
		this.resetGates();
		TransactionManager.newTx();
		wire("b").setValue(wireAvalue);
		this.resetWires();
		
		wireAvalue = wire("a").getValue();
		System.out.println("Day 7. Second part: wire 'a' value after reinit: " + wireAvalue);
		assertEquals("Wire 'a ' value after reinit: ", 956,  wireAvalue);
	}
	
	@Test
	public void testCircuitSamples() throws IOException {
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
		initLogicalCircuit(is);
		
//		for(Signal s : this.wires.values()) {
//			System.out.println(s);
//		}
		
		assertEquals("Wire d", 72, wire("d").getValue());
		assertEquals("Wire e", 507, wire("e").getValue());
		assertEquals("Wire f", 492, wire("f").getValue());
		assertEquals("Wire g", 114, wire("g").getValue());
		assertEquals("Wire h", 65412, wire("h").getValue());
		assertEquals("Wire i", 65079, wire("i").getValue());
		assertEquals("Wire x", 123, wire("x").getValue());
		assertEquals("Wire y", 456, wire("y").getValue());
		assertEquals("Wire route same as y", 456, wire("route").getValue());
	}
	
	
	@Test
	public void testReinitValue() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("123 -> x").append("\n")
		 .append("456 -> y").append("\n")
		 .append("x AND y -> d").append("\n");
		
		ByteArrayInputStream is = new ByteArrayInputStream(sb.toString().getBytes());
		initLogicalCircuit(is);
		
		for(Signal s : this.wires.values()) {
			System.out.println(s);
		}
		
		assertEquals("Wire x", 123, wire("x").getValue());
		assertEquals("Wire y", 456, wire("y").getValue());
		assertEquals("Wire d", 72, wire("d").getValue());
		
		// reinitialized value
		TransactionManager.newTx();
		wire("y").setValue(5);
		
		for(Signal s : this.wires.values()) {
			System.out.println(s);
		}
	}
	
	@Test
	public void testNOT() {
		TransactionManager.newTx();
		Signal s = new Signal("out");
		assertFalse("Signal is not initialized", s.isInitialized());
		
		Gate bitwiseCompl = Gate.getNOT(s, "doesnt mattaer");
		bitwiseCompl.setInputValue("A", 123);
		assertTrue("Signal has valid data", s.isInitialized());
		assertEquals("Bitwise complement", 65412, s.getValue());
		
		Signal s2 = new Signal("out2");
		Gate bwC2 = Gate.getNOT(s2, "doesnt mattaer");
		bwC2.setInputValue("A", 456);
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
	
	private void initLogicalCircuit(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		String line = null;
		while((line = reader.readLine()) != null) {
			String[] inout = line.split("->");
			if (inout.length < 2) {
				throw new IllegalStateException("Inpput/output badly defined " + line);
			}
			
			TransactionManager.newTx();
		//	System.out.println(line  + " " + TransactionManager.getTxId());
			String outID = inout[1].trim();
			String[] opParts = inout[0].split("\\s+");
			if (opParts.length == 1) {
				// wire is initialized by value
				try {
					wire(outID).setValue(Integer.parseInt(opParts[0].trim()));
				} catch (NumberFormatException e) {
					wire(opParts[0].trim()).addListener(wire(outID));
				}
			
			} else if (opParts.length == 2 && "NOT".equals(opParts[0])) {
				// NOT is single operand
				Gate not = Gate.getNOT(wire(outID), line);
				setGateOperand(not, "A", opParts[1]);
				this.gateRegister.add(not);
			} else if (opParts.length == 3) {
				Gate gate = Gate.getGate(opParts[1], wire(outID), line);
				setGateOperand(gate, "A", opParts[0]);
				setGateOperand(gate, "B", opParts[2]);
				this.gateRegister.add(gate);
			} else {
				throw new IllegalStateException("Inpput/output badly defined " + line);
			}
		}
	}
	
	private void setGateOperand(Gate gate, String opName, String value) {
		String trimmed = value.trim();
		if(trimmed.isEmpty()) {
			throw new IllegalStateException("Operand value cant be empty!");
		}
		
		if (isValue(trimmed)) {
			gate.setInputValue(opName, Integer.parseInt(trimmed));
		} else {
			wire(value).addListener(opName, gate);
		}
	}
	
	private boolean isValue(String strVal) {
		try {
			Integer.parseInt(strVal);
		} catch (NumberFormatException e) {
			return false;
		}
		
		return true;
	}
	
	private void resetGates() {
		for (Gate g : this.gateRegister) {
			g.reset();
		}
	}
	
	private void resetWires() {
		for (Signal s : this.wires.values()) {
			s.setValue(s.getValue());
		}
	}
	
	private Signal wire(String id) {
		if (this.wires == null) {
			this.wires = new HashMap<>();
		}
		
		Signal wire = this.wires.get(id);
		if (wire == null) {
			wire = new Signal(id);
			this.wires.put(id, wire);
		}
		
		return wire;
	}
}
