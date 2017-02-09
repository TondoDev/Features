package org.tondo.advent2016.day11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author TondoDev
 *
 */
public class ConfigurationReader {

	private static final Pattern FLOOR_PARSER = Pattern.compile("^The ([a-z]+) floor contains (.*)$");
	private static final Pattern DEVICE_PARSER = Pattern.compile("([a-z]+)(?:-compatible)? (microchip|generator)");
	
	private static final Map<String, Integer> FLOOR_NAME;
	static {
		Map<String, Integer> tmp = new HashMap<>();
		tmp.put("first", 1);
		tmp.put("second", 2);
		tmp.put("third", 3);
		tmp.put("fourth", 4);
		FLOOR_NAME = Collections.unmodifiableMap(tmp);
	}
	
	private Map<String, Integer> elementMap = new HashMap<>();
	private Map<Integer, List<String>> floors = new HashMap<>();
	private int elementIdGen = 1;
	
	public void readConfiguration(BufferedReader reader) throws IOException {
		String line = null;
		while ((line = reader.readLine()) != null) {
			Matcher floorM = FLOOR_PARSER.matcher(line);
			if (!floorM.find()) {
				throw new IllegalArgumentException("Incorrect imput! " + line);
			}
			
			Integer floorNum = FLOOR_NAME.get(floorM.group(1));			
			if (floorNum == null) {
				throw  new IllegalArgumentException("Incorrect floor name '" + floorM.group(1) + "'");
			}
			
			Matcher deviceM = DEVICE_PARSER.matcher(floorM.group(2));
			while (deviceM.find()) {
				String elem = deviceM.group(1);
				String device = deviceM.group(2);
				Integer elementId = this.elementMap.get(elem);
				if (elementId == null) {
					this.elementMap.put(elem, this.elementIdGen);
					elementId = this.elementIdGen++;
				}
				
				List<String> floor = this.floors.get(floorNum);
				if (floor == null) {
					floor = new ArrayList<>();
					this.floors.put(floorNum, floor);
				}
				
				floor.add(elementId.toString() + device.substring(0, 1));
			}
		}
	}
	
	public FloorState getInitialState() {
		// ensure that empty floors contains empty list, not null
		for (int i = 1; i <= 4; i++) {
			if(this.floors.get(i) == null) {
				this.floors.put(i, Collections.<String>emptyList());
			}
		}
		return new FloorState(this.floors, 1);
	}
	
	public FloorState getEndState() {
		Map<Integer, List<String>> endState = new HashMap<>();
		List<String> finalFloor = new ArrayList<>();
		
		List<Integer> elementIds = new ArrayList<>(elementMap.values());
		Collections.sort(elementIds);
		for (Integer eid : elementIds) {
			finalFloor.add(eid.toString()+"g");
			finalFloor.add(eid.toString()+"m");
		}
		
		endState.put(4, finalFloor);
		return new FloorState(endState, 4);
	}
	
	
}
