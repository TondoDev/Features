package org.tondo.Java7Features.paths;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Calendar;
import java.util.Set;

import org.junit.Test;
import org.tondo.Java7Features.JavaFeaturesTestBase;

/**
 * 
 * @author TondoDev
 *
 */
public class FileAndDirsManipulationTest  extends JavaFeaturesTestBase{

	/**
	 * Basic test for creating files 
	 */
	@Test
	public void testCreateFile() throws IOException {
		Path fileToCreate = inTempDir("ahoj");
		assertFalse(Files.exists(fileToCreate));
		
		// create file
		Path created = Files.createFile(fileToCreate);
		assertTrue(Files.exists(created));
		assertEquals(fileToCreate, created);
		
		getFileKeeper().markForWatch(created);
		
		// creating existing file throws exception
		try {
			created = Files.createFile(fileToCreate);
			fail("FileAlreadyExistsException exception expected!");
		} catch (FileAlreadyExistsException e) {}
	}
	
	/**
	 * Basic test sample for demonstration of creating directories 
	 */
	@Test
	public void testCreateDirectory() throws IOException {
		Path dirToCreate = inTempDir("ahojDir");
		assertFalse(Files.exists(dirToCreate));
		
		Path createdDir = Files.createDirectory(dirToCreate);
		assertNotNull(createdDir);
		assertTrue(Files.exists(createdDir));
		assertEquals(dirToCreate, createdDir);
		
		// attempt to create existing directory
		try {
			Files.createDirectory(dirToCreate);
			fail("FileAlreadyExistsException exception expected");
		} catch(FileAlreadyExistsException e) {}
		
		getFileKeeper().markForWatch(createdDir);
		
		// all superordinated directories must exists in order to
		// create most nested directory
		try {
			Files.createDirectory(inTempDir("otec", "decko"));
			fail("NoSuchFileException  exception expected");
		} catch (NoSuchFileException e) {}
		
		// for creating hierarchy of directories at once
		Path hierarchy = inTempDir("otec", "decko");
		Path createdHierary = Files.createDirectories(hierarchy);
		assertNotNull(createdHierary);
		assertTrue(Files.exists(createdHierary));
		assertEquals(hierarchy, createdHierary);
		
		// mark child and parent, will be deleted in reversed order
		getFileKeeper().markForWatch(createdHierary.getParent());
		getFileKeeper().markForWatch(createdHierary);
	}
	
	/**
	 * Test demonstration basic file copy using {@code Files} class 
	 */
	@Test
	public void testCopyFile() throws IOException {
		Path source = inResourceDir("music.mp3");
		Path target = inTempDir("copiedMusic.mp3");
		
		Path copiedFile = Files.copy(source, target);
		assertNotNull(copiedFile);
		assertTrue(Files.exists(copiedFile));
		assertEquals(target, copiedFile);
		
		getFileKeeper().markForWatch(copiedFile);
		
		// by default copy() crash if target file already exists 
		Path otherSource = inResourceDir("wordFile.docx");
		try {
			Files.copy(otherSource, target);
			fail("FileAlreadyExistsException exception expected!");
		} catch (FileAlreadyExistsException e) {}
		
		
		// copy option needs to be specified for overwriting existing files
		copiedFile = Files.copy(otherSource, target, StandardCopyOption.REPLACE_EXISTING);
		// NOTE: overwriting does not change created time of file
		
		// for ensure that copied file overwrite exiting file (different size expecting)
		Object sourceSize = Files.getAttribute(otherSource, "size");
		assertNotNull(sourceSize);
		Object targetSize = Files.getAttribute(copiedFile, "size");
		assertEquals(sourceSize, targetSize);
		
		getFileKeeper().markForWatch(copiedFile);
		
		// more overloads
		// copy from input stream, 
		// copy to output stream
		// copy directory - files from original directory are not copied
	}
	
