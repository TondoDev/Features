package org.tondo.sslhandshake;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * 
 * @author TondoDev
 *
 */
public class LoggableTrustManager extends Loggable implements X509TrustManager{
	private String who;
	private X509TrustManager original;
	
	public LoggableTrustManager(String myName, X509TrustManager orig) {
		this.who = myName;
		this.original = orig;
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		this.log(who, "checkClientTrusted");
		original.checkClientTrusted(chain, authType);
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		this.log(who, "checkServerTrusted");
		original.checkServerTrusted(chain, authType);
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		this.log(who, "getAcceptedIssuers");
		return original.getAcceptedIssuers();
	}

}
