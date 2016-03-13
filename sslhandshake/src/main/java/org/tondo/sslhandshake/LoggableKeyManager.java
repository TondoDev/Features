package org.tondo.sslhandshake;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

public class LoggableKeyManager extends Loggable implements X509KeyManager{
	
	
	private String who;
	private X509KeyManager original;
	
	public LoggableKeyManager(String name, X509KeyManager orig) {
		this.who = name;
		this.original = orig;
	}

	@Override
	public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
		log(who, "chooseClientAlias");
		return original.chooseClientAlias(keyType, issuers, socket);
	}

	@Override
	public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
		log(who, "chooseServerAlias");
		String alias = original.chooseServerAlias(keyType, issuers, socket);
		System.out.println("KeyType: " + keyType + " Principals: " + issuers + " Retval: " + alias);
		return alias;
	}

	@Override
	public X509Certificate[] getCertificateChain(String alias) {
		log(who, "getCertificateChain");
		X509Certificate[] chain = original.getCertificateChain(alias);
		System.out.println("Alias: " + alias + " Retval: " + chain[0].getSubjectDN());
		return original.getCertificateChain(alias);
	}

	@Override
	public String[] getClientAliases(String keyType, Principal[] issuers) {
		log(who, "getClientAliases");
		return original.getClientAliases(keyType, issuers);
	}

	@Override
	public PrivateKey getPrivateKey(String alias) {
		log(who, "getPrivateKey");
		PrivateKey pk = original.getPrivateKey(alias);
		System.out.println("Alias: " + alias + " Retval: " + pk.getFormat());
		return pk;
	}

	@Override
	public String[] getServerAliases(String keyType, Principal[] issuers) {
		log(who, "getServerAliases");
		return original.getServerAliases(keyType, issuers);
	}

}