	@Test
	public void testTempDirectoryAndFiles() throws IOException {
		
		// create temp file in default system location for temporary files
		Path tempFile = Files.createTempFile("", "");
		assertNotNull(tempFile);
		assertTrue(Files.exists(tempFile));
		// if really created in system default location for temporary files
		// in my system it looks like C:\Users\ANTONZ~1\AppData\Local\Temp\7925128612627575292
		assertEquals(Paths.get(System.getProperty("java.io.tmpdir")), tempFile.getParent());
		
		// temp file can have prefix and suffix
		// but directory location must be specified
		Path tempFilePrefix = Files.createTempFile(getTempLocation(), "PREF", "SUFF");
		assertNotNull(tempFilePrefix);
		assertTrue(Files.exists(tempFilePrefix));
		String prefixedTempName = tempFilePrefix.getFileName().toString();
		assertTrue(prefixedTempName.startsWith("PREF") && prefixedTempName.endsWith("SUFF"));
		
		// Temp files are not deleted automatically,
		// shutdown hook is required, or opened with flag  DELETE_ON_CLOSE
		new File(prefixedTempName).deleteOnExit();
		getFileKeeper().markForWatch(tempFile);
		
		// temp directories are created with createTemDirectory() method
	}
	
	
	/**
	 * Accessing and modifying time attributes of file
	 * @throws IOException 
	 */
	@Test
	public void testTimeAttributes() throws IOException {
		// just for ensure if we are working on clean environment
		String fileName = "Test PDF file.pdf";
		Path sourcePath = inTempDir(fileName);
		assertFalse("Remove file " + sourcePath + " for run test from clean state", Files.exists(sourcePath));
		
		
		Path workFile = getFileKeeper().copyResource(inResourceDir(fileName));
		assertNotNull(workFile);
		assertTrue(Files.exists(workFile));
		
		BasicFileAttributes fileAttrs = Files.readAttributes(workFile, BasicFileAttributes.class);
		assertNotNull(fileAttrs);
		
		// just wondering if always new instance of FileTime is returned
		assertEquals(fileAttrs.lastModifiedTime(), fileAttrs.lastModifiedTime());
		assertNotSame(fileAttrs.lastModifiedTime(), fileAttrs.lastModifiedTime());
		
		// copied file shoud have all times properties in current year
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
	
		assertEquals(currentYear, fromFileTime(fileAttrs.lastModifiedTime()).get(Calendar.YEAR));
		assertEquals(currentYear, fromFileTime(fileAttrs.lastAccessTime()).get(Calendar.YEAR));
		assertEquals(currentYear, fromFileTime(fileAttrs.creationTime()).get(Calendar.YEAR));
		
		Calendar somewhereInPast = Calendar.getInstance();
		somewhereInPast.set(Calendar.YEAR, currentYear - 1);
		// static method for crreating FileTime
		FileTime lmt = FileTime.fromMillis(somewhereInPast.getTimeInMillis());
		somewhereInPast.set(Calendar.YEAR, currentYear - 2);
		FileTime lat = FileTime.fromMillis(somewhereInPast.getTimeInMillis());
		somewhereInPast.set(Calendar.YEAR, currentYear - 3);
		FileTime ct = FileTime.fromMillis(somewhereInPast.getTimeInMillis());
		
		// store times using BasicFileAttributeView
		BasicFileAttributeView view = Files.getFileAttributeView(workFile, BasicFileAttributeView.class);
		view.setTimes(lmt, lat, ct);
		
		// we need to call readAttributes() again (for demonstration purposes from BasiFileAttributeView)
		BasicFileAttributes updatedAttrs = view.readAttributes();
		assertEquals(currentYear - 1, fromFileTime(updatedAttrs.lastModifiedTime()).get(Calendar.YEAR));
		assertEquals(currentYear - 2, fromFileTime(updatedAttrs.lastAccessTime()).get(Calendar.YEAR));
		assertEquals(currentYear -3 , fromFileTime(updatedAttrs.creationTime()).get(Calendar.YEAR));
	}
	
	@Test
	public void testUserPrincipalLookup() throws IOException {
		// TODO investigate classes
		// AclFileAttributeView
		// AclEntry
		// AclEntry.Builder
		UserPrincipalLookupService lookupSvc = FileSystems.getDefault().getUserPrincipalLookupService();
		assertEquals("NT AUTHORITY\\SYSTEM (Well-known group)", lookupSvc.lookupPrincipalByName("SYSTEM").toString());
	}
	
