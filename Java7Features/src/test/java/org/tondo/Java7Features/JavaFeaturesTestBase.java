package org.tondo.Java7Features;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.tondo.testutils.StandardTestBase;

/**
 * Base class for testing Java 7 features
 * Set common properties of all tests.
 * 
 * @author TondoDev
 *
 */
public class JavaFeaturesTestBase extends StandardTestBase{

	@Override
	protected void init() {
		setResourceLocation(Paths.get("SampleFiles"));
	}
	
	/**
	 * Returns Path object in working directory if provided path is relative.
	 * If path is absolute same path is returned.
	 * @param path
	 * 		path to resolve against working directory
	 * @return
	 * 		resolved path
	 */
	protected Path toEffectivePath(Path path) {
		if (!path.isAbsolute()) {
			return getTempLocation().resolve(path);
		}
		
		return path;
	}
	
	/**
	 * Create file in working area if relative path is specified, or at provided absolute location
	 * Created file is immediately registered in FileKeeper to be deleted after test run.
	 * From provided parts Path object is constructed at first.
	 * If file already exists, exception is thrown
	 * @param part
	 * @param parts
	 * @return
	 * 		Path object for created file
	 * @throws IOException 
	 */
	protected Path createWorkFile(String part, String... parts) throws IOException {
		return this.createWorkFile(Paths.get(part, parts));
	}
	
	/**
	 * Create file in working area if relative path is specified, or at provided absolute location
	 * Created file is immediately registered in FileKeeper to be deleted after test run
	 * @param part
	 * @param parts
	 * @return
	 * @throws IOException 
	 */
	protected Path createWorkFile(Path path) throws IOException {		
		Path created = Files.createFile(toEffectivePath(path));
		getFileKeeper().markForWatch(created);
		return created;
	}
	
	protected Path createWorkDirectory(Path path) throws IOException {
		Path created = Files.createDirectories(toEffectivePath(path));
		getFileKeeper().markForWatch(created);
		return created;
	}
	
	protected Path createWorkDirectory(String part, String... parts) throws IOException {
		return this.createWorkDirectory(Paths.get(part, parts));
	}
	
	protected void assertExists(Path path) {
		Assert.assertTrue("File or directory \"" + path + "\" does not exists!" , Files.exists(path));
	}
	
	protected void assertNotExists(Path path) {
		Assert.assertFalse("File or directory \"" + path + "\" exists!", Files.exists(path));
	}
	
	// TODO extract to helper class
	@SuppressWarnings("unchecked")
	protected <T> Set<T> cSET(T... items) {
		Set<T> rv = new HashSet<>();
		Collections.addAll(rv, items);
		return rv;
	}
	
}
