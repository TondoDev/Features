package org.tondo.sslhandshake;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author TondoDev
 *
 */
public class Loggable {
	
	private List<String> logs;
	
	private static final AtomicInteger counter = new AtomicInteger(1);
	
	protected void log(String source, String msg) {
		String logMSg = counter.getAndIncrement() + ". " + source + " " + msg;
		System.out.println(logMSg);
		getLogsInternal().add(logMSg);
	}
	
	private List<String> getLogsInternal() {
		if (this.logs == null) {
			this.logs = new ArrayList<>();
		}
		return this.logs;
	}
	
	public List<String> getLogs() {
		return this.getLogsInternal();
	}
}
