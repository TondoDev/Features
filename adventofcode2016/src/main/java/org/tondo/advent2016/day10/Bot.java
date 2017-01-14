package org.tondo.advent2016.day10;

/**
 * 
 * @author TondoDev
 *
 */
public class Bot {

	private int id;
	private Integer low;
	private Integer high;
	
	public Bot(int id) {
		this.id = id;
	}
	
	public Integer getLow() {
		return low;
	}
	
	public Integer getHigh() {
		return high;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isReady() {
		return this.low != null && this.high != null;
	}
	
	public int[] release() {
		int[] rv = new int[] {this.low, this.high};
		this.low = null;
		this.high = null;
		return rv;
	}
	
	public void setValue(int val) {
		if (this.low == null) {
			this.low = val;
		} else if (this.low < val) {
				this.high = val;
		} else {
			this.high = this.low;
			this.low = val;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ Bot: ").append(this.id).append(", ")
			.append("low: ").append(this.low).append(", ")
			.append("high: ").append(this.high).append("]");
			
		
		return sb.toString();
	}
}
