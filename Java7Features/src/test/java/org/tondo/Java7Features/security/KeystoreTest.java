package org.tondo.Java7Features.security;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.tondo.Java7Features.JavaFeaturesTestBase;

/**
 * Java keystore manipulations tests and examples
 * 
 * @author TondoDev
 *
 */
public class KeystoreTest  extends JavaFeaturesTestBase {
	
	/**
	 * init test resource components to point to keystore directory as defaul
	 */
	@Override
	protected void init() {
		setResourceLocation(Paths.get("keystore"));
	}
	
	/**
	 * Basic test of loading keystore behavior.  
	 */
	@Test
	public void testLoadingKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		Path keystorePath = inResourceDir("teststore.jks");
		assertTrue(Files.exists(keystorePath));
		
		// default keystore is set in java security file
		assertEquals("jks", KeyStore.getDefaultType());
		
		// retrieve kestore object by its type
		KeyStore store = KeyStore.getInstance( KeyStore.getDefaultType());
		assertNotNull(store);
		
		// unknown keystore type
		try {
			KeyStore.getInstance( "WTF");
			fail("KeyStoreException expected!");
		} catch (KeyStoreException e) {}
		
		FileInputStream fis = new FileInputStream(keystorePath.toFile());
		
		// symmetric operation is store()
		store.load(fis, "storepass".toCharArray());
		fis.close();
		
		// attempt to load with bad storepass
		try {
			fis = new FileInputStream(keystorePath.toFile());
			store.load(fis, "wrongpassword".toCharArray());
			fail("IOException expected due to wrong password!");
		} catch (IOException e) {}
		fis.close();
	}
	
	/**
	 * Sample of reading keystore data 
	 */
	@Test
	public void testListKeystoreEntries() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
		KeyStore store = loadKeystore(inResourceDir("teststore.jks"), "storepass");
		
		// all aliases
		Enumeration<String> aliases = store.aliases();
		Set<String> foundEntries = new HashSet<>();
		while(aliases.hasMoreElements()) {
			foundEntries.add(aliases.nextElement());
		}
		assertEquals(cSET("mybank", "myprivate"), foundEntries);
		
		// also exists getEntry(), getCertificate()...
		Key key = store.getKey("myprivate", "privatepass".toCharArray());
		assertNotNull(key);
		
		assertEquals("RSA", key.getAlgorithm());
		assertTrue(key instanceof RSAPrivateKey);
		assertEquals("PKCS#8",  key.getFormat());
		
		// certificate contains public information of key pair
		Certificate cert = store.getCertificate("mybank");
		assertNotNull(cert);
		assertEquals("X.509", cert.getType());
	}
	
	
	/**
	 * encapsulated loading of keystore from path object 
	 */
	private KeyStore loadKeystore(Path path, String password) { 
		assertNotNull(path);
		try (FileInputStream fis = new FileInputStream(path.toFile())) {
			KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
			char[] pwdArray = password == null ? null : password.toCharArray();
			store.load(fis, pwdArray);
			return store;
		} catch (Exception e) {
			// on any error return null
			e.printStackTrace();
			return null;
		}
	}

}
