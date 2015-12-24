package org.tondo.dayfourteen;

public class Reindeer {

	// in km/s
	private int topSpeed;
	private int flyTime;
	private int restTime;
	private String name;
	
	// internal state
	private int distance;
	private boolean flying;
	private int streak;
	private int points;
	
	public Reindeer(String name, int speed, int flyTime, int restTime) {
		this.topSpeed = speed;
		this.restTime = restTime;
		this.flyTime = flyTime;
		this.name = name;
		
		// state vars
		this.reset();
	}
	
	
	public int getDistanceAfterTime(int time) {
		int remainingTime = time;
		int distance = 0;
		boolean flying = true;
		
		while(remainingTime > 0) {
			if (flying) {
				int effective = remainingTime > flyTime ? flyTime : remainingTime;
				distance += effective*topSpeed;
				remainingTime -= effective;
			} else {
				remainingTime -= this.restTime;
			}
			
			flying = !flying;
		}
		return distance;
	}
	
	
	public void incrementTime() {
		if(this.flying) {
			this.distance += this.topSpeed;
		}
		
		this.streak++;
		
		if (this.flying ? streak == this.flyTime : streak == this.restTime) {
			this.streak = 0;
			this.flying = !this.flying;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isFlying() {
		return flying;
	}
	
	public int getDistance() {
		return distance;
	}
	
	public int getPoints() {
		return points;
	}
	
	public void incrementPoints() {
		this.points++;
	}
	
	public void reset() {
		this.distance = 0;
		this.flying = true;
		this.streak = 0;
		this.points = 0;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[" + this.name + ", Distance: " + this.distance + ", Pts: " + this.points + "]";
	}
}
