package org.tondo.Java7Features.paths;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SecureDirectoryStream;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.tondo.Java7Features.ExampleFileVisitor;
import org.tondo.Java7Features.JavaFeaturesTestBase;

public class FileSystemTest extends JavaFeaturesTestBase {

	/**
	 * File store properties examined
	 */
	@Test
	public void testRetrieveFileStoreInforamtion() throws IOException {
		FileSystem fs = FileSystems.getDefault();
		assertNotNull(fs);

		List<FileStore> stores = iterableToList(fs.getFileStores());

		// file store is device such a drive, partition or volume
		// in my system I have 2 partitions
		assertEquals(2, stores.size());

		// for storing triples of basic file store identification
		Set<String> storeNames = new HashSet<>();
		for (FileStore store : stores) {
			storeNames.add(store.name() + ";" + store.type() + ";"
					+ store.isReadOnly());
			long total = store.getTotalSpace();
			long unallocated = store.getUnallocatedSpace();
			long usable = store.getUsableSpace();
			assertTrue(total > 0);
			assertTrue(unallocated > 0 && total >= unallocated);
			assertTrue(usable > 0 && total >= usable);
			// Just for notice of NumberFormat class existence :)
			// System.out.println(NumberFormat.getInstance().format(total));
		}

		assertEquals(cSET("Windows7_OS;NTFS;false", "Data;NTFS;false"),
				storeNames);

		// can be used for determine if file store supports such attribute view
		assertTrue(stores.get(0).supportsFileAttributeView(
				BasicFileAttributeView.class));
		assertFalse(stores.get(0).supportsFileAttributeView(
				PosixFileAttributeView.class));
	}

	/**
	 * Filesystem ist trehad safe.
	 * 
	 */
	@Test
	public void testFilesystemInfo() {
		FileSystem fileSystem = FileSystems.getDefault();
		// som overloads
		// FileSystems.getFileSystem();
		// FileSystems.newFileSystem(Path, ClassLoader)

		// default FS is always open
		assertTrue(fileSystem.isOpen());
		assertFalse(fileSystem.isReadOnly());

		// can be changed for example by inserting usb memory stick
		List<Path> rootDirectories = iterableToList(fileSystem
				.getRootDirectories());
		assertEquals(
				Arrays.asList(Paths.get("C:\\"), Paths.get("D:\\"),
						Paths.get("E:\\"), Paths.get("F:\\")), rootDirectories);

		// fost of Files static methods delegate to this provider
		FileSystemProvider provider = fileSystem.provider();
		assertNotNull(provider);
	}

