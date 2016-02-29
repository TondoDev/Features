package org.tondo.Java7Features;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tondo.testutils.ConsoleOutputCapturer;
import org.tondo.testutils.CorruptedResource;

public class SuppressExceptionsTest {
	
	public static ConsoleOutputCapturer capturer;
	
	@BeforeClass
	public static void init() {
		capturer = ConsoleOutputCapturer.getInstance();
	}
	
	@AfterClass
	public static void cleanup() {
		capturer.stopCapturing();
	}
	
	@Before
	public void initCapture() {
		capturer.capture();
	}
	
	@After
	public  void resetCapture() {
		capturer.stopCapturing();
	}

	
	/**
	 * Just for sure, if we can run properly.
	 */
	@Test
	public void testResourceOk() throws Exception {
		Exception exceptionThrown = null;
		try (CorruptedResource resA = new CorruptedResource("A", false, false, false)) {
			resA.doSomething();
		} catch (Exception e) {
			exceptionThrown = e;
		}
		assertNull(exceptionThrown);
		// and operations executes in this order
		String[] lines = capturer.getLines();
		assertEquals(2, lines.length);
		assertEquals("[A]: logic successful!", lines[0]);
		assertEquals("[A]: close successful!", lines[1]);
	}
	
	
	/**
	 * Failed logic test
	 */
	@Test
	public void testResourceLogicFail() throws Exception {
		Exception exceptionThrown = null;
		try (CorruptedResource resA = new CorruptedResource("A", false, true, false)) {
			resA.doSomething();
		} catch (Exception e) {
			exceptionThrown = e;
		}
		
		assertNotNull(exceptionThrown);
		assertEquals("[A]: logic failed!", exceptionThrown.getMessage());
		assertTrue(exceptionThrown.getSuppressed().length == 0);
		// and operations executes in this order
		String[] lines = capturer.getLines();
		assertEquals(1, lines.length);
		assertEquals("[A]: close successful!", lines[0]);
	}
	
	
	/**
	 * Failed close test
	 */
	@Test
	public void testResourceCloseFail() throws Exception {
		Exception exceptionThrown = null;
		try (CorruptedResource resA = new CorruptedResource("A", false, false, true)) {
			resA.doSomething();
		} catch (Exception e) {
			exceptionThrown = e;
		}
		
		assertNotNull(exceptionThrown);
		assertEquals("[A]: close failed!", exceptionThrown.getMessage());
		assertTrue(exceptionThrown.getSuppressed().length == 0);
		// and operations executes in this order
		String[] lines = capturer.getLines();
		assertEquals(1, lines.length);
		assertEquals("[A]: logic successful!", lines[0]);
	}
	
	
	/**
	 * Failed both logic and close test. Close exception is suppressed by logic exception
	 */
	@Test
	public void testResourceLogicAndCLoseFail() throws Exception {
		Exception exceptionThrown = null;
		try (CorruptedResource resA = new CorruptedResource("A", false, true, true)) {
			resA.doSomething();
		} catch (Exception e) {
			exceptionThrown = e;
		}
		
		assertNotNull(exceptionThrown);
		// close exception is susspressed, so thrown is logic faild exception
		assertEquals("[A]: logic failed!", exceptionThrown.getMessage());
		assertTrue(exceptionThrown.getSuppressed().length == 1);
		// suppressed exptions are stored in Throwable.getSuppressed() array
		assertEquals("[A]: close failed!", exceptionThrown.getSuppressed()[0].getMessage());
		// nothing was executed properly
		String[] lines = capturer.getLines();
		assertEquals(0, lines.length);
	}
	
	
	/**
	 * Test of failing create resource operation. Close method is not called
	 */
	@Test
	public void testResourceCreateFail() throws Exception {
		Exception exceptionThrown = null;
		try (CorruptedResource resA = new CorruptedResource("A", true, false, false)) {
			resA.doSomething();
		} catch (Exception e) {
			exceptionThrown = e;
		}
		
		assertNotNull(exceptionThrown);
		assertEquals("[A]: create failed!", exceptionThrown.getMessage());
		assertTrue(exceptionThrown.getSuppressed().length == 0);
		// nothing was executed properly
		String[] lines = capturer.getLines();
		assertEquals(0, lines.length);
	}
	
	
	/**
	 * Test of failing create resource operation.
	 * Two resources will be created, and second one will fail.
	 * Closing of second resource IS called.
	 */
	@Test
	public void testResourceCreateFailOnSecondResource() throws Exception {
		Exception exceptionThrown = null;
		try (CorruptedResource resA = new CorruptedResource("A", false, false, false);
				CorruptedResource resB = new CorruptedResource("B", true, false, false)) {
			resA.doSomething();
			resB.doSomething();
		} catch (Exception e) {
			exceptionThrown = e;
		}
		
		assertNotNull(exceptionThrown);
		assertEquals("[B]: create failed!", exceptionThrown.getMessage());
		assertTrue(exceptionThrown.getSuppressed().length == 0);
		
		// and operations executes in this order
		String[] lines = capturer.getLines();
		assertEquals(1, lines.length);
		assertEquals("[A]: close successful!", lines[0]);
	}
}
