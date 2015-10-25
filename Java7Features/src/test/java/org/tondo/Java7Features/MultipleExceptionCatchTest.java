package org.tondo.Java7Features;

import static org.junit.Assert.*;

import org.junit.Test;
import org.tondo.Java7Features.exceptions.IntegerBasedException;
import org.tondo.Java7Features.exceptions.StringBasedException;

public class MultipleExceptionCatchTest {

	/**
	 * TEst for no exception thrown
	 */
	@Test
	public void testMoreCatchBlocksNoExceptionThrown() {
		int decesion = 10;
		int result = 0;
		
		try {
			if (decesion < 0) {
				throw new IntegerBasedException(10);
			} else if (decesion < 10) {
				throw new StringBasedException("Christmas");
			} else {
				result = 5;
			}
		} catch(StringBasedException se) {
			result = 2;
		} catch (IntegerBasedException ie) {
			result = 4;
		}
		assertEquals(5, result);
	}
	
	
	/**
	 * TEst for no exception thrown
	 */
	@Test
	public void testMoreCatchBlocks() {
		int decesion = 8;
		int result = 0;
		
		try {
			if (decesion < 0) {
				throw new IntegerBasedException(10);
			} else if (decesion < 10) {
				throw new StringBasedException("Christmas");
			} else {
				result = 5;
			}
		} catch(StringBasedException se) {
			result = 2;
		} catch (IntegerBasedException ie) {
			result = 4;
		}
		assertEquals(2, result);
	}
	
	
	/**
	 * Test with multi catch block.
	 * Exception object e can access only members (without casting) of common ancestor
	 */
	@Test
	public void testMultipleCatchBlocks() {
		int decesion = -8;
		String result = null;
		
		try {
			if (decesion < 0) {
				throw new IntegerBasedException(10);
			} else if (decesion < 10) {
				throw new StringBasedException("Christmas");
			} else {
				result = "OK";
			}
		
		// in multiple catch block is excetion argument final (can't assign value to it)
		} catch(StringBasedException | IntegerBasedException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMemories());
			result = e.toString();
		} 
		assertEquals("10", result);
	}
}
