package org.tondo.Java7Features;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExampleFileVisitor extends SimpleFileVisitor<Path> {
	
	private Map<String, FileVisitResult> continueConfig;
	
	private int preDirsCalled;
	private int postDirsCalled;
	private int filesVisited;
	private int numOfFails;
	private List<String> callOrder;
	
	/**
	 * Available configuration names:
	 * <ul>
	 * 	<li>preDir</li>
	 * 	<li>postDir</li>
	 * 	<li>visit</li>
	 * 	<li>failed</li>
	 * </ul>
	 * 
	 * @param config
	 * 		configration map for return value of each callback method
	 */
	public ExampleFileVisitor(Map<String, FileVisitResult> config) {
		setConfiguration(config);
		callOrder = new ArrayList<>();
		this.reset();
	}
	
	public ExampleFileVisitor() {
		this(null);
	}
	
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc)
			throws IOException {
		this.postDirsCalled++;
		this.callOrder.add("POST " + pathToString(dir));
		return resolveContinue("postDir");
	}
	
	
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			throws IOException {
		this.preDirsCalled++;
		this.callOrder.add("PRE " + pathToString(dir));
		return resolveContinue("preDir");
	}
	
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException {
		this.filesVisited++;
		this.callOrder.add("FILE " + pathToString(file));
		return resolveContinue("visit");
	}
	
	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc)
			throws IOException {
		this.numOfFails++;
		this.callOrder.add("FAIL " + pathToString(file));
		return resolveContinue("failed");
	}
	
	private FileVisitResult resolveContinue(String callbackName) {
		if (this.continueConfig == null) {
			return FileVisitResult.CONTINUE;
		}
		
		FileVisitResult result = this.continueConfig.get(callbackName);
		return result == null ? FileVisitResult.CONTINUE : result;
	}
	
	public int getPreDirsCalled() {
		return preDirsCalled;
	}
	
	public int getPostDirsCalled() {
		return postDirsCalled;
	}
	
	public int getNumOfFails() {
		return numOfFails;
	}
	
	public int getFilesVisited() {
		return filesVisited;
	}
	
	public List<String> getCallOrder() {
		return new ArrayList<>(callOrder);
	}
	
	public void setConfiguration(Map<String, FileVisitResult> config) {
		this.continueConfig = config == null ? null : new HashMap<>(config);
	}
	
	/**
	 * Reset internal counters
	 */
	public void reset() {
		this.preDirsCalled = 0;
		this.postDirsCalled = 0;
		this.filesVisited = 0;
		this.numOfFails = 0;
		this.callOrder.clear();
	}
	
	private String pathToString(Path path) {
		if (path == null) {
			return null;
		}
		
		Path fileName = path.getFileName();
		return fileName == null ? path.toString() : fileName.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Pre dirs called: ").append(this.preDirsCalled).append('\n')
			.append("Post dirs called: ").append(this.postDirsCalled).append('\n')
			.append("File visit called: ").append(this.filesVisited).append('\n')
			.append("Fails  called: ").append(this.numOfFails).append('\n')
			.append("Call order: ").append(this.callOrder).append('\n');
		return sb.toString();
	}
}
