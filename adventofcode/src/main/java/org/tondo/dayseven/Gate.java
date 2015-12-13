package org.tondo.dayseven;

public abstract class Gate {

	private Signal output;
	private int aInput;
	private int bInput;
	
	protected boolean aValid;
	protected boolean bValid;
	private boolean enabled;
	private String name;
	
	private int transaction = -1;
	
	public Gate(Signal outputWire) {
		this(outputWire, "");
		if(outputWire.isInitialized()) {
			throw new IllegalArgumentException("Signal " + outputWire.getName() + " is already initialized!");
		}
		this.output = outputWire;
	}
	
	public Gate(Signal outputWire, String name) {
		if(outputWire.isInitialized()) {
			throw new IllegalArgumentException("Signal " + outputWire.getName() + " is already initialized!");
		}
		this.output = outputWire;
		this.name = name;
		this.enabled = false;
		this.aValid = false;
		this.bValid = false;
	}
	
	
	public boolean inputsValids() {
		return this.aValid && this.bValid;
	}
	
	public void setInputValue(String inputName, int value) {
		if ("A".equals(inputName)) {
			this.aInput = value;
			this.aValid = true;
		} else if ("B".equals(inputName)) {
			this.bInput = value;
			this.bValid = true;
		} else {
			throw new IllegalArgumentException(" Only A and B inputs are available");
		}
		this.generateOutput(TransactionManager.getTxId());
	}
	
	public abstract int computeValue(int a, int b);
	
	private void generateOutput(int transaction) {
		if (isEnabled() && inputsValids()) {
			if (this.transaction == transaction) {
				throw new IllegalStateException("Components loop detected! (transction: " + transaction + ")");
			}
			
			int value = computeValue(aInput, bInput) & 0xFFFF;
			output.setValue(value);
			this.transaction = transaction;
		}
	}
	
	public void reset() {
		this.aValid = false;
		this.bValid = false;
	}
	
	public void setEnabled(boolean flag) {
		if (this.enabled == flag) {
			return;
		}
		this.enabled = flag;
		
		if(this.enabled) {
			generateOutput(TransactionManager.getTxId());
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	
	public static Gate getNOT(Signal output, String name) {
		return new Gate(output, name) {
			
			@Override
			public boolean inputsValids() {
				return this.aValid;
			}
			
			@Override
			public int computeValue(int a, int b) {
				return ~a;
			}
		};
	}
	
	public static Gate getLSHIFT(Signal output, String name) {
		return new Gate(output, name) {
			
			@Override
			public int computeValue(int a, int b) {
				return a << b;
			}
		};
	}
	
	public static Gate getRSHIFT(Signal output, String name) {
		return new Gate(output, name) {
			
			@Override
			public int computeValue(int a, int b) {
				return a >> b;
			}
		};
	}
	
	public static Gate getAND(Signal output, String name) {
		return new Gate(output, name) {
			
			@Override
			public int computeValue(int a, int b) {
				return a & b;
			}
		};
	}
	
	public static Gate getOR(Signal output, String name) {
		return new Gate(output, name) {
			@Override
			public int computeValue(int a, int b) {
				return a | b;
			}
		};
	}
	
	@Override
	public String toString() {
		return this.name.isEmpty() ? "(Gate name empty)" : this.name;
	}
}
