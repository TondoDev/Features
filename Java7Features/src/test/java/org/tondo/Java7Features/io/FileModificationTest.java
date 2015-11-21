package org.tondo.Java7Features.io;

import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;
import org.tondo.Java7Features.JavaFeaturesTestBase;

/**
 * Experiments with overwriting, shrinking, resizing, inserting data into file...
 * @author TondoDev
 *
 */
public class FileModificationTest  extends JavaFeaturesTestBase {
	
	private static final int ORIG_SAMPLE_SIZE = 20;
	private static final int ORIG_PATTERN = 5;

	/**
	 * Just for check my helper method
	 * @throws IOException
	 */
	@Test
	public void testFillFile() throws IOException {
		Path outFile = inTempDir("out.tmp");
		getFileKeeper().markForWatch(outFile);
		
		int pattern = 5;
		int len = 20;
		genBinaryFile(pattern, len, outFile);
		
		byte[] restoredData = Files.readAllBytes(outFile);
		assertEquals("Lenght of resotred data", len, restoredData.length);
		
		for (int i = 0; i < len; i++) {
			assertEquals("Data value in index " + i, pattern, (int)restoredData[i]);
		}
	}
	
	/**
	 * File is overwritten 
	 * @throws IOException
	 */
	@Test
	public void testWritingToFileOutputStream() throws IOException {
		Path outFile = testProlog();
		
		try (OutputStream os = new FileOutputStream(outFile.toFile())) {
			os.write(0);
			os.write(0);
			os.write(0);
		}
		byte[] restoredData = Files.readAllBytes(outFile);
		// file is overwritten by default and completely replaced
		assertEquals("Lenght of resotred data", 3, restoredData.length);
		
		// oerwritten first three bytes
		assertEquals(restoredData[0], 0);
		assertEquals(restoredData[1], 0);
		assertEquals(restoredData[2], 0);
	}
	
	@Test
	public void testAppendByStream() throws IOException {
		Path outFile = testProlog();
		
		try (OutputStream os = new FileOutputStream(outFile.toFile(), true)) {
			os.write(0);
			os.write(0);
			os.write(0);
		}
		byte[] restoredData = Files.readAllBytes(outFile);
		// file size increased
		assertEquals("Lenght of resotred data", ORIG_SAMPLE_SIZE + 3, restoredData.length);
		
		// appended last three bytes
		assertEquals(restoredData[20], 0);
		assertEquals(restoredData[21], 0);
		assertEquals(restoredData[22], 0);
		
		// rest should be filled by original value
		for (int i= 0; i <ORIG_SAMPLE_SIZE; i++ ) {
			assertEquals("Data value in index " + i, ORIG_PATTERN, (int)restoredData[i]);
		}
	}
	
	@Test
	public void testOverwritinRandomAccessFile() throws IOException {
		Path outFile = testProlog();
		
		// just for example that only write mode doesn't exists
		try {
			RandomAccessFile rac = new RandomAccessFile(outFile.toFile(), "w");
			fail("IllegalArgumentException expected, due to bad mode");
		} catch(IllegalArgumentException e) {}
		
		
		try (RandomAccessFile rac = new RandomAccessFile(outFile.toFile(), "rw")) {
			assertEquals("Current pointer is on the beginning", 0, rac.getFilePointer());
			assertEquals("Size of file", ORIG_SAMPLE_SIZE, rac.length());
			// overwriting first three bytes
			rac.write(0);
			rac.write(0);
			rac.write(0);
		}
	
		byte[] restoredData = Files.readAllBytes(outFile);
		// file size is same
		assertEquals("Lenght of resotred data", ORIG_SAMPLE_SIZE, restoredData.length);
		// first bytes are overwriten
		assertEquals(restoredData[0], 0);
		assertEquals(restoredData[1], 0);
		assertEquals(restoredData[2], 0);
		
		// remaining is still the same
		for (int i = 3; i < ORIG_SAMPLE_SIZE; i++ ) {
			assertEquals("Data value in index " + i, ORIG_PATTERN, (int)restoredData[i]);
		}
	}
	
	/**
	 *	 We can't create FileChannel directly, but we can get it from RandomAccessFile or
	 *	 FileInput/Output stream
	 */
	@Test
	public void  testOverwritingByFileChannel() throws IOException {
		Path outFile = testProlog();
		try (RandomAccessFile rac = new RandomAccessFile(outFile.toFile(), "rw")) {
			FileChannel channel = rac.getChannel();
			assertEquals("Current pointer is on the beginning", 0, channel.position());
			assertEquals("Size of file", ORIG_SAMPLE_SIZE, channel.size());
		
			// Channels works with buffers
			// no flip is needed, because it limit and capacaity is set to length and position is 0
			ByteBuffer buffer = ByteBuffer.wrap(new byte[] {0,0,0});
			
			// overwriting first three bytes
			channel.write(buffer);
			
			assertEquals("Source RAF sharing file pointer", rac.getFilePointer(), channel.position());
		}
		
		// same as with random access file example
		byte[] restoredData = Files.readAllBytes(outFile);
		// file size is same
		assertEquals("Lenght of resotred data", ORIG_SAMPLE_SIZE, restoredData.length);
		// first bytes are overwriten
		assertEquals(restoredData[0], 0);
		assertEquals(restoredData[1], 0);
		assertEquals(restoredData[2], 0);
		
		// remaining is still the same
		for (int i = 3; i < ORIG_SAMPLE_SIZE; i++ ) {
			assertEquals("Data value in index " + i, ORIG_PATTERN, (int)restoredData[i]);
		}
	}
	
