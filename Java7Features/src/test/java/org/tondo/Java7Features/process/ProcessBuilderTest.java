package org.tondo.Java7Features.process;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;
import org.tondo.Java7Features.JavaFeaturesTestBase;

/**
 * Some examples of OS process manipulations with ProcessBuilder class
 * @author TondoDev
 *
 */
public class ProcessBuilderTest extends JavaFeaturesTestBase {

	/**
	 * Example of create process which immediatelly terminates after writing some data
	 * to stderr  
	 */
	@Test
	public void testCreateProcess() throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder("java", "-version");
		Process proc = builder.start();
		// wait for process termination, current thread is blocked
		proc.waitFor();
		
		// this input stream is connected to program  output (valid for stdout, and stderr)
		// java version is printed to stderr
		InputStream programOutput = proc.getErrorStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(programOutput));
		assertTrue(reader.readLine().startsWith("java version"));
		
		// uknown command
		try {
			new ProcessBuilder("dir").start();
			fail("IOException expected!");
		} catch(IOException e) {}
		
	}
	
	/**
	 * using common input stream for process stdout and stderr.
	 * Same as previous example, but used redirection of stderr to stdout
	 */
	@Test
	public void testRedirectStderrToStdout() throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder("java", "-version");
		// this will "merge" stdout and sterr into single stream
		builder.redirectErrorStream(true);
		Process proc = builder.start();
		
		// wait for process termination, current thread is blocked
		proc.waitFor();
		
		// this input stream is connected to program  output (valid for stdout, and stderr)
		// java version is printed to stderr
		InputStream programOutput = proc.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(programOutput));
		assertTrue(reader.readLine().startsWith("java version"));
	}
	
	/**
	 * Examine return value of process
	 */
	@Test
	public void testExitValues() throws InterruptedException, IOException {
		ProcessBuilder builder = new ProcessBuilder("java", "-version");
		Process proc = builder.start();
		int returnCodeWaitFor = proc.waitFor();
		int exitValue = proc.exitValue();
		
		// when everything goes OK, 0 should be returned
		assertEquals(0, exitValue);
		// two approach of retrieving exit code should have same value
		assertEquals(returnCodeWaitFor, exitValue);
		
		// bad exit status will be emulated with wrong program argument
		ProcessBuilder badBuilder = new ProcessBuilder("java", "-veee");
		Process badProcess = badBuilder.start();
		int badWaitFor = badProcess.waitFor();
		int badExitCOde = badProcess.exitValue();
		
		// failure should be denoted by value different from zero
		assertTrue(badWaitFor != 0);
		// same values by different retrieval methods
		assertEquals(badWaitFor, badExitCOde);
	}
	
	/**
	 * Example of redirection of process output to file
	 * same approach can be used for input from file
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testRedirectionToFile() throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder("java", "-version");
		Path targetPath = inTempDir("retirTarget.txt");
		getFileKeeper().markForWatch(targetPath);
		// file doesnt exists at the beginning
		assertFalse(Files.exists(targetPath));
		
		// stderr will be redirected to this file
		builder.redirectError(targetPath.toFile());
		
		Process proc = builder.start();
		proc.waitFor();
		
		// file should be created even if doesnt exists before
		assertTrue(Files.exists(targetPath));
		
		// whole content of file is readed and first line compared
		List<String> lines = Files.readAllLines(targetPath, Charset.defaultCharset());
		assertTrue(lines.size() > 0);
		assertTrue(lines.get(0).startsWith("java version"));
//		assertEquals("java version \"1.7.0_60\"", lines.get(0));
	}
	
}
