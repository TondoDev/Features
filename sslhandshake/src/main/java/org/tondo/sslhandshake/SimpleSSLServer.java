package org.tondo.sslhandshake;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

/**
 * 
 * @author TondoDev
 *
 */
public class SimpleSSLServer extends SSLTestPeer {
	
	private SSLServerSocketFactory socketFactory = null;
	private int port;
	
	public SimpleSSLServer(int port, KeyStore keyStore, char[] keyPwd) {
		super("S");
		SSLContext ctx = initContext(keyStore, keyPwd);
		this.socketFactory = ctx.getServerSocketFactory();
		this.port = port;
	}

	
	@Override
	public void run() {
		try (SSLServerSocket serverSocket = (SSLServerSocket) this.socketFactory.createServerSocket(port);
				Socket client = serverSocket.accept();){
			System.out.println("Server accepted connection!");
			Reader reader = new InputStreamReader(client.getInputStream());
			char[] buff = new char[2];
			System.out.println("Server is going to read request!");
			int chars = reader.read(buff);
			if (chars != 1) {
				throw new IllegalStateException("Expected exactly one character but read " + chars);
			}
			
			System.out.println("Server has red request!");
			
			// server signature
			buff[1] = 'S';
			
			System.out.println("Server is going to write response!");
			Writer writer = new OutputStreamWriter(client.getOutputStream());
			writer.write(buff);
			writer.flush();
			System.out.println("Server response written!");
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Server exit!");
	}
	
}
