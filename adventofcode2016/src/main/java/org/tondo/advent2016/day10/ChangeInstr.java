package org.tondo.advent2016.day10;

public class ChangeInstr implements BotInstruction {

	private int botId;
	private int lowId;
	private int highId;
	private char lowType;
	private char highType;
	
	public ChangeInstr(int botId, String lowType, int lowId, String highType, int highId) {
		this.botId = botId;
		this.lowType = "bot".equals(lowType) ? 'B' : 'O';
		this.lowId = lowId;
		
		this.highType = "bot".equals(highType) ? 'B' : 'O';
		this.highId = highId;
	}
	
	@Override
	public boolean execute(BotSwarm swarm) {
		
		Bot bot = swarm.getBot(botId);
		if (!bot.isReady()) {
			return false;
		}
		
		if (!canExecute(swarm)) {
			return false;
		}
		
		int[] values = bot.release();
		
		if (lowType == 'B') {
			Bot lowBot = swarm.getBot(lowId);
			lowBot.setValue(values[0]);
			swarm.examineBotForTermination(lowBot);
		} else {
			swarm.setToOutput(lowId, values[0]);
		}
		
		if (highType == 'B') {
			Bot highBot = swarm.getBot(highId);
			highBot.setValue(values[1]);
			swarm.examineBotForTermination(highBot);
		} else {
			swarm.setToOutput(highId, values[1]);
		}
		
		return true;
	}
	
	private boolean canExecute(BotSwarm swarm) {
		if (lowType == 'B' && swarm.getBot(lowId).isReady()) {
			return false;
		}
		return highType == 'O' || !swarm.getBot(highId).isReady();
	}
}
