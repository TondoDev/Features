package org.tondo.Java7Features.io;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.AcceptPendingException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.tondo.Java7Features.JavaFeaturesTestBase;

public class IoUtilsTest extends JavaFeaturesTestBase {

	private static final int PORT_NO = 5000;
	
	/**
	 * Reading text file all at once
	 */
	@Test
	public void testReadAllAtOnce() throws IOException {
		Path source = inResourceDir("names.txt");

		// ensured that file will be closed
		byte[] data = Files.readAllBytes(source);
		String textContent = new String(data);
		String[] lines = textContent.split("\r\n");	// CR LF (windows like)
		assertEquals(3, lines.length);
		assertArrayEquals(new String[] {"Tonko", "Jozko", "Jarka"}, lines);
		
		// when not existing file is passed
		try {
			Files.readAllBytes(Paths.get("gugug.gaga"));
			fail("NoSuchFileException expected!");
		} catch (NoSuchFileException e) {}
	}
	
	/**
	 *	Writing content to file with read back  
	 */
	@Test
	public void testWriteAllAtOnce() throws IOException {
		Path target = inTempDir("target.txt");
		assertFalse(Files.exists(target));
		
		String content = "Bola babka\nMala capka!";
		// default open options are  CREATE, TRUNCATE_EXISTING, and WRITE
		// so by default file is created if does not exists
		Files.write(target, content.getBytes());
		assertTrue(Files.exists(target));
		getFileKeeper().markForWatch(target);
		
		// read back content
		// readAllLines is other possibility to read text files all at once
		List<String> lines = Files.readAllLines(target, Charset.defaultCharset());
		assertEquals(Arrays.asList("Bola babka", "Mala capka!"),  lines);
		
		// appending content
		Files.write(target, "additional".getBytes(), StandardOpenOption.APPEND);
		lines = Files.readAllLines(target, Charset.defaultCharset());
		assertEquals(Arrays.asList("Bola babka", "Mala capka!additional"),  lines);
		
		// with WRITE only mode, file must exists
		try {
			Files.write(Paths.get("gugu.gaga"), "additional".getBytes(), StandardOpenOption.WRITE);
			fail("NoSuchFileException expected!");
		} catch(NoSuchFileException e) {}
		
		
		Path nextFile = Paths.get("nextFile.txt");
		// String implements CharSequence
		Files.write(nextFile, Arrays.asList("Juventus", "Barcelona"), Charset.defaultCharset());
		getFileKeeper().markForWatch(nextFile);
		// read back and compare
		assertEquals(Arrays.asList("Juventus", "Barcelona"), Files.readAllLines(nextFile, Charset.defaultCharset()));
		
		// read back with byte[] version
		byte[] data = Files.readAllBytes(nextFile);
		assertEquals("Juventus\r\nBarcelona\r\n", new String(data));
	}
	
	/**
	 * Example how can be used BufferedReader and BufferedWriter created using Files class
	 */
	@Test
	public void testBufferedReadWrite() throws IOException {
		Path target = inTempDir("mylife.txt");
		getFileKeeper().markForWatch(target);
		
		// writing to file
		try (BufferedWriter writer = Files.newBufferedWriter(target, Charset.defaultCharset(), StandardOpenOption.CREATE); ) {
			writer.write("My name is Anton");
			writer.newLine();
			writer.newLine();
			writer.write("and I want to be a developer");
			writer.newLine();
		}
		
		assertTrue(Files.exists(target));
		
		// reading previously writed lines and storing them into list
		List<String> lines = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(target, Charset.defaultCharset())) {
			String line = null;
			// returning null mean EOF
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		}
		
		assertEquals(3, lines.size());
		assertEquals("My name is Anton", lines.get(0));
		assertEquals("and I want to be a developer", lines.get(2));
		
