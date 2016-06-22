package org.tondo.Java7Features;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test of initialization of different variables type
 * @author TondoDev
 *
 */
public class InitializersTest {

	private int instancePrimitive;
	private static int staticPrimitive;
	
	private String instanceRef;
	private static String staticRef;
	
	@Test
	public void testDefaultInitializers() {
		assertEquals(0, instancePrimitive);
		assertEquals(0, staticPrimitive);
		
		assertNull(instanceRef);
		assertNull(staticRef);
		
		// local variables are not initialized during declaration
		// usage of uninitialied variable causes compilation error
		// --------
		// int unitializedPrimitive;
		//System.out.println(unitializedPrimitive); -- compilation error
		// --------
		//String uninitialiedRef;
		//System.out.println(uninitialiedRef); -- compilation error
		
		int localPrimitive;
		localPrimitive = 8; // initialized before first read
		assertEquals(8, localPrimitive);
		
		
	}
	
}
