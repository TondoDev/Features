package org.tondo.dayseven;

import java.util.ArrayList;
import java.util.List;

public class Signal {
	
	private static class GateListener {
		String gateParam;
		Gate gate;
		
		public GateListener(String gp, Gate g) {
			this.gateParam = gp;
			this.gate = g;
		}
	}

	private String name;
	private int value;
	private boolean initialized;
	private List<GateListener> gateListeners;
	private List<Signal> signalListeners; // forgotten situaltion about connection signal directly to other signal
	private int transaction = -1;
	
	public Signal(String name) {
		this.name = name;
		this.initialized = false;
		this.gateListeners = new ArrayList<>();
		this.signalListeners = new ArrayList<>();
	}
	
	public void setValue(int value) {
		this.value = value;
		this.initialized = true;
		
		if (this.transaction == TransactionManager.getTxId()) {
			throw new IllegalStateException("Components loop detected! (transction: " + transaction + ")");
		}
		
		for (GateListener gl : this.gateListeners) {
			gl.gate.setInputValue(gl.gateParam, value);
		}
		
		for (Signal s : this.signalListeners) {
			s.setValue(value);
		}
		
		this.transaction = TransactionManager.getTxId();
	}
		
	public int getValue() {
		if (!initialized) {
			throw new IllegalStateException("Signal " + this.name + " is not initialized!");
		}
		return value;
	}
	
	public void addListener(String gateParam, Gate gate) {
		if (this.isInitialized()) {
			gate.setInputValue(gateParam, this.getValue());
		}
		this.gateListeners.add(new GateListener(gateParam, gate));
	}
	
	public void addListener(Signal signal) {
		if (this.isInitialized()) {
			signal.setValue(this.getValue());
		}
		
		this.signalListeners.add(signal);
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return this.name + ": " + (this.initialized ? (this.value + " (tx: "+this.transaction+")") : "(not initialized)");
	}
}