	/**
	 * Sample of SimpleFileVisitor usage. Examine conditional termination and
	 * DFS order of visiting.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFileVisitor() throws IOException {
		// sample dir structure for traversing
		Path rootDir = createDemonstrationDirectoryStructure();

		ExampleFileVisitor visitor = new ExampleFileVisitor();
		Files.walkFileTree(rootDir, visitor);
		// check callbacks count
		assertEquals(2, visitor.getPreDirsCalled());
		assertEquals(2, visitor.getPostDirsCalled());
		assertEquals(3, visitor.getFilesVisited());
		assertEquals(0, visitor.getNumOfFails());
		// examine callbacks call order
		assertEquals(Arrays.asList("PRE travelDir", "PRE childDir",
				"FILE grandChild.txt", "POST childDir", "FILE firstFile.txt",
				"FILE secondFile.txt", "POST travelDir"),
				visitor.getCallOrder());

		// restriction only for first root dir
		visitor.reset();
		Files.walkFileTree(rootDir, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
				0, visitor);
		// check callbacks count
		// only root dir visited and examined as file?
		assertEquals(0, visitor.getPreDirsCalled());
		assertEquals(0, visitor.getPostDirsCalled());
		assertEquals(1, visitor.getFilesVisited());
		assertEquals(0, visitor.getNumOfFails());
		// examine callbacks call order
		// root dir is handled as file, because no recursive nesting is executed
		assertEquals(Arrays.asList("FILE travelDir"), visitor.getCallOrder());

		// maxlevel 1
		// child directory is visited like file, but not intered into
		visitor.reset();
		Files.walkFileTree(rootDir, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
				1, visitor);
		// check callbacks count
		assertEquals(1, visitor.getPreDirsCalled());
		assertEquals(1, visitor.getPostDirsCalled());
		assertEquals(3, visitor.getFilesVisited());
		assertEquals(0, visitor.getNumOfFails());
		// examine callbacks call order
		assertEquals(Arrays.asList("PRE travelDir", "FILE childDir",
				"FILE firstFile.txt", "FILE secondFile.txt", "POST travelDir"),
				visitor.getCallOrder());

		// negative maxdepth
		visitor.reset();
		try {
			Files.walkFileTree(rootDir,
					EnumSet.of(FileVisitOption.FOLLOW_LINKS), -1, visitor);
			fail("IllegalArgumentException expected!");
		} catch (IllegalArgumentException e) {
		}

		// termination after first dir post processing
		Map<String, FileVisitResult> config = new HashMap<String, FileVisitResult>();
		config.put("postDir", FileVisitResult.TERMINATE);
		ExampleFileVisitor configuredVisitor = new ExampleFileVisitor(config);
		Files.walkFileTree(rootDir, configuredVisitor);
		// examine callbacks call order
		// after Post DIR is traversal terminated
		assertEquals(Arrays.asList("PRE travelDir", "PRE childDir",
				"FILE grandChild.txt", "POST childDir"),
				configuredVisitor.getCallOrder());

		// skip siblings on post dir
		config.clear();
		config.put("postDir", FileVisitResult.SKIP_SIBLINGS);
		configuredVisitor.reset();
		configuredVisitor.setConfiguration(config);

		Files.walkFileTree(rootDir, configuredVisitor);
		// examine callbacks call order
		// after Post DIR is traversal terminated
		assertEquals(Arrays.asList("PRE travelDir", "PRE childDir",
				"FILE grandChild.txt", "POST childDir", "POST travelDir"),
				configuredVisitor.getCallOrder());

		// skip siblings on pre dir
		config.clear();
		config.put("preDir", FileVisitResult.SKIP_SIBLINGS);
		configuredVisitor.reset();
		configuredVisitor.setConfiguration(config);

		Files.walkFileTree(rootDir, configuredVisitor);
		// examine callbacks call order
		// after Post DIR is traversal terminated
		assertEquals(Arrays.asList("PRE travelDir"),
				configuredVisitor.getCallOrder());
	}

	/**
	 * DirectoryStream tests. DirectoryStream is not recursive
	 */
	@Test
	public void testDirectoryStream() throws IOException {
		Path rootDir = createDemonstrationDirectoryStructure();
		DirectoryStream<Path> dirStream = Files.newDirectoryStream(rootDir);
		List<Path> paths = iterableToList(dirStream);
		// closing is required
		dirStream.close();

		// stream is not recursive!
		assertEquals(3, paths.size());
		assertTrue(paths.contains(rootDir.resolve("childDir")));
		assertTrue(paths.contains(rootDir.resolve("firstFile.txt")));
		assertTrue(paths.contains(rootDir.resolve("secondFile.txt")));

		// attempt to do something with closed directory stream
		try {
			dirStream.iterator();
			fail("IllegalStateException expected!");
		} catch (IllegalStateException e) {
		}

		// directory stream iterator doesnt support remove()
		try (DirectoryStream<Path> tmpStream = Files
				.newDirectoryStream(rootDir)) {
			tmpStream.iterator().remove();
			fail("UnsupportedOperationException expected!");
		} catch (UnsupportedOperationException e) {
		}
	}
	
	/**
	 * TEst how to determine if system support SecureDirectoryStream
	 * Dont understand what can be do with such stream.
	 * @throws IOException 
	 */
	@Test
	public void testSecureDirectoryStream() throws IOException {
		Path rootDir = createDemonstrationDirectoryStructure();
		DirectoryStream<Path> dirStream = Files.newDirectoryStream(rootDir);

		// in my system SecureDirectoryStream is not supported
		assertFalse(dirStream instanceof SecureDirectoryStream);
		
		// in doc is written that, SecureDirectoryStream can be obtained by casting
		// standard DirectoryStream to SecureDirectoryStrea. If cast fails, it means, 
		// SecureDirectoryStream is not supported in the system
		try {
			System.out.println((SecureDirectoryStream<Path>)dirStream);
			fail("ClassCastException expected!");
		} catch(ClassCastException e) {}
		
		dirStream.close();
	}

