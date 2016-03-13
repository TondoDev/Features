package org.tondo.sslhandshake;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

public class SSLTestPeer extends Thread {
	
	private String peerName;
	
	private LoggableKeyManager keyManager;
	private LoggableTrustManager trustManager;
	
	public SSLTestPeer(String peerName) {
		this.peerName = peerName;
	}
	
	protected final SSLContext initContext(KeyStore store, char[] keyPwd) {
		
		try {
			TrustManagerFactory managerFactory = TrustManagerFactory.getInstance("PKIX");
			managerFactory.init(store);
			TrustManager[] managers = managerFactory.getTrustManagers();
			for (int i = 0; i < managers.length; i++) {
				if (managers[i] instanceof X509TrustManager) {
					this.trustManager = new LoggableTrustManager(this.peerName, (X509TrustManager) managers[i]);
					managers[i] = this.trustManager;
				}
			}
			
			KeyManagerFactory keyManFactory = KeyManagerFactory.getInstance("PKIX");
			keyManFactory.init(store, keyPwd);
			KeyManager[] keyManagers = keyManFactory.getKeyManagers();
			for (int i = 0; i < keyManagers.length; i++) {
				if (keyManagers[i] instanceof X509KeyManager) {
					this.keyManager = new LoggableKeyManager(this.peerName, (X509KeyManager) keyManagers[i]);
					keyManagers[i] = this.keyManager;
				}
			}
			
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(keyManagers, managers, null);
			
			return context;
		} catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | UnrecoverableKeyException e) {
			throw new IllegalStateException("Can't initilize SSL context", e);
		}
		
	}
	
	
	protected LoggableKeyManager getKeyManager() {
		return keyManager;
	}
	
	protected LoggableTrustManager getTrustManager() {
		return trustManager;
	}
	
	
	public String getPeerName() {
		return peerName;
	}
	
	public List<String> getLogs() {
		List<String> result = new ArrayList<>();
		if (getKeyManager() != null) {
			result.addAll(getKeyManager().getLogs());
		}
		if (getTrustManager() != null) {
			result.addAll(getTrustManager().getLogs());
		}
		Collections.sort(result, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				// expected log format begin with number immediately followed by dot
				String orderNoFirst = o1.split("\\.")[0];
				String orderNoSecond = o2.split("\\.")[0];
				return Integer.valueOf(orderNoFirst).compareTo(Integer.valueOf(orderNoSecond));
			}
		});
		
		return result;
	}

}
