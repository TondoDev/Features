package org.tondo.advent2016.day10;

public class InitInstr implements BotInstruction{
	
	private int botId;
	private Integer val;
	
	public InitInstr(int bi, Integer v) {
		this.botId = bi;
		this.val = v;
	}
	
	@Override
	public boolean execute(BotSwarm swarm) {
		Bot bot = swarm.getBot(this.botId);
		
		// can't set when all values are filled
		if (bot.isReady()) {
			return false;
		}
		bot.setValue(this.val);
		swarm.examineBotForTermination(bot);
		return true;
	}

}
