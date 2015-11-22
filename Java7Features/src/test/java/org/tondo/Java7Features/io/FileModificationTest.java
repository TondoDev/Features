package org.tondo.Java7Features.io;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
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
	private static final int INSERT_PATTERN = 99;

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
		try (RandomAccessFile rac = new RandomAccessFile(outFile.toFile(), "w")){
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
	public void testShrinkAndEnlagreByRAF() throws IOException {
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
		int moveBy = 6;
		int moveStart = 5;
		try (RandomAccessFile raf = new RandomAccessFile(outFile.toFile(), "rw")) {
			// buffer is small for demonstration of moving blocks
			this.makeSpace(raf, moveStart, moveBy, 7);
			raf.seek(moveStart);
			for (int i = 0; i < moveBy; i++) {
				raf.write(99);
			}
		}
		
		
		byte[] restoredData = Files.readAllBytes(outFile);
		// first bytes unchanged
		for (int i =0; i < moveStart; i++) {
			assertEquals("index " + i, i, restoredData[i]);
		}
		
		// inserted data
		for (int i =moveStart; i < moveBy; i++) {
			assertEquals("index " + i, INSERT_PATTERN, restoredData[i]);
		}
		
		// inserted data
		for (int i =moveStart + moveBy; i < ORIG_SAMPLE_SIZE; i++) {
			assertEquals("index " + i, i-moveBy, restoredData[i]);
		}
	}
	
	@Test
	public void testInsertDataIntoFileMoreCases() throws IOException {
		Path outFile = testProlog();
		int fromIndex, movedBy, bufferSize;
		byte[] restored = null;
		
		fromIndex = 0;
		movedBy = 20;
		bufferSize = 20;
		insertData(outFile, fromIndex, movedBy, bufferSize);
		restored = Files.readAllBytes(outFile);
		assertRestoredDataAfterInsert(restored, fromIndex, movedBy);
		
		fromIndex = 0;
		movedBy = 20;
		bufferSize = 1;
		outFile = testProlog();
		insertData(outFile, fromIndex, movedBy, bufferSize);
		restored = Files.readAllBytes(outFile);
		assertRestoredDataAfterInsert(restored, fromIndex, movedBy);
		
		fromIndex = 0;
		movedBy = 1;
		bufferSize = 10;
		outFile = testProlog();
		insertData(outFile, fromIndex, movedBy, bufferSize);
		restored = Files.readAllBytes(outFile);
		assertRestoredDataAfterInsert(restored, fromIndex, movedBy);
		
		fromIndex = 19;
		movedBy = 5;
		bufferSize = 10;
		outFile = testProlog();
		insertData(outFile, fromIndex, movedBy, bufferSize);
		restored = Files.readAllBytes(outFile);
		assertRestoredDataAfterInsert(restored, fromIndex, movedBy);
		
		fromIndex = 20;
		movedBy = 5;
		bufferSize = 7;
		outFile = testProlog();
		insertData(outFile, fromIndex, movedBy, bufferSize);
		restored = Files.readAllBytes(outFile);
		assertRestoredDataAfterInsert(restored, fromIndex, movedBy);
		
		// moving from far after file length
		fromIndex = 30;
		movedBy = 5;
		bufferSize = 7;
		outFile = testProlog();
		insertData(outFile, fromIndex, movedBy, bufferSize);
		restored = Files.readAllBytes(outFile);
		assertEquals("File size", 35, restored.length);
		// original data remains
		for (int i = 0; i < ORIG_SAMPLE_SIZE; i++) {
			assertEquals("index " + i, ORIG_PATTERN, restored[i]);
		}
		
		// following zeroed bytes created by RAF
		for (int i = ORIG_SAMPLE_SIZE; i <  30; i++) {
			assertEquals("index " + i, 0, restored[i]);
		}
		
		// finally follows inserted data
		for (int i = 30; i <  35; i++) {
			assertEquals("index " + i, INSERT_PATTERN, restored[i]);
		}
		
	}
	
	@Test
	public void cutFileContent() throws IOException {
		Path outFile = testProlog();
		int fromIndex, movedBy, bufferSize;
		byte[] restored = null;
		
		// create sample file [5, 5, 5, 5, 5, 99, 99, 99, 99, 99, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5]
		fromIndex = 5;
		movedBy = 5;
		bufferSize = 4;
		insertData(outFile, fromIndex, movedBy, bufferSize);
		restored = Files.readAllBytes(outFile);
		assertEquals("Test data original length", 25, restored.length);
		
		
		int cutLen = 4;
		// shrink content back
		try (RandomAccessFile raf = new RandomAccessFile(outFile.toFile(), "rw")) {
			cutOutContent(raf, 5, cutLen, 3);
		}
		
		restored = Files.readAllBytes(outFile);
		// four bytes removed
		assertEquals("Shrinked file size", 21, restored.length);
		// first bytes unchanged
		for (int i = 0; i < fromIndex; i++) {
			assertEquals("index " + i, ORIG_PATTERN, restored[i]);
		}
		assertEquals("One byte remains", INSERT_PATTERN, restored[fromIndex]);
		
		// some bytes
		for (int i = fromIndex + 1; i < ORIG_SAMPLE_SIZE + movedBy - cutLen; i++) {
			assertEquals("index " + i, ORIG_PATTERN, restored[i]);
		}
	}
	
	@Test
	public void cutOfFileContentMoreCases() throws FileNotFoundException, IOException {
		Path outFile = testProlog();
		int fromIndex, movedBy, bufferSize;
		byte[] restored = null;
		
		fromIndex = 5;
		movedBy = 5;
		bufferSize = 4;
		insertData(outFile, fromIndex, movedBy, bufferSize);
		restored = Files.readAllBytes(outFile);
		assertEquals("Test data original length", 25, restored.length);
		
		int cutLen = 40;
		// cut of more that overal size of file
		try (RandomAccessFile raf = new RandomAccessFile(outFile.toFile(), "rw")) {
			cutOutContent(raf, 5, cutLen, 3);
		}
		
		restored = Files.readAllBytes(outFile);
		assertEquals("Only first bytes remains", 5, restored.length);
		
		//-------------------------------
		// only last one byte will remain
		testProlog();
		try (RandomAccessFile raf = new RandomAccessFile(outFile.toFile(), "rw")) {
			// write something interesting at last byte
			raf.seek(raf.length()-1);
			raf.write(22);
		}
		
		try (RandomAccessFile raf = new RandomAccessFile(outFile.toFile(), "rw")) {
			cutOutContent(raf, 0, 19, 8);
		}
		restored = Files.readAllBytes(outFile);
		assertEquals("Only last byte", 1, restored.length);
		assertEquals(22, restored[0]);
		
		//-------------------------------
		// cut all bytes
		testProlog();
		try (RandomAccessFile raf = new RandomAccessFile(outFile.toFile(), "rw")) {
			cutOutContent(raf, 0, 20, 8);
		}
		restored = Files.readAllBytes(outFile);
		assertEquals("Only last byte", 0, restored.length);
		
		//-------------------------------
		// cut one byte
		testProlog();
		insertData(outFile, 5, 5, 4);
		try (RandomAccessFile raf = new RandomAccessFile(outFile.toFile(), "rw")) {
			cutOutContent(raf, 4, 1, 7);
		}
		restored = Files.readAllBytes(outFile);
		assertEquals("Only last byte", 24, restored.length);
		// first bytes unchanged
		for (int i = 0; i < 4; i++) {
			assertEquals("index " + i, ORIG_PATTERN, restored[i]);
		}
				
		// inserted byte sequence
		for (int i = 4; i < 9; i++) {
			assertEquals("index " + i, INSERT_PATTERN, restored[i]);
		}
		
		for (int i = 9; i < 24; i++) {
			assertEquals("index " + i, ORIG_PATTERN, restored[i]);
		}
	}
	
	
	private void insertData(Path file, int fromIndex, int moveBy, int bufferSize) throws FileNotFoundException, IOException {
		try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "rw")) {
			this.makeSpace(raf, fromIndex, moveBy, bufferSize);
			raf.seek(fromIndex);
			for (int i = 0; i < moveBy; i++) {
				raf.write(INSERT_PATTERN);
			}
		}	
	}
	
	private void assertRestoredDataAfterInsert(byte[] restoredData, int fromIndex, int movedBy) {
		// first bytes unchanged
		for (int i = 0; i < fromIndex; i++) {
			assertEquals("index " + i, ORIG_PATTERN, restoredData[i]);
		}

		// inserted data
		for (int i = fromIndex; i < movedBy; i++) {
			assertEquals("index " + i, INSERT_PATTERN, restoredData[i]);
		}

		// inserted data
		for (int i = fromIndex + movedBy; i < ORIG_SAMPLE_SIZE; i++) {
			assertEquals("index " + i, ORIG_PATTERN, restoredData[i]);
		}
	}
	
	/**
	 * Vytvori miesto pre vlozenie noveho obsahu v subere s nahodnym pristupom.
	 * Miesto sa vytvori posunutim ostavajuceho obsahu o velkost noveho miesta.
	 * Vytvorene nove miesto nie je inicializovane a hodnota bytov sa nezmenila.
	 * @param raf
	 * 	otvoreny subor s nahodnym pristupom, v ktorom sa bude miesto vystvarat
	 * @param fromIndex
	 * 	index v subore od ktoreho ma byt vytvorene nove miest
	 * @param moveBy
	 * 	velkost vytvoreneho miesta
	 * @param buffSize
	 * 	velkost buffra pouziteho pri presuvani blokov. Len pokusny charakter na overenie
	 *  spravnosti algoritmu
	 * @throws IOException
	 */
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
	}
	
	/**
	 * Cuts out contetn of file, Final size will be lower than original
	 * @param raf
	 * @param fromIndex
	 * @param count
	 * @param buffSize
	 * @throws IOException 
	 */
	private void cutOutContent(RandomAccessFile raf, long fromIndex, long count, int buffSize) throws IOException {
		byte[] moveBuffer = new byte[buffSize];
		long len =  raf.length();
		
		if (fromIndex + count >= len) {
			raf.setLength(fromIndex);
			return;
		}
		
		long currRead = fromIndex + count;
		while (currRead < len) {
			raf.seek(currRead);
			int read = raf.read(moveBuffer);
			raf.seek(currRead-count);
			raf.write(moveBuffer, 0, read);
			currRead += buffSize;
		}
		
		raf.setLength(len-count);
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
