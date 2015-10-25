package org.tondo.Java7Features.io;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author TondoDev
 *
 */
public class BufferTest {

	/**
	 * Behavior of Buffer classes and changes of position, limit and capacity
	 * properties after operations.
	 * 
	 * invariant holds: 0 <= mark <= position <= limit <= capacity
	 */
	@Test
	public void testBufferProperties() {
		int capacity = 50;
		ByteBuffer buff = ByteBuffer.allocate(capacity);
		// initial state just after creation
		assertEquals(capacity, buff.capacity());
		assertEquals(capacity, buff.limit());
		assertEquals(0, buff.position());
		
		// put one byte
		buff.put((byte) 10);
		assertEquals(capacity, buff.capacity());
		assertEquals(capacity, buff.limit());
		// position is moved
		assertEquals(1, buff.position());
		
		// buffer state[10,0,0...]
		//                 |
		//            Current position
		assertEquals(0,buff.get());
		
		// rewind position to zero
		buff.rewind();
		assertEquals(10,buff.get());
		// relative get increments position by one
		// so it is zero again
		assertEquals(0,buff.get());
		
		// clear does not crlears data bug sets
		// limit, position to default state and remove mark
		buff.clear();
		assertEquals(capacity, buff.capacity());
		assertEquals(capacity, buff.limit());
		assertEquals(0, buff.position());
		assertEquals(10,buff.get());
		
		// set limit to current position and position set to 0
		// usable in channel write operations (means reading from buffer, write to channel)
		// limit denotes position after which should not be readed
		assertEquals(1, buff.position());
		buff.flip(); // limit = 1
		assertEquals(0, buff.position());
		assertEquals(1, buff.limit());
		
		assertEquals(10, buff.get());
		
		// when position == limit, and subsequently get() method is called
		// underwlow exception is thrown
		assertEquals(buff.limit(), buff.position());
		try {
			buff.get();
			fail("BufferUnderflowException expected!");
		} catch (BufferUnderflowException e) {}
		
		
		buff.clear();
		buff.put((byte) 10);
		buff.put((byte) 20);
		buff.put((byte) 30);
		// this set mark at current position
		buff.mark();
		assertEquals(3, buff.position());
		
		buff.put((byte) 40);
		buff.put((byte) 50);
		assertEquals(5, buff.position());
		
		// set position to specified mark
		buff.reset();
		assertEquals(3, buff.position());
		
		// clear also removed mark - mark is undefined
		buff.clear();
		
		// calling reset() on buffer with undefined mark, exception is thrown
		try {
			buff.reset();
			fail("InvalidMarkException expected!");
		} catch (InvalidMarkException e) {}
		
	}
}
