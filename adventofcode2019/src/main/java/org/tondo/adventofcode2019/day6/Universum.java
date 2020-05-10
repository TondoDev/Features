package org.tondo.adventofcode2019.day6;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Universum {

	public static class Orbit {
		private String name;
		private int distance;
		private List<Orbit> orbits;
		private Orbit base;
		private int traveled;
		
		public Orbit(String name) {
			if (name == null) {
				throw new IllegalArgumentException("Name for orbit can't be null");
			}
			this.name = name;
			this.distance = -1;
			this.orbits = new ArrayList<>();
			this.base = null;
			this.traveled = -1;
		}
		
		public String getName() {
			return name;
		}
		
		public int getDistance() {
			return distance;
		}
		
		public void setDistance(int distance) {
			this.distance = distance;
		}
		
		public List<Orbit> getOrbits() {
			return orbits;
		}
		
		public Orbit findOrbit(String name) {
			return this.orbits.stream()
					.filter(o -> o.getName().equals(name))
					.findFirst().orElse(null);
		}
		
		public Orbit getBase() {
			return base;
		}
		
		public void setBase(Orbit base) {
			this.base = base;
		}
		
		public int getTraveled() {
			return traveled;
		}
		
		public void setTraveled(int traveled) {
			this.traveled = traveled;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Orbit)) {
				return false;
			}
			Orbit second = (Orbit) obj;
			return this.name.equals(second.getName());
		}
		
		@Override
		public int hashCode() {
			return this.name.hashCode();
		}
	}
	
	private long orbitsCount = 0L;
	private Orbit com;
	private Map<String, Orbit> register = new HashMap<>();
	
	public void addOribt(String base, String orbit) {
		validateInput(base, orbit);
		
		Orbit baseObject = this.register.get(base);
		if (baseObject == null) {
			baseObject = new Orbit(base);
			this.register.put(base, baseObject);
		}
		
		if (this.com == null && "COM".equals(base)) {
			this.com = baseObject;
			this.com.setDistance(0);
		}
		
		Orbit orbitObject = this.register.get(orbit);
		if (orbitObject == null) {
			orbitObject = new Orbit(orbit);
			this.register.put(orbit, orbitObject);
		}
		
		baseObject.getOrbits().add(orbitObject);
		orbitObject.setBase(baseObject);
		
		determineDistanceOfpath(orbitObject);
	}
	
	public void determineDistanceOfpath(Orbit orbit) {
		if (orbit.getBase().getDistance() < 0) {
			return;
		}
		
		Deque<Orbit> stack = new ArrayDeque<>();
		stack.push(orbit);
		
		
		while(!stack.isEmpty()) {
			Orbit orb = stack.pop();
			int distance = orb.getBase().getDistance();
			
			distance++;
			orb.setDistance(distance);
			this.orbitsCount += distance;
			stack.addAll(orb.getOrbits());
		}
	}
	
	public void validateInput(String base, String orbit) {
		if (base == null || orbit == null) {
			throw new IllegalStateException("Base or orbit can't be null!");
		}
		
		if ("COM".equals(orbit)) {
			throw new IllegalStateException("COM objects can't orbit any object!");
		}
		
		Orbit orbitObject = this.register.get(orbit);
		if (orbitObject != null && orbitObject.getBase() != null && !orbitObject.getBase().getName().equals(base)) {
			throw new IllegalStateException("Object can directly orbit only one object. Orbit '" + orbit + "' already orbits '"+orbitObject.getBase().getName());
		}
		
		Orbit baseObject = this.register.get(base);
		if (baseObject != null && baseObject.findOrbit(orbit) != null) {
			throw new IllegalStateException("Orbit '"+orbit + "' already orbits '" + base +"'.");
		}
	}
	
	
	public long getOrbitsCount() {
		return orbitsCount;
	}
	
	
	public int getDistanceBetween(String orb1, String orb2) {
		Orbit start = determineNode(orb1);
		Orbit target = determineNode(orb2);
		
		Deque<Orbit> stack = new ArrayDeque<>();
		start.setTraveled(0);
		stack.push(start);
		
		while (!stack.isEmpty()) {
			Orbit node = stack.pop();
			if (target.getName().equals(node.getName())) {
				// there is only one path to destination, because there are no loops
				return node.getTraveled();
			}
			
			Orbit base = node.getBase();
			// add base to the stac if not visited
			if (base != null && base.getTraveled() < 0) {
				base.setTraveled(node.getTraveled() + 1);
				stack.push(base);
			}
			
			// add all non visited orbits
			node.getOrbits().stream().filter(o -> o.getTraveled() < 0).forEach(o-> {
				o.setTraveled(node.getTraveled() + 1);
				stack.push(o);
			});
		}
		
		return -1;
	}
	
	private Orbit determineNode(String orb) {
		Orbit myPos = this.register.get(orb);
		if (myPos == null || myPos.getBase() == null) {
			throw new IllegalStateException("Can't find start orbit for '" + orb + "'");
		}
		
		return myPos.getBase();
	}
	
	
}
