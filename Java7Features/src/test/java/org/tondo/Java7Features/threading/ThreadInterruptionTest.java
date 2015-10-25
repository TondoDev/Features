package org.tondo.Java7Features.threading;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ThreadInterruptionTest {

	/**
	 *  Investigation of interrupted flag of "other" thread.
	 */
	@Test
	public void testSleptThreadInterruption() throws InterruptedException {
		
		final List<Boolean> flagKeeper = new ArrayList<>();
		
		Thread sleepy = new Thread() {
			
			@Override
			public void run() {
				flagKeeper.add(Thread.currentThread().isInterrupted());
				try {
					System.out.println("Worker is going to sleep");
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					System.out.println("Worker is interrupted");
					flagKeeper.add(Thread.currentThread().isInterrupted());
					Thread.currentThread().interrupt();
					flagKeeper.add(Thread.currentThread().isInterrupted());
				}
			}
		};
		
		
		sleepy.start();
		// give some time to sleepy thread
		Thread.sleep(1000);
		System.out.println("You have enough time to sleep");
		sleepy.interrupt();	
		
		// some time for sleepy termination
		Thread.sleep(500);
		
		// just for sure
		assertEquals(3, flagKeeper.size());
		// first item contains iterrupted status of sleepy thread before 
		// it went sleep
		assertFalse(flagKeeper.get(0));
		// second contains flag just after catching interrupted exception
		// Expected false because interruptedException clears this flag for sleepy thread
		assertFalse(flagKeeper.get(1));
		// it is good practice to mark current thread as interrupted when catching InterruptedException
		// for investigation of thread state from other place.
		// because InterruptedException clears its interruptedFlag
		assertTrue(flagKeeper.get(2));
		
		// ivestigation of terminated thread
		assertFalse(sleepy.isAlive());
		// interrupted flag on non-alive thread is false
		assertFalse(sleepy.isInterrupted());
	}
}
