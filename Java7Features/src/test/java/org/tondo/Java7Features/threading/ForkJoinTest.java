package org.tondo.Java7Features.threading;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

import static org.junit.Assert.*;

import org.junit.Test;
import org.tondo.Java7Features.forkjoin.FJRecursiveTask;
import org.tondo.Java7Features.forkjoin.ForkJoinLogs;
import org.tondo.Java7Features.forkjoin.ForkJoinLogs.FJLogRecord;

/**
 * Very simple demonstration of Fork/Join usage.
 * More things should be clear when Java concurrency will be studied in more detail.
 * TODO: Executors, ExecutorService...
 * @author TondoDev
 *
 */
public class ForkJoinTest {
	
	private static final int DATA_SIZE = 100;
	
	@Test
	public void testDivideAndConquer() {
		// for logging intermediate results of each worker
		ForkJoinLogs logger = new ForkJoinLogs();
		int[] data = prepareData();
		
		ForkJoinPool fjPool = new ForkJoinPool();
		// blocking
		long result = fjPool.invoke(new FJRecursiveTask(data, 0, data.length, logger));
		
		// formula for sum all numbers between 1 and n
		long expected = DATA_SIZE*(DATA_SIZE + 1)/2;
		assertEquals(expected, result);
		
		List<FJLogRecord> logRecods = logger.getLogs();
		// expected 63 tasks were generated
		assertEquals(63, logRecods.size());
		
		Set<String> threadNameSet = new HashSet<>();
		for (FJLogRecord record : logRecods) {
			threadNameSet.add(record.getThreadName());
		}
		
		// assumption that count of participated threads is lower than number of fork/join tasks
		// for less tasks this is not necessary true
		assertTrue(logRecods.size() > threadNameSet.size());
		//
		assertEquals(fjPool.getPoolSize(), threadNameSet.size());
		
		// TODO what is getParallelism()?
		// System.out.println("PARA: " + fjPool.getParallelism());
		
	}
	
	
	private int[] prepareData() {
		
		int[] retval = new int[DATA_SIZE];
		for (int i = 0; i < DATA_SIZE; i++) {
			retval[i] = i + 1;
		}
		
		return retval;
		
	}

}