	/**
	 * Globbing is specifying DirectoryStream result with something like
	 * simplified regular expressions.
	 */
	@Test
	public void testDirectoryStreamGlobbing() throws IOException {
		Path rootDir = createDemonstrationDirectoryStructure();
		DirectoryStream<Path> dirStream = Files.newDirectoryStream(rootDir,
				"*.txt");
		List<Path> paths = iterableToList(dirStream);
		// closing is required
		dirStream.close();
		assertEquals(2, paths.size());
		assertTrue(paths.contains(rootDir.resolve("firstFile.txt")));
		assertTrue(paths.contains(rootDir.resolve("secondFile.txt")));

		// only dir
		dirStream = Files.newDirectoryStream(rootDir, "*Dir");
		paths = iterableToList(dirStream);
		dirStream.close();
		assertEquals(1, paths.size());
		assertTrue(paths.contains(rootDir.resolve("childDir")));

		// PathMatcher is used in loop
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher(
				"glob:*File*");
		dirStream = Files.newDirectoryStream(rootDir);
		int matchedFilenane = 0;
		int matchedPath = 0;
		for (Path p : dirStream) {
			if (matcher.matches(p.getFileName())) {
				matchedFilenane++;
			}

			if (matcher.matches(p)) {
				matchedPath++;
			}
		}
		dirStream.close();
		assertEquals(2, matchedFilenane);
		// don't work with whole path just with filename
		assertEquals(0, matchedPath);

		// path matcher with unrecognized prefix
		try {
			FileSystems.getDefault().getPathMatcher("gldob:*.txt");
			fail("UnsupportedOperationException expected!");
		} catch (UnsupportedOperationException e) {
		}

	}

	/**
	 * DirectoryStream.Filter is suitable for filtering based on other file
	 * attributes than file name (size, hidden..)
	 */
	@Test
	public void testDirectoryStreamFilter() throws IOException {
		Path rootDir = createDemonstrationDirectoryStructure();

		// filter accepts only directories
		DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
			@Override
			public boolean accept(Path entry) throws IOException {
				return Files.isDirectory(entry);
			}
		};

		DirectoryStream<Path> dirStream = Files.newDirectoryStream(rootDir,
				filter);
		List<Path> filteredPaths = iterableToList(dirStream);
		dirStream.close();

