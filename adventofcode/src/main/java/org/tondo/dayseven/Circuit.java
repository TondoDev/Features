package org.tondo.dayseven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Circuit {

	private Map<String, Signal> wires;
	private List<Gate> gates;
	private Map<String, Signal> constants;
	private int dummyCnt;
	
	public Circuit() {
		this.wires = new HashMap<>();
		this.gates = new ArrayList<>();
		this.constants = new HashMap<>();
		this.dummyCnt = 1;
	}
	
	
	public Signal wire(String id) {
		
		Signal wire = this.constants.get(id);
		if (wire != null) {
			return wire;
		}
		
		wire = this.wires.get(id);
		if (wire == null) {
			wire = new Signal(id);
			this.wires.put(id, wire);
		}
		
		return wire;
	}
	
	public void addConstant(Signal wire) {
		this.wires.remove(wire.getName());
		this.constants.put(wire.getName(), wire);
	}
	
	public void setGateOperand(Gate gate, String opName, String value) {
		String trimmed = value.trim();
		if(trimmed.isEmpty()) {
			throw new IllegalStateException("Operand value cant be empty!");
		}
		
		Signal inputWire = null;
		if (isValue(trimmed)) {
			// dummy wire storing constant
			inputWire = this.wire("#"+(dummyCnt++)+"#");
			inputWire.setValue(Integer.parseInt(trimmed));
			this.addConstant(inputWire);
		} else {
			inputWire = wire(value);
		}
		
		inputWire.addListener(opName, gate);
	}
	
	
	public Gate getGate(String operation, Signal ouput, String gateId) {
		Gate newGate = null;
		switch (operation) {
		case "NOT":
			newGate = Gate.getNOT(ouput, gateId);
			break;
		case "AND":
			newGate = Gate.getAND(ouput, gateId);
			break;
		case "OR" :
			newGate = Gate.getOR(ouput, gateId);
			break;
		case "LSHIFT":
			newGate = Gate.getLSHIFT(ouput, gateId);
			break;
		case "RSHIFT":
			newGate = Gate.getRSHIFT(ouput, gateId);
			break;
		default:
			throw new IllegalArgumentException("Unknow Gate operaion " + operation);
		}
		
		this.gates.add(newGate);
		return newGate;
	}
	
	private boolean isValue(String strVal) {
		try {
			Integer.parseInt(strVal);
		} catch (NumberFormatException e) {
			return false;
		}
		
		return true;
	}
	
	public void enable() {
		for (Gate g : this.gates) {
			TransactionManager.newTx();
			g.setEnabled(true);
		}
		
		for (Signal s : this.constants.values()) {
			TransactionManager.newTx();
			s.setEnabled(true);
		}
		for (Signal s : this.wires.values()) {
			TransactionManager.newTx();
			s.setEnabled(true);
		}
		
	}
	
	public void disable() {
		for (Gate g : this.gates) {
			g.reset();
			g.setEnabled(false);
		}
		
		for (Signal s : this.wires.values()) {
			s.setEnabled(false);
		}
	}
	
	public void printWires() {
		for (Signal s : this.wires.values()) {
			System.out.println(s);
		}
	}
}
