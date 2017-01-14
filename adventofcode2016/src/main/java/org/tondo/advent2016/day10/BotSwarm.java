package org.tondo.advent2016.day10;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotSwarm {
	private static final Pattern INPUT_VAL = Pattern.compile("^value ([0-9]+) goes to bot ([0-9]+)$");
	private static final Pattern CHANGE = Pattern.compile("^bot ([0-9]+) gives low to (bot|output) ([0-9]+) and high to (bot|output) ([0-9]+)$");

	private Map<Integer, Bot> botRegister;
	private Map<Integer, Integer> outputBins;
	private Integer terminatingBotId;
	private LinkedList<BotInstruction> instructionQeue;
	
	public BotSwarm() {
		this.botRegister = new HashMap<>();
		this.outputBins = new HashMap<>();
		this.instructionQeue = new LinkedList<>();
	}
	
	public BotInstruction decode(String instr) {
		Matcher input = INPUT_VAL.matcher(instr);
		if (input.find()) {
			return new InitInstr(
					Integer.parseInt(input.group(2)), 
					Integer.parseInt(input.group(1)));
			
		}
		
		Matcher change = CHANGE.matcher(instr);
		if (change.find()) {
			return new ChangeInstr(
				Integer.parseInt(change.group(1)), // srcBot
				change.group(2),  // lowType
				Integer.parseInt(change.group(3)),  // lowId
				change.group(4),  // highType
				Integer.parseInt(change.group(5))); // highId
		}
		
		throw new IllegalArgumentException("Invalid instruction: " + instr);
	}
	
	public void processInstruction(String encodedInstruction) {
		BotInstruction instr = decode(encodedInstruction);
		
		if (instr.execute(this)) {
			processQeue();
		} else {
			this.instructionQeue.add(instr);
		}
	}
	
	private void processQeue() {
		boolean changeFlag = false;
		do {
			changeFlag = false;
			Iterator<BotInstruction> iter = this.instructionQeue.iterator();
			while (iter.hasNext()) {
				BotInstruction bi = iter.next();
				
				if (bi.execute(this)) {
					changeFlag = true;
					iter.remove();
					break;
				}
			}
		} while(changeFlag);
	}
	
	public Bot getBot(int botId) {
		Bot rv = this.botRegister.get(botId);
		if (rv == null) {
			rv = new Bot(botId);
			this.botRegister.put(botId, rv);
		}
		return rv;
	}
	
	public void setToOutput(int outputId, Integer value) {
		this.outputBins.put(outputId, value);
	}
	
	public Integer getTerminatingBotId() {
		return terminatingBotId;
	}
	
	public int getMultipliedOutput(int maxBin) {
		
		int rv = 1;
		for (int i = 0; i <= maxBin; i++) {
			rv *= this.outputBins.get(i);
		}
		
		return rv;
	}
	
	public void printInfo() {
		System.out.println("Instructions in queue: " + this.instructionQeue.size());
		System.out.println("Number of bots: " + this.botRegister.values());
		System.out.println("Used out bins: " + this.outputBins);
	}
	public void examineBotForTermination(Bot bot) {
		if (terminatingBotId == null) {
			if (bot.isReady() && bot.getLow() == 17 && bot.getHigh() == 61) {
				this.terminatingBotId = bot.getId();
			}
		}
	}
}
