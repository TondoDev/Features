package org.tondo.Java7Features.forkjoin;

import java.util.concurrent.RecursiveTask;

/**
 * Simple fork/join recursive task to compute sum all numbers stored in
 * integer array. It is quite primitive task to do with fork/join, but should
 * demonstrate how divide and counquer is done by this F/J framework.
 * 
 * @author TondoDev
 *
 */
public class FJRecursiveTask extends RecursiveTask<Long>{
	
	private static final long serialVersionUID = 1L;

	private static final int TRESHOLD = 5;
	
	private int[] data;
	private int fromBound;
	private int toBound;
	private ForkJoinLogs logger;

	public FJRecursiveTask(int[] fullData, int from, int to, ForkJoinLogs logging) {
		this.data = fullData;
		this.fromBound = from;
		this.toBound = to;
		this.logger = logging;
	}
	
	@Override
	protected Long compute() {
		long result = 0L;
		// log current computation unit state
		logger.log(Thread.currentThread().getName(), fromBound, toBound);
		
		if ((toBound - fromBound) < TRESHOLD) {
			// linear calculation of sum
			for (int i = fromBound; i < toBound; i++) {
				result += data[i];
			}
		} else {
			// divide task for for smaller pieces
			int middle = (fromBound + toBound) / 2;
			FJRecursiveTask taskLeft = new FJRecursiveTask(data, fromBound, middle, logger);
			FJRecursiveTask taskRight = new FJRecursiveTask(data, middle, toBound, logger);
			// ForkJoinWorkerThreda
			// exeucted divided task (may be executed asynchronously, but it is not rule)
			taskLeft.fork();
			taskRight.fork();
			
			// see also get() (differs in behavior with exceptions)
			result += taskLeft.join() + taskRight.join();
		}
		
		return result;
	}

}