	@Test
	public void testPosixFilePermissions() {
		// POSIX view is not supported on my system (WIN 7)
		// PosixFileAttributeView
		// PosixFileAttributes
		Set<PosixFilePermission> permissionsSet = cSET(
					PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE, 
					PosixFilePermission.GROUP_READ, 
					PosixFilePermission.OTHERS_EXECUTE, PosixFilePermission.OTHERS_READ);
		
		assertEquals(permissionsSet, PosixFilePermissions.fromString("rwxr--r-x"));
		assertEquals("rwxr--r-x", PosixFilePermissions.toString(permissionsSet));
	}
	
	
	/**
	 * Examples of moving files
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testMovingFiles() throws IOException, InterruptedException {
		Path music = this.getFileKeeper().copyResource(inResourceDir("music.mp3"));
		assertExists(music);
		getFileKeeper().markForWatch(music);
		
		Path targetFolder = createWorkDirectory("workDir");
		assertExists(targetFolder);
		
		// attempt to replace directory with file
		try {
			Files.move(music, targetFolder);
			fail("FileAlreadyExistsException exception expected!");
		} catch (FileAlreadyExistsException e) {}
		
		// moving to target location
		Path moved = Files.move(music, targetFolder.resolve("music.mp3"));
		getFileKeeper().markForWatch(moved);
		assertExists(moved);
		assertNotExists(music);
		
		// rename
		Path renamed = Files.move(moved, moved.resolveSibling("newName.mp3"));
		getFileKeeper().markForWatch(renamed);
		assertExists(renamed);
		assertNotExists(moved);
		
		Path replacer = createWorkFile("replacer");
		assertExists(replacer);
		
		// move to destination, where file with such name already exists
		try {
			Files.move(replacer, renamed);
			fail("FileAlreadyExistsException exception expected!");
		} catch (FileAlreadyExistsException e) {}
		
		// soruce file shoud stay unchanged
		assertExists(replacer);
		
		// move with rewrite option
		Files.move(replacer, renamed, StandardCopyOption.REPLACE_EXISTING);
		assertNotExists(replacer);
		assertExists(renamed);
		
	}
	
	/**
	 * Examples of moving directories.
	 * Directory is moved also with its content
	 * @throws IOException
	 */
	@Test
	public void testMovingDirectory() throws IOException {
		final String fileName = "music.mp3"; 
		
		// here will be moved directory stored
		Path targetDir = createWorkDirectory("workDir");
		getFileKeeper().markForWatch(targetDir);
		assertExists(targetDir);
		
		// directory which will be moved
		Path sourceDir = createWorkDirectory("sourceDir");
		getFileKeeper().markForWatch(sourceDir);
		assertExists(sourceDir);
		// copy one file into directory
		getFileKeeper().copyResource(inResourceDir(fileName), sourceDir.resolve(fileName));
		
		// move directory with file inside
		Path moved = Files.move(sourceDir, targetDir.resolve(sourceDir.getFileName()));
		getFileKeeper().markForWatch(moved);
		getFileKeeper().markForWatch(moved.resolve(fileName));
		// direcotry is moved with file
		assertExists(moved);
		assertExists(moved.resolve(fileName));
		assertNotExists(sourceDir);
	}
	
	/**
	 * Deleting files using {@link Files} utility methods
	 * @throws IOException 
	 */
	@Test
	public void testDeleteFile() throws IOException {
		Path workFile = createWorkFile("notNeeded.txt");
		assertExists(workFile);
		
		// deleting file
		Files.delete(workFile);
		assertNotExists(workFile);
		
		// deleting not existing file
		Path nonExisting = Paths.get("halakaBalaka");
		assertNotExists(nonExisting);
		// throws exception
		try {
			Files.delete(nonExisting);
			fail("NoSuchFileException expected!");
		} catch (NoSuchFileException e) {}
		
		// alternative if we don't want to check if file exists
		boolean deleted = Files.deleteIfExists(nonExisting);
		assertFalse(deleted);
		// using deleteIfExists with existing file, return value shoudl be true
		Path otherFile = createWorkFile("other.txt");
		assertExists(otherFile);
		boolean otherDelete = Files.deleteIfExists(otherFile);
		assertNotExists(otherFile);
		assertTrue(otherDelete);
	}
	
	@Test
	public void testDeletingDirectory() throws IOException {
		final String dirName = "NotNeeded";
		Path notNeededDir = createWorkDirectory(dirName);
		assertExists(notNeededDir);
		
		Files.delete(notNeededDir);
		assertNotExists(notNeededDir);
		
		// deleting non-empty directory
		Path nonEmptyDir = createWorkDirectory(dirName);
		Path fileInside  = createWorkFile(dirName, "something.txt");
		getFileKeeper().markForWatch(fileInside);
		getFileKeeper().markForWatch(nonEmptyDir);
		
		// deleting not empty dir fail.
		// At wirst all content must be deleted (SimpleFileVisitor)
		try {
			Files.delete(nonEmptyDir);
			fail("DirectoryNotEmptyException expected");
		} catch (DirectoryNotEmptyException e) {}		
	}
	
	@Test
	public void testSoftLinks() throws IOException {
		Path musicTarget = getFileKeeper().copyResource(inResourceDir("music.mp3"));
		assertExists(musicTarget);
		
		String workDirName = "wrokWork";
		Path workDir = createWorkDirectory(workDirName);
		assertExists(workDir);
		
		// create soft symbolic link - crashing on my system (missing privilegues for user for creatign symlinks)
		String linkName = "linkMusic";
		try {
			Files.createSymbolicLink(inTempDir(workDirName, linkName), musicTarget);
			fail("FileSystemException expected!");
		} catch (FileSystemException e) {}
		
		// try at least isSymbilicLink()
		// readSymbolicLink - finding target
//		Path wordFile = inResourceDir("wordFile.docx");
//		Path linkFile = inResourceDir("aaa.lnk");
//		// this doesnt work
//		assertTrue(Files.exists(linkFile));
//		assertTrue(Files.isSymbolicLink(linkFile));
//		assertFalse(Files.isSymbolicLink(wordFile));
//		
//		// resolving target
//		Path target = Files.readSymbolicLink(linkFile);
//		assertNotNull(target);
//		assertEquals(wordFile, target);
	}
	
	private Calendar fromFileTime(FileTime ft) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ft.toMillis());
		return cal;
	}
	
}