		// because read line doesn't return line terminator in readed string
		assertEquals("", lines.get(1));
	}
	
	/**
	 * Reading and writing to files using Input/Output stream classes
	 * @throws IOException 
	 */
	@Test
	public void testUnbufferedReadWrite() throws IOException {
		Path target = inTempDir("mylife.txt");
		getFileKeeper().markForWatch(target);
		
		String content = "This day is best day!";		
		int dataSize = content.getBytes().length;
		
		try (OutputStream os = Files.newOutputStream(target, StandardOpenOption.CREATE)) {
			os.write(content.getBytes());
		}
		
		assertTrue(Files.exists(target));
		assertEquals(new Long(dataSize), (Long)Files.getAttribute(target, "size"));

		byte[] readedData = new byte[dataSize];
		try (InputStream is = Files.newInputStream(target)) {
			is.read(readedData);
		}
		
		assertEquals(content, new String(readedData));
	}
	
	/**
	 * Examine usage of SeekableByteChannel
	 */
	@Test
	public void testReadFromSeekableByteChannel() throws IOException {
		Path workFile = getFileKeeper().copyResource(Paths.get("names.txt"));
		assertTrue(Files.exists(workFile));
		
		int bufferCapacity = 8;
		ByteBuffer buffer = ByteBuffer.allocate(bufferCapacity);
		try (SeekableByteChannel channel = Files.newByteChannel(workFile)) {
			assertEquals(0,channel.position());
			// reading from beggining
			int readedDataSize = channel.read(buffer);
			assertEquals(bufferCapacity, readedDataSize);
			assertEquals(bufferCapacity, channel.position());
			// also buffer state changes
			assertEquals(buffer.capacity(), buffer.position());
			assertEquals(buffer.position(), buffer.limit());
			
			buffer.rewind();
			String decodedData = Charset.defaultCharset().decode(buffer).toString();
			assertEquals("Tonko\r\nJ", decodedData);
			
			// move to the end
			channel.position(channel.size());
			assertEquals(channel.size(), channel.position());
			// reading from end of channel causes EOF indicator
			buffer.clear();
			assertEquals(-1, channel.read(buffer));
			
			// default buffer is opened in read only
			try {
				channel.write(ByteBuffer.wrap("ahoj".getBytes()));
				fail("NonWritableChannelException expected!");
			} catch (NonWritableChannelException e) {}
			
		}
	}
	
	/**
	 * Writing using seekable byte channel 
	 */
	@Test
	public void testWriteToSeekableChannel() throws IOException {
		Path workFile = getFileKeeper().copyResource(Paths.get("names.txt"));
		assertTrue(Files.exists(workFile));
		
		try (SeekableByteChannel channel = Files.newByteChannel(workFile, StandardOpenOption.WRITE)) {
			long channelSize = channel.size();
			// move to third (on second index) byte of channel (absolute)
			int writePos = 2;
			channel.position(writePos);
			assertEquals(writePos, channel.position());
			byte[] dataToWrite = "pes".getBytes();
			// create buffer from backing array (changing one will modify other)
			ByteBuffer buffer = ByteBuffer.wrap(dataToWrite);
			int writenSize = channel.write(buffer);
			assertEquals(dataToWrite.length, writenSize);
			// position in channel is changed
			assertEquals(writePos + dataToWrite.length, channel.position());
			// channel size is still as was before write
			// because it overwrites following bytes
			assertEquals(channelSize, channel.size());
			
			// rewind to the end
			channel.position(channel.size());
			assertEquals(channel.size(), channel.position());
			buffer.rewind(); // set position in buffer to zero
			writenSize = channel.write(buffer);
			// now channel size should be extended
			assertEquals(channelSize + writenSize , channel.size());
			// position also moves further
			assertEquals(channel.size(), channel.position());
		}
	}
	
	/**
	 * Threading scenario of simple client - server communication.
	 * As a result Future (pending result) object is used
	 * 
	 * See also accept() alternative with CompletionHandler argument instead of returning
	 * Future object
	 * @throws InterruptedException 
	 */
	@Test
	public void testAsyncSocketChannelsByFuture() throws InterruptedException {
		final Set<String> serverAccepted = new HashSet<>();
		
		Thread serverThread = new Thread() {
			@Override
			public void run() {
				try {
					AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
					// address and port
					server.bind(new InetSocketAddress("localhost", PORT_NO));
					Future<AsynchronousSocketChannel> pendingAccept = server.accept();
					// block until some connection is accepted
					AsynchronousSocketChannel clientCommChannel = pendingAccept.get();
					ByteBuffer buff = ByteBuffer.allocate(128);
					
					int receivedDataSize = 0;
					while ((receivedDataSize = clientCommChannel.read(buff).get()) != -1) {
						buff.flip();
						// convert readed byte buffer to string with default encoding
						String dataFromClient = Charset.defaultCharset().decode(buff).toString();
						serverAccepted.add(dataFromClient);
						buff.clear();
						if (receivedDataSize  < 128) {
							clientCommChannel.write(ByteBuffer.wrap(("ECHOED " + dataFromClient).getBytes())).get();
							clientCommChannel.close();
							break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		};
		
		// server thread executed
		serverThread.start();
		// should be enought time for server start
		Thread.sleep(1500);
		
		final String clientData = "HALLOO!";
		final Set<String> clientReceived = new HashSet<>();
		
		Thread clientThread = new Thread() {
			@Override
			public void run() {
				try {
					AsynchronousSocketChannel socket = AsynchronousSocketChannel.open();
					Future<Void> connected = socket.connect(new InetSocketAddress("localhost", PORT_NO));
					// waits until connection is established
					connected.get();
					// sent data to server (by future object)
					socket.write(ByteBuffer.wrap(clientData.getBytes()));
					
					ByteBuffer buff = ByteBuffer.allocate(128);
					socket.read(buff).get();
					buff.flip();
					clientReceived.add(Charset.defaultCharset().decode(buff).toString());
					
//					socket.read(buff, null, new CompletionHandler<Integer, Void>() {
//						public void completed(Integer result, Void attachment) {
//							System.out.println("completed " + result);
//						};
//						
//						public void failed(Throwable exc, Void attachment) {
//							
//						};
//					});
					
					socket.close();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		};
		
		clientThread.start();
		
		// wait for both thread ends
		try {
			serverThread.join();
			clientThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// server received something
		assertEquals(clientData, serverAccepted.iterator().next());
		// client accepts modified response from server
		assertEquals("ECHOED " +clientData, clientReceived.iterator().next());
		
	}
	
	/**
	 * ASync file write using CompletionHandler
	 * @throws IOException 
	 */
	@Test
	public void testAsyncWriteToFile() throws IOException {
		Path targetFile = inTempDir("dest.txt");
		getFileKeeper().markForWatch(targetFile);
		
		String data = "My name is Developer!";
		
		// first item will be attachment 
		// second item will be writen data size
		// third thread ID
		final List<Integer> receivedAttachment = new ArrayList<>();
		
		try(AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(targetFile, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
			ByteBuffer buff = ByteBuffer.wrap(data.getBytes());
			fileChannel.write(buff, 0, 5, new CompletionHandler<Integer, Object>() {

				@Override
				public void completed(Integer result, Object attachment) {
					receivedAttachment.add((Integer)attachment);
					receivedAttachment.add(result);
					receivedAttachment.add(Integer.valueOf((int)Thread.currentThread().getId()));
				}

				@Override
				public void failed(Throwable exc, Object attachment) {
				}
			});
			
			// of course write() overload with returning Future<Integer> is also available
 			// fileChannel.write(buff, 0).get();
		}
		
		// non-existing file without CREATE option
		try(AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open( inTempDir("gug.txt"), StandardOpenOption.WRITE)) {
			fail("NoSuchFileException expected!");
		} catch (NoSuchFileException e) {}
		
		// wait for async write completion, 1sec should be enough
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertEquals(Integer.valueOf(5), receivedAttachment.get(0));
		assertEquals(Integer.valueOf(data.length()), receivedAttachment.get(1));
		// maybe bad assertion, because it is not guaranteed that CompletionHandler is executed
		// in own thread
		assertNotEquals("Current and handler thread may be different!", (int)Thread.currentThread().getId(),  (int)receivedAttachment.get(1));
		
		// read back written data
		String readedData = new String(Files.readAllBytes(targetFile));
		assertEquals(data, readedData);
	}
	
	/**
	 * Simple example of asynchronous file read using thread pool
	 */
	@Test
	public void testAsyncFileRead() throws IOException, InterruptedException {
		ExecutorService pool = new ScheduledThreadPoolExecutor(2);
		Path input = inResourceDir("channel_read.txt");
		
		final int readsCount = 5;
		final int bytesToRead = 5;
		ByteBuffer[] buffers = new ByteBuffer[readsCount];
		
		try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(input, 
				EnumSet.of(StandardOpenOption.READ) , pool)) {
			
			// execute reading operations
			for (int i = 0; i < readsCount; i++) {
				buffers[i] = ByteBuffer.allocate(bytesToRead);
				// once is byte buffer passed as a storage place, once is passed as attachment to be read in completion handler
				fileChannel.read(buffers[i], (long)i * bytesToRead, buffers[i], new CompletionHandler<Integer, ByteBuffer>() {

					@Override
					public void completed(Integer result, ByteBuffer attachment) {
					}

					@Override
					public void failed(Throwable exc, ByteBuffer attachment) {
					}
				});
			}			
			
			// wait all task finish
			// NOTE: this wait is needed here because if it placed after try-with-resource
			// block, channel is closed and not all reads are completed
			pool.awaitTermination(2L, TimeUnit.SECONDS);
		} 
		
		// examining readed data
		for (int i = 0; i < readsCount; i++) {
			StringBuilder sb = new StringBuilder();
			for (int c = 0; c < bytesToRead; c++) {
				sb.append(i+1);
			}
			buffers[i].flip();
			assertEquals(sb.toString(), Charset.defaultCharset().decode(buffers[i]).toString());
		}
	}
	
	/**
	 *	Demo of retrieval supported socked options of channel 
	 */
	@Test
	public void testSocketOptions() throws IOException {
		AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
		Set<SocketOption<?>> supportedOptions =  server.supportedOptions();
		
		// results may be system dependent
		assertEquals(2, supportedOptions.size());

		// just for better assertions
		Map<String, Class<?>> optionsMapping = new HashMap<>();
		for (SocketOption<?> option : supportedOptions) {
			optionsMapping.put(option.name(), option.type());
		}
		
		// option names
		assertEquals(cSET("SO_REUSEADDR", "SO_RCVBUF"), optionsMapping.keySet());
		// option types
		assertEquals(Integer.class, optionsMapping.get("SO_RCVBUF"));
		assertEquals(Boolean.class, optionsMapping.get("SO_REUSEADDR"));
		
		// values of options can be determined by .toString() method
		// option to socket can be set AsynchronousServerSocketChannel.setOption(StandardSocketOption, value)
	}
	
	/**
	 * Demonstration that accept on Asynch server socket can't be called
	 * again, before accept() call receive connection  
	 * @throws InterruptedException 
	 */
	@Test
	public void testServerAcceptInLoop() throws IOException, InterruptedException {
		AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
		server.bind(new InetSocketAddress("localhost", PORT_NO));

		// this will not work, multiple accepts called
		try {
			for (int i = 0; i < 3 ; i++) {
				server.accept();
			}
			fail("AcceptPendingException expected!");
		} catch(AcceptPendingException e) {}
		
		server.close();
	}
}
