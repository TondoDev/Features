package org.tondo.Java7Features.io;


import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;

import org.junit.Test;
import org.tondo.Java7Features.JavaFeaturesTestBase;

/**
 * Samples of usage java.util.Properties clsas
 * @author TondoDev
 *
 */
public class PropertiesTest extends JavaFeaturesTestBase {
	
	@Test
	public void testCreationAndLoadPropertyFile() throws IOException {
		
		Properties props = new Properties();
		assertTrue("After creation properties should be empty!", props.isEmpty());
		
		// setting property
		props.setProperty("username", "tondodev");
		assertFalse("Properties should not be empty after setting value!", props.isEmpty());
		assertEquals("Property value mismatch!","tondodev", props.getProperty("username"));
		
		// getting not existing property
		assertNull("Non existing property should be returned as null!", props.getProperty("halakabalaka"));
		
		// another value
		props.setProperty("password", "heslo");
		assertEquals("all propterties keys mismatch", cSET("username", "password"), props.keySet());
		assertEquals("all propterties values mismatch", cSET("tondodev", "heslo"), new HashSet<>(props.values()));
		assertEquals("Size of properties mismatch!", 2, props.size());
		
		Path defaultProps = inTempDir("defautl.properties");
		try (FileOutputStream fos = new FileOutputStream(defaultProps.toFile())) {
			props.store(fos, " this is the best!");
		}
		// mark for deletion after test
		getFileKeeper().markForWatch(defaultProps);
		
		assertTrue("Properties file shoud exists on disk!", defaultProps.toFile().exists());
		
		//------------ LOADING SAVED PROPS
		Properties loadedProps = new Properties();
		assertTrue("After creation loaded properties should be empty!", loadedProps.isEmpty());
		
		// loading from file
		try (FileInputStream fis = new FileInputStream(defaultProps.toFile())) {
			loadedProps.load(fis);
		}
		
		assertEquals("loaded propterties keys mismatch", cSET("username", "password"), loadedProps.keySet());
		assertEquals("loaded propterties values mismatch", cSET("tondodev", "heslo"), new HashSet<>(loadedProps.values()));
		assertEquals("Size of loaded properties mismatch!", 2, loadedProps.size());
		
		// change password and save
		loadedProps.setProperty("password", "hnacka");
		try (FileOutputStream fos = new FileOutputStream(defaultProps.toFile())) {
			loadedProps.store(fos, " saving changed ");
		}
		
		// read again if change saved correctly
		Properties lastCheckProps = new Properties();
		try (FileInputStream fis = new FileInputStream(defaultProps.toFile())) {
			lastCheckProps.load(fis);
		}
		assertEquals("loaded propterties keys mismatch", cSET("username", "password"), lastCheckProps.keySet());
		assertEquals("Changed value after reload is not matching", "hnacka", lastCheckProps.getProperty("password"));
	}
	
	@Test
	public void testPropetiesNullValue() throws IOException {
		Properties defaults = new Properties();
		try {
			defaults.setProperty("hodnota", null);
			fail("NullPointerException expected!");
		} catch(NullPointerException e) {}
	}
	
	/**
	 * Simulation of usage default properties hierrarchy.
	 * Properties created with constructor with Properthis argument, create defauls for this prperties
	 * When current properties doesnt have such key, it is searched in defaults.
	 * Current properties can override defaults with same key, but doesnt modify values in defaultt proverties
	 * ( when key is removed from current, value from defaults will be returned again).
	 * 
	 * Both default and current must be saved separately.
	 * 
	 */
	@Test
	public void testPropertiesDefault() {
		Properties defaults = new Properties();
		defaults.setProperty("def_slnko", "svieti");
		defaults.setProperty("def_voda", "laka");
		
		assertEquals("Default propeties after clreation!", cSET("def_slnko", "def_voda"), defaults.keySet());
		
		// properties passed to constructor should be treated as default fallbac
		Properties appProps = new Properties(defaults);
		assertEquals("even if default properties are set 'current' properties size is zero at the begining", 0, appProps.size());
		
		// unknow value
		assertNull("Unknown key returns null", appProps.getProperty("halaka"));
		assertEquals("Not found in current but found in fallback defaults", "svieti", appProps.getProperty("def_slnko"));
		
		// changing value which is also in fallback
		appProps.setProperty("def_slnko", "zatmenie");
		assertEquals("New value shoudl override defautls", "zatmenie", appProps.getProperty("def_slnko"));
		assertEquals("Value in defaults should not be changed!", "svieti", defaults.getProperty("def_slnko"));
		
		// props contains only its own properties
		appProps.setProperty("nove", "psica");
		assertEquals(cSET("def_slnko", "nove"), appProps.keySet());
		assertEquals(cSET("zatmenie", "psica"), new HashSet<>(appProps.values()));
		
		// all property keys together with defauls
		assertEquals("Keys of all propetries with overloaded default", cSET("def_slnko", "nove", "def_voda"), 
				appProps.stringPropertyNames()); // ehm, enumeration to set conversion :S
		
		appProps.remove("def_slnko");
		assertEquals("overridin key is removed", cSET("nove"), appProps.keySet());
		assertEquals("removed key falls back to defaults again", "svieti", appProps.getProperty("def_slnko"));
		
	}

}
