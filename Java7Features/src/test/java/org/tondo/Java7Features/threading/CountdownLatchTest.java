package org.tondo.Java7Features.threading;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * Sample usage of CoundownLatch barrier, which is one shot (count can't be reset).
 * @author TondoDev
 *
 */
public class CountdownLatchTest {
	/**
	 * Unit of execution of this test 
	 */
	private static class HardWorker implements Runnable {
		// visitor is assigned to guide
		private CountDownLatch starter;
		// 
		private CountDownLatch finisher;
		
		private boolean interruptedFlag;
		private long workTime;
		private AtomicInteger sharedValue;
		
		public HardWorker(CountDownLatch start, CountDownLatch finish, long workingTime, AtomicInteger value) {
			this.interruptedFlag = false;
			this.workTime = workingTime;
			this.starter = start;
			this.finisher = finish;
			this.sharedValue = value;
		}

		public boolean isInterruptedFlag() {
			return interruptedFlag;
		}
		
		@Override
		public void run() {
			try {
				// waiting for enabled execution
				starter.await();
				// emulation of some useful computation
				Thread.sleep(this.workTime);
				// increase finished workers counter, which is shared
				this.sharedValue.incrementAndGet();
				// decrease countdown counter to signal its work is done
				this.finisher.countDown();
			} catch (InterruptedException e) {
				this.interruptedFlag = true;
				Thread.currentThread().interrupt();
			}
		}
	}
	
	@Test
	public void testCountdownLatchSignaling() throws InterruptedException {
		final int WORKERS_COUNT = 7;
		
		// signal from main thread to workers used for starting work of all threads after all are created
		// 1 is used becase we want enable all threads with single operation, so countDown()
		// will be called only once
		CountDownLatch starter = new CountDownLatch(1);
		// signal from worker to main
		CountDownLatch finisher = new CountDownLatch(WORKERS_COUNT);
		AtomicInteger finishedCounter = new AtomicInteger();
		
		// for generating random working times for each worker
		Random randomizer = new Random();
		
		List<HardWorker> wirkers = new ArrayList<>();
		for (int c = 0; c < WORKERS_COUNT; c++) {
			HardWorker w = new HardWorker(starter, finisher, randomizer.nextInt(2000) + 1000, finishedCounter);
			wirkers.add(w);
			new Thread(w).start();
		}
		
		// after all threads are prepared, allow them execute their work
		// thsi call decrease starter's counter from 1 to zero, which wake up
		// all worker threads from .await() call.
		starter.countDown();
		
		// blocks main thread till all workers call countDown().
		// counter of finisher will be decreased from WORKERS_COUNT to 0
		finisher.await();
		
		assertEquals(WORKERS_COUNT, finishedCounter.intValue());
		
		// this should return immediately
		finisher.await();
	}
	
	@Test
	public void testInterruptingAwaiting() throws InterruptedException {
		CountDownLatch starter = new CountDownLatch(1);
		// signal from worker to main
		CountDownLatch finisher = new CountDownLatch(1);
		AtomicInteger dummy = new AtomicInteger();
		
		HardWorker worker = new HardWorker(starter, finisher, 5000, dummy);
		Thread workerThread = new Thread(worker);
		workerThread.start();
		
		// should be enough time for worker to call await()
		Thread.sleep(700);
		// interrupt thread while is in await()
		workerThread.interrupt();
		Thread.sleep(800);
		workerThread.interrupt();
		
		// worker thread didn't increment its shared baluce because it was itnerrupted before
		assertEquals(0, dummy.intValue());
		// custom HardWorker interruption flag (not Thread interrupted flag)
		assertTrue(worker.isInterruptedFlag());
	}

}
