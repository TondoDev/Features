package org.tondo.Java7Features.forkjoin;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple, maybe bad, object for logging som actions from multiple threads.
 * Access to internal log structure is done by synchronized methods.
 * 
 * @author TondoDev
 *
 */
public class ForkJoinLogs {
	
	public static class FJLogRecord {
		
		private String threadName;
		private int fromBoundary;
		private int toBoundary;
		
		public FJLogRecord(String tn, int fb, int tb) {
			threadName = tn;
			fromBoundary = fb;
			toBoundary = tb;
		}
		
		public String getThreadName() {
			return threadName;
		}
		
		public int getFromBoundary() {
			return fromBoundary;
		}
		
		public int getToBoundary() {
			return toBoundary;
		}
	}

	private List<FJLogRecord> logs;
	
	public ForkJoinLogs() {
		this.logs = new ArrayList<>();
	}
	
	public synchronized void log(String treadIdentification, int from, int to) {
		this.logs.add(new FJLogRecord(treadIdentification, from, to));
	}
	
	public synchronized List<FJLogRecord> getLogs() {
		return new ArrayList<>(this.logs);
	}
}
