package org.tondo.Java7Features.visibility;

import static org.junit.Assert.*;

import org.junit.Test;
/**
 * 
 * @author TondoDev
 * 
 * instance and static fields are accessed according to reference variable class.
 * This is different from methods, where actual object instance is used to determine
 * method of which object is called.
 * 
 * SameB extends from SameA
 */
public class VisibilityTest {
	
	@Test
	public void testFieldVisibility() {
		
		assertEquals("Instance field by reference variable", 30, new SomeB().x);
		assertEquals("Static field by reference variable", 40, new SomeB().y);
		
		assertEquals("Instance field by reference variable", 10, ((SomeA)new SomeB()).x);
		assertEquals("Static field by reference variable", 20, ((SomeA)new SomeB()).y);
	}

}
