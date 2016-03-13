package org.tondo.sslhandshake;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.junit.Test;
import org.tondo.sslhandshake.SimpleSSLClient;
import org.tondo.sslhandshake.SimpleSSLServer;
import org.tondo.testutils.StandardTestBase;

/**
 * 
 * Server keystore/trustore generated with command <br />
 * <code>keytool -genkeypair -keyalg RSA -alias server -keystore server.jks -storepass trusted -validity 360 -keysize 2048</code>.<br />
 * 
 * Passwords for keystore integrity and for protection alias for private key are the sane <code>trusted</code><br />
 * 
 * Certificate is exported with <br />
 * <code>keytool -keystore server.jks -storepass trusted -export -alias server -file serverCert.cer</code><br />
 * 
 * Client trustore is created by importing certificate <br />
 * <code>keytool -import -alias myserver -keystore client.jks  -file serverCert.cer</code>. Password is still <code>trusted</code><br />
 * @author TondoDev
 *
 */
public class TestSSLHandshake extends StandardTestBase {
	
	@Test
	public void testHandshake() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, InterruptedException {
		int port = 9900;
		
		Path serverStorePath = getFileKeeper().copyResource(inResourceDir("server.jks"));
		KeyStore serverKeystore = KeyStore.getInstance("jks");
		serverKeystore.load(new FileInputStream(serverStorePath.toFile()), "trusted".toCharArray());
		 
		
		SimpleSSLServer server = new SimpleSSLServer(port, serverKeystore, "trusted".toCharArray());
		server.start();
		// this should be enough to server start
		Thread.sleep(500);
		
		Path clientStore = getFileKeeper().copyResource(inResourceDir("client.jks"));
		KeyStore clientTrustore = KeyStore.getInstance("jks");
		clientTrustore.load(new FileInputStream(clientStore.toFile()), "trusted".toCharArray());
		
		SimpleSSLClient client = new SimpleSSLClient(port, clientTrustore, null);
		client.start();
		
		server.join();
		client.join();
		
		assertEquals("Client received correct data", "CS", client.getResponse());
		System.out.println(client.getLogs());
		System.out.println(server.getLogs());
	}

}
