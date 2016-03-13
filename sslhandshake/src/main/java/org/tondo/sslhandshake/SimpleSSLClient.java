package org.tondo.sslhandshake;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.SSLSocketFactory;

/**
 * 
 * @author TondoDev
 *
 */
public class SimpleSSLClient extends SSLTestPeer {

	private SSLSocketFactory socketFactory;
	private int port;
	
	private String response;
	
	/**
	 * 
	 * @param store
	 * @param keyPWd
	 * 	password to read private keys
	 */
	public SimpleSSLClient(int port, KeyStore store, char[] keyPwd) {
		super("C");
		socketFactory = initContext(store, keyPwd).getSocketFactory();
		this.port = port;
	}
	
	@Override
	public void run() {
		try (Socket socket = this.socketFactory.createSocket(InetAddress.getLoopbackAddress(), this.port)) {
			System.out.println("Client connected!");
			Writer writer = new OutputStreamWriter(socket.getOutputStream());
			writer.write('C');
			writer.flush();
			System.out.println("Client wrote data!");
			
			char[] buff = new char[2];
			Reader reader = new InputStreamReader(socket.getInputStream());
			System.out.println("Client is going to read server response!");
			if (reader.read(buff) != 2) {
				throw new IllegalStateException("Expected exactly two characters!");
			}
			System.out.println("Client received response!");
			this.response = new String(buff);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Client exit!");
	}
	
	public String getResponse() {
		return response;
	}
}
