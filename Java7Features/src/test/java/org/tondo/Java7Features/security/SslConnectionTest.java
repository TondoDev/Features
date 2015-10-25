package org.tondo.Java7Features.security;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.junit.Test;
import org.tondo.Java7Features.JavaFeaturesTestBase;

/**
 * 
 * @author TondoDev
 *
 */
public class SslConnectionTest extends JavaFeaturesTestBase {
	
	/**
	 * init test resource components to point to keystore directory as defaul
	 */
	@Override
	protected void init() {
		setResourceLocation(Paths.get("keystore"));
	}

	/**
	 * Communication with SSL Server and SSL client.
	 * Important part is setting of keystore (for server) and truststore (for client) in system properties
	 * before client and server is created.
	 */
	@Test
	public void testClientServerSSLConnection() throws IOException, InterruptedException {
		
		final Path keystoreFile = getFileKeeper().copyResource(inResourceDir("serverstore.jks"));
		
		// overriding location of kyestore and its password by setting system properties
		// This setting must be applied before ssl server socket is created
		// there is also other way how to set keystore for SSLserverSocket, using SSLContext and KeyManagerFactory.
		// TODO when was used keystore with more than one key and their passwords were different 
		// from keystore password, it threw UnrecoverableKeyException: Cannot recover key
		System.setProperty("javax.net.ssl.keyStore", keystoreFile.toString());
		System.setProperty("javax.net.ssl.keyStorePassword", "storepass");
		
		// truststore is used by client to know which server can trust
		// in this case, same file as servers keystore
		System.setProperty("javax.net.ssl.trustStore", keystoreFile.toString());
		System.setProperty("javax.net.ssl.trustStorePassword", "storepass");
		
		Thread serverThread = new Thread() {
			
			@Override
			public void run() {
				// SSL sockets are created by ServerSocketFactory
				ServerSocketFactory serverFactory = SSLServerSocketFactory.getDefault();
				assertTrue(serverFactory instanceof SSLServerSocketFactory);
				SSLServerSocketFactory sslServerFactory = (SSLServerSocketFactory)serverFactory;
				
				try {
					// bound socket on port
					SSLServerSocket serverSocket = (SSLServerSocket)sslServerFactory.createServerSocket(8800);
					
					SSLSocket clientConnection = (SSLSocket) serverSocket.accept();
					InputStream is =  clientConnection.getInputStream();
					byte[] receivedData = new byte[1024];
					int received = is.read(receivedData);
					// Arrays.copyOf is because to remove trailing (probably zeroed) bytes from receivedData byte array
					String parsedClientData = new String(Arrays.copyOf(receivedData, received));
					
					// reply with client data with prepended server specific value
					clientConnection.getOutputStream().write(("RESPONSE " + parsedClientData) .getBytes());
					clientConnection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			};
		};
		
		serverThread.start();
		// this should be enough for server start
		Thread.sleep(1500);
		
		final String clientData = "sifrovane data";
		// data received client from server
		// need to be settable from anonymous class
		final Set<String> clientReceived = new HashSet<>();
		Thread clientThread = new Thread() {
			@Override
			public void run() {
				try {
					SSLSocket serverConnection = (SSLSocket)SSLSocketFactory.getDefault().createSocket("localhost", 8800);
					// better should be usege of BufferedWriter(OutputStreamWriter(outputStream))
					BufferedOutputStream  bos = new BufferedOutputStream(serverConnection.getOutputStream());
					bos.write(clientData.getBytes());
					bos.flush(); // because we are buffered now
					
					// reading  response from server
					BufferedReader buffReader = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
					CharBuffer chBuff = CharBuffer.allocate(128);
					buffReader.read(chBuff);
					chBuff.flip();	// for reading filled content with correct bounds
					clientReceived.add(chBuff.toString());
					
					serverConnection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		clientThread.start();
		
		// waits for both threads to terminate
		clientThread.join();
		serverThread.join();
		
		assertEquals(1, clientReceived.size());
		// client should received same as it sends but with server modification
		assertEquals("RESPONSE " + clientData, clientReceived.iterator().next());
	}
}