		// just single entry fulfill criteria
		assertEquals(1, filteredPaths.size());
		assertEquals(Paths.get("childDir"), filteredPaths.get(0).getFileName());
	}

	/**
	 * Watch changes on filesystem in directory. We can watch on changes inside
	 * directory. Better testable from other thread
	 */
	@Test
	public void testWatchFileEvents() throws IOException, InterruptedException {
		Path tmpDir = createWorkDirectory("tmpdir");

		// retrieve watch service from default FileSystem instance
		WatchService watchSvc = FileSystems.getDefault().newWatchService();
		tmpDir.register(watchSvc, StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_MODIFY,
				StandardWatchEventKinds.ENTRY_DELETE);

		Path textfile = createWorkFile("tmpdir", "someText.txt");
		// poll exists imediately witch reatrieved watch key or null when none
		// is available
		// take() call blocks until new watch key is available, can be used in
		// multiprocess or multithread
		WatchKey watchKey = watchSvc.poll();
		assertNotNull(watchKey);

		List<WatchEvent<?>> capturedEvents = watchKey.pollEvents();
		assertEquals(1, capturedEvents.size());
		WatchEvent<?> event = capturedEvents.get(0);
		assertEquals(StandardWatchEventKinds.ENTRY_CREATE, event.kind());
		assertEquals(1, event.count());
		// path between the directory registered with the watch service, and the
		// entry that is created, deleted, or modified.
		assertEquals(Paths.get("someText.txt"), event.context());
		// need reset
		watchKey.reset();
		// dont very understand when key is invalidated
		assertTrue(watchKey.isValid());

		// modify file
		modifyFile(textfile);
		// overload with time how long should wait for event
		watchKey = watchSvc.poll(1000, TimeUnit.MILLISECONDS);
		assertNotNull(watchKey);
		capturedEvents = watchKey.pollEvents();
		assertEquals(1, capturedEvents.size());
		event = capturedEvents.get(0);
		assertEquals(StandardWatchEventKinds.ENTRY_MODIFY, event.kind());
		assertEquals(1, event.count());
		assertEquals(Paths.get("someText.txt"), event.context());
		watchKey.reset();

		// delete file
		assertTrue(watchKey.isValid());
		Files.delete(textfile);
		// take is blocking call
		watchKey = watchSvc.take();
		assertNotNull(watchKey);
		capturedEvents = watchKey.pollEvents();
		assertEquals(1, capturedEvents.size());
		event = capturedEvents.get(0);
		assertEquals(StandardWatchEventKinds.ENTRY_DELETE, event.kind());
		assertEquals(1, event.count());
		assertEquals(Paths.get("someText.txt"), event.context());
		watchKey.reset();

		// create directory
		createWorkDirectory("tmpdir", "subdir");
		watchKey = watchSvc.take();
		assertNotNull(watchKey);
		capturedEvents = watchKey.pollEvents();
		assertEquals(1, capturedEvents.size());
		event = capturedEvents.get(0);
		assertEquals(StandardWatchEventKinds.ENTRY_CREATE, event.kind());
		assertEquals(1, event.count());
		assertEquals(Paths.get("subdir"), event.context());
		watchKey.reset();

		// create file inside subdir
		// watch dont work on child directory
		createWorkDirectory("tmpdir", "subdir", "mojmoj.txt");
		watchKey = watchSvc.poll();
		assertNull(watchKey);

		// closing watch sertvice
		watchSvc.close();
		try {
			watchSvc.poll();
			fail("ClosedWatchServiceException expected!");
		} catch (ClosedWatchServiceException e) {
		}

	}

	/**
	 * Example of usage of other than default filesystem
	 */
	@Test
	public void testZipFileSystem() throws IOException {
		Path zipFile = getFileKeeper().copyResource(Paths.get("archived.zip"));
		assertTrue(Files.exists(zipFile));
		
		// chooses ZipFileSystemProvider 
		FileSystem zipFS = FileSystems.newFileSystem(zipFile, null);
		assertNotNull("ZIP file system not installed", zipFS);
		
		Path root = zipFS.getPath("/");
		assertNotNull(root);
		assertTrue(Files.exists(root));
		
		List<Path> zipDirPaths = iterableToList(Files.newDirectoryStream(root));
		assertNotNull(zipDirPaths);
		// two files and one directory
		assertEquals(3, zipDirPaths.size());
		assertTrue(zipDirPaths.contains(zipFS.getPath("/mama.txt")));
		assertTrue(zipDirPaths.contains(zipFS.getPath("/dokument.docx")));
		assertTrue(zipDirPaths.contains(zipFS.getPath("/podpriecinok/")));
		// because Paths uses default filesystem
		assertFalse(zipDirPaths.contains(Paths.get("/mama.txt")));
		// file system need to be closed
		zipFS.close();
				
				
		// create new file archive
		// this dont work for me
//		Map<String, String> attrs = new HashMap<>();
//		attrs.put("create", "true"); // create of not exists, default is false
//		// another possible attribute is "encoding"
//		
//		
//		URI u = URI.create("zip:/test.zip");
//		Path p =Paths.get(u);
//		FileSystem tmpFS = FileSystems.newFileSystem(u, attrs);
//		
//		tmpFS.close();
//		getFileKeeper().markForWatch(emptyZip);
	}

	/**
	 * Returns Path object to root of created directory structure All created
	 * files and directories are marked for delete in proper order after test
	 * finish.
	 * 
	 * @return
	 */
	private Path createDemonstrationDirectoryStructure() throws IOException {
		Path rootDir = inTempDir("travelDir");
		rootDir = createWorkDirectory(rootDir);
		assertTrue(Files.exists(rootDir));
		getFileKeeper().markForWatch(rootDir);

		Path firsFile = createWorkFile(rootDir.resolve("firstFile.txt"));
		assertTrue(Files.exists(firsFile));
		getFileKeeper().markForWatch(firsFile);

		Path secondFile = createWorkFile(rootDir.resolve("secondFile.txt"));
		assertTrue(Files.exists(secondFile));
		getFileKeeper().markForWatch(secondFile);

		Path childDir = createWorkDirectory(rootDir.resolve("childDir"));
		assertTrue(Files.exists(childDir));
		getFileKeeper().markForWatch(childDir);

		Path grandChild = createWorkFile(childDir.resolve("grandChild.txt"));
		assertTrue(Files.exists(grandChild));
		getFileKeeper().markForWatch(grandChild);

		return rootDir;
	}

	private void modifyFile(Path file) throws IOException {
		assertTrue(Files.exists(file));

		// hopefully writer will be closed automatically (try-with-resources
		// blok)
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(
				file.toFile(), true))) {
			writer.write("ahoj");
			writer.flush();
		}
	}

	private <T> List<T> iterableToList(Iterable<T> iterable) {
		List<T> rv = new ArrayList<>();
		for (T t : iterable) {
			System.out.println(t);
			rv.add(t);
		}

		return rv;
	}
}