	@Test
	public void testShringAndEnlagreByRAF() throws IOException {
		Path outFile = testProlog();
		final int SHRINK = 10;
		// at first shrinking
		try (RandomAccessFile raf = new RandomAccessFile(outFile.toFile(), "rw")) {
			raf.setLength(SHRINK);
		}
		
		byte[] restoredData = Files.readAllBytes(outFile);
		assertEquals("Lenght of shrinked resotred data", SHRINK, restoredData.length);
		// remaining is still the same
		for (int i = 0; i < SHRINK; i++ ) {
			assertEquals("Data value in index " + i, ORIG_PATTERN, (int)restoredData[i]);
		}
		
		// OK, now increase size
		try (RandomAccessFile raf = new RandomAccessFile(outFile.toFile(), "rw")) {
			raf.setLength(ORIG_SAMPLE_SIZE);
		}
		restoredData = Files.readAllBytes(outFile);
		assertEquals("Lenght of incresed resotred data", ORIG_SAMPLE_SIZE, restoredData.length);
		
		// first half is original
		for (int i = 0; i < SHRINK; i++ ) {
			assertEquals("Data value in index " + i, ORIG_PATTERN, (int)restoredData[i]);
		}
		// area after original file size is filled with zeros
		for (int i = 10; i < ORIG_SAMPLE_SIZE; i++ ) {
			assertEquals("Data value in index " + i, 0, (int)restoredData[i]);
		}
	}
	
	@Test
	public void testInsertInTheMiddleOfFileRAF() throws IOException {
		Path outFile = inTempDir("out.tmp");
		getFileKeeper().markForWatch(outFile);
		
		try (OutputStream os = new FileOutputStream(outFile.toFile())) {
			for (int i = 0; i < ORIG_SAMPLE_SIZE; i++) {
				os.write(i);
			}
		}
		
		try (RandomAccessFile raf = new RandomAccessFile(outFile.toFile(), "rw")) {
			// buffer is small for demonstration of moving blocks
			int moveBy = 6;
			int moveStart = 5;
			this.makeSpace(raf, moveStart, moveBy, 7);
			raf.seek(moveStart);
			for (int i =0; i < moveBy; i++) {
				raf.write(99);
			}
		}
		
		byte[] restoredData = Files.readAllBytes(outFile);
		for (int i = 0; i < restoredData.length; i++) {
			if (i < 10) {
				System.out.print(" ");
			}
			System.out.print(restoredData[i]);
			if (i != 0 && (i % 10) == 0) {
				System.out.println();
			} else {
				System.out.print(" ");
			}
		}
	}
	
	private void makeSpace(RandomAccessFile raf, long fromIndex, long moveBy, int buffSize) throws IOException {
		byte[] moveBuffer = new byte[buffSize];
		
		long currCopy = raf.length() ;
		while ((currCopy-buffSize) >= fromIndex) {
			currCopy-=buffSize;
			raf.seek(currCopy);
			int read = raf.read(moveBuffer, 0, buffSize);
			raf.seek(currCopy + moveBy);
			raf.write(moveBuffer, 0, read);
		}
		// correction when reminder is lower than buffer size
		if (fromIndex < currCopy) {
			raf.seek(fromIndex);
			int read = raf.read(moveBuffer, 0, (int) ((currCopy - fromIndex) + 1));
			raf.seek(fromIndex + moveBy);
			raf.write(moveBuffer, 0, read);
		}
		
//		if (currCopy >= mo)
	}
	
	
	
	private Path testProlog() throws IOException {
		Path outFile = inTempDir("out.tmp");
		getFileKeeper().markForWatch(outFile);
		
		int pattern = ORIG_PATTERN;
		int len = ORIG_SAMPLE_SIZE;
		genBinaryFile(pattern, len, outFile);
		return outFile;
	}
	
	/**
	 * Generate binary file with {@code value} pattern repeated {@code howMuch} times
	 * @param value
	 * 	value which will be repeated into file
	 * @param howMuch
	 * 	indicates how much times should be value repeated
	 * @param outFile
	 * 	output file where data will be written, file will be created when dosn't exists
	 * @throws IOException
	 */
	private void genBinaryFile(int value, int howMuch, Path outFile) throws IOException {
		if (howMuch > 100 || howMuch < 0) {
			throw new IllegalArgumentException("Only size between 0 and 100 bytes are allowed for these tests");
		}
		
		byte[] data = new byte[howMuch];
		for(int i =0; i < howMuch; i++) {
			data[i] = (byte)value;
		}
		
		Files.write(outFile, data);
	}
}
