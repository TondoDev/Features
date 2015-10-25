package org.tondo.Java7Features.paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author TondoDev
 *
 */
public class FileAttribtesTests {
	
	private static Path RESOURCE_PATH = null;
	private static Set<String> BASIC_FILE_ATTRIBUTES = null;
	
	@BeforeClass
	public static void init() {
		String currentDir = System.getProperty("user.dir");
		RESOURCE_PATH = Paths.get(currentDir, "src", "test", "resources", "SampleFiles");
		BASIC_FILE_ATTRIBUTES = new HashSet<>(Arrays.asList(
				"lastModifiedTime", "lastAccessTime",  
				"creationTime", 	"size", 
				"isRegularFile",	"isDirectory",
				"isSymbolicLink", 	"isOther", 		"fileKey"));
	}

	/**
	 * Testinb ability to determine file type by <code>Files.probeContentType()</code>,
	 * which returns MIME type as string
	 * Files are stored in src/test/resources/SampleFiles
	 */
	@Test
	public void testContentType() throws IOException {
		assertEquals("audio/mpeg", Files.probeContentType(getResourcePath("music.mp3")));
		assertEquals("application/pdf", Files.probeContentType(getResourcePath("Test PDF file.pdf")));
		assertEquals("text/plain", Files.probeContentType(getResourcePath("textFile.txt")));
		// this is for MS word DOCX pormat
		assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", Files.probeContentType(getResourcePath("wordFile.docx")));
		
		// for uknown type returns null
		assertNull(Files.probeContentType(getResourcePath("unknown.bin")));
		
		// for not existing file, returns null
		Path nonExisting = Paths.get("asdas");
		assertFalse("This file should not exists!", Files.exists(nonExisting));
		assertNull(Files.probeContentType(nonExisting));
	}

	/**
	 * Test for <code>Files.getAttribute(Path, attrName)</code> method.
	 * Supported attribute names: <br />
	 * <ul>
	 * 	<li>lastModifiedTime</li>
	 * 	<li>lastAccessTime</li>
	 * 	<li>creationTime</li>
	 * 	<li>size</li>
	 * 	<li>isRegularFile</li>
	 * 	<li>isDirectory</li>
	 * 	<li>isSymbolicLink</li>
	 * 	<li>isOther</li>
	 *	<li>fileKey</li>
	 * </ul>
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testFileAttribute() throws IOException {
		Path music = getResourcePath("music.mp3");
		Map<String, Object> attributes = Files.readAttributes(music, "*");
		
		assertEquals(BASIC_FILE_ATTRIBUTES, 
				attributes.keySet());
		
		Object lastModified = Files.getAttribute(music, "lastModifiedTime");
		assertTrue(lastModified instanceof FileTime);
		assertEquals(lastModified, attributes.get("lastModifiedTime"));
		assertEquals(lastModified, Files.getLastModifiedTime(music));
		
		Object lastAccessTime = Files.getAttribute(music, "lastAccessTime");
		assertTrue(lastAccessTime instanceof FileTime);
		assertEquals(lastAccessTime, attributes.get("lastAccessTime"));
		// Missing lastAccessTime in Files?
		
		Object creationTime = Files.getAttribute(music, "creationTime");
		assertTrue(creationTime instanceof FileTime);
		assertEquals(lastAccessTime, attributes.get("creationTime"));
		// Missing creationTime in Files?
		
		Object size = Files.getAttribute(music, "size");
		assertTrue(size instanceof Long);
		assertEquals(size, attributes.get("size"));
		assertEquals(size, Files.size(music));
		
		Object isRegularFile = Files.getAttribute(music, "isRegularFile");
		assertTrue(isRegularFile instanceof Boolean);
		assertEquals(isRegularFile, attributes.get("isRegularFile"));
		assertEquals(isRegularFile, Files.isRegularFile(music));
		
		Object isDirectory = Files.getAttribute(music, "isDirectory");
		assertTrue(isDirectory instanceof Boolean);
		assertEquals(isDirectory, attributes.get("isDirectory"));
		assertEquals(isDirectory, Files.isDirectory(music));
		
		Object isSymbolicLink = Files.getAttribute(music, "isSymbolicLink");
		assertTrue(isSymbolicLink instanceof Boolean);
		assertEquals(isSymbolicLink, attributes.get("isSymbolicLink"));
		assertEquals(isSymbolicLink, Files.isSymbolicLink(music));
		
		Object isOther = Files.getAttribute(music, "isOther");
		assertTrue(isOther instanceof Boolean);
		assertEquals(isOther, attributes.get("isOther"));
		// Missing other in Files?

		// object that uniquely identifies the given file, or null if a file key is not available (platform dependent)
		Object fileKey = Files.getAttribute(music, "fileKey");
		assertNull(fileKey);
		assertNull(attributes.get("fileKey"));
		
		// when name is mispelled exeption is thrown
		int exceptionsThrown = 0;
		try {
			Files.getAttribute(music, "aaa");
		} catch (IllegalArgumentException e) {
			exceptionsThrown++;
		}
		assertEquals(1, exceptionsThrown);
		
		try {
			Files.getAttribute(Paths.get("aaaa"), "size");
		} catch(NoSuchFileException e) {
			exceptionsThrown++;
		}
		assertEquals(2, exceptionsThrown);
		
		// when not existing path used and attribute is misspelled, IllegalArgumentExeption is thrown
		try {
			Files.getAttribute(Paths.get("aaaa"), "siize");
		} catch(IllegalArgumentException e) {
			exceptionsThrown++;
		}
		assertEquals(3, exceptionsThrown);
		
		// NOTE:  getAttribute() has optional argument LinkOption for behavior with symbolic links
		// NOTE: some method of Files class uses state "cannot be determined". For example:
		// Files.exists() returns false when file doesn't exists, or when cannot be determined if file exists
	}
	
	/**
	 * Test for accessing attributes providing view expression
	 * @throws IOException 
	 */
	@Test
	public void testAttributesViewSelection() throws IOException {
		Path music = getResourcePath("music.mp3");
		// all attributes from view
		Map<String, Object> attributes = Files.readAttributes(music, "basic:*");
		assertEquals(BASIC_FILE_ATTRIBUTES, attributes.keySet());
		
		// default view compared to basic view
		Map<String, Object> someBasicAttrs = Files.readAttributes(music, "basic:size,lastAccessTime");
		Map<String, Object> defaultViewAttrs = Files.readAttributes(music, "size,lastAccessTime");
		assertEquals(cSET("size", "lastAccessTime"), someBasicAttrs.keySet());
		assertEquals(someBasicAttrs, defaultViewAttrs);
		
		// exaples of other views
		assertEquals(cSET("owner", "acl"), Files.readAttributes(music, "acl:*").keySet());
		assertEquals(cSET("owner"), Files.readAttributes(music, "owner:*").keySet());
		// empty in my system
		assertTrue(Files.readAttributes(music, "user:*").keySet().isEmpty());
		assertEquals(cSET("lastModifiedTime", "lastAccessTime", "isSymbolicLink", "size", "system", "fileKey", "isDirectory", "hidden", "isOther", "isRegularFile", "creationTime", "attributes", "archive", "readonly"), Files.readAttributes(music, "dos:*").keySet());
		
		int exceptionsThrown = 0;
		// POSIX view doesn't work on my system
		try {
			Files.readAttributes(music, "posix:*");
		} catch (UnsupportedOperationException e) {
			exceptionsThrown++;
		}
		assertEquals(1, exceptionsThrown);
		
		// bad view name
		try {
			Files.readAttributes(music, "basaic:size,lastAccessTime");
		} catch (UnsupportedOperationException e) {
			exceptionsThrown++;
		}
		assertEquals(2, exceptionsThrown);
		
		// bad attr name 
		// notice different exceptions when view and attribute are misspelled
		try {
			Files.readAttributes(music, "basic:siize,lastAccessTime");
		} catch (IllegalArgumentException e) {
			exceptionsThrown++;
		}
		assertEquals(3, exceptionsThrown);
		
		// view name is case sensitive
		try {
			Files.readAttributes(music, "BASIC:size,lastAccessTime");
		} catch (UnsupportedOperationException e) {
			exceptionsThrown++;
		}
		assertEquals(4, exceptionsThrown);
		
		// attribute name is case sensitive
		try {
			Files.readAttributes(music, "basic:SIZE,lastAccessTime");
		} catch (IllegalArgumentException e) {
			exceptionsThrown++;
		}
		assertEquals(5, exceptionsThrown);
		
		// bad delimeter
		try {
			Files.readAttributes(music, "basic:size;lastAccessTime");
		} catch (IllegalArgumentException e) {
			exceptionsThrown++;
		}
		assertEquals(6, exceptionsThrown);
		
		// space between attributes
		try {
			Files.readAttributes(music, "basic:size, lastAccessTime");
		} catch (IllegalArgumentException e) {
			exceptionsThrown++;
		}
		assertEquals(7, exceptionsThrown);
	}
	
	/**
	 * Test for demonstrating which attribute views are supported on current platform
	 */
	@Test
	public void testAttributeViewsSupport() {
		FileSystem fs = FileSystems.getDefault();
		
		assertEquals(cSET("acl", "basic", "owner", "user", "dos"), fs.supportedFileAttributeViews());
		
		// just retrieve som file store
		FileStore store = fs.getFileStores().iterator().next();
		
		// supported
		assertTrue(store.supportsFileAttributeView("acl"));
		assertTrue(store.supportsFileAttributeView(AclFileAttributeView.class));
		
		// not supported
		assertFalse(store.supportsFileAttributeView("posix"));
		assertFalse(store.supportsFileAttributeView(PosixFileAttributeView.class));
		
		// not existing
		assertFalse(store.supportsFileAttributeView("gugu"));
	}
	
	/**
	 * demonstrating what file store is. It is something like logical disk volue
	 */
	@Test
	public void testFileStores() {
		Set<String> volumes = new HashSet<>();
		
		for (FileStore store : FileSystems.getDefault().getFileStores()) {
			volumes.add(store.name());
		}
		
		// in my system
		assertEquals(cSET("Data","Windows7_OS"), volumes);
	}
	
	
	@Test
	public void testBasicFileAttributeView() throws IOException {
		Path music = getResourcePath("music.mp3");
		
		// directly access attributes
		BasicFileAttributes basicAttrs = Files.readAttributes(music, BasicFileAttributes.class);
		assertTrue(basicAttrs != null);
		
		// same set of methods...
		assertTrue(basicAttrs.isRegularFile());
		assertFalse(basicAttrs.isDirectory());
		// is not regular file, nor directory, nor sym link
		assertFalse(basicAttrs.isOther());
		
		
		// using attribute view
		BasicFileAttributeView basicView = Files.getFileAttributeView(music, BasicFileAttributeView.class);
		assertTrue(basicView != null);
		assertEquals("basic", basicView.name());
		// not same reference
		assertTrue(basicAttrs != basicView.readAttributes());
		assertEquals(basicAttrs.isRegularFile(), basicView.readAttributes().isRegularFile());
		assertEquals(basicAttrs.size(), basicView.readAttributes().size());
		
		// each next call returns new instance of attributes
		assertTrue(basicView.readAttributes() != basicView.readAttributes());
		
		// bad class
		try {
			Files.readAttributes(music, PosixFileAttributes.class);
			fail("UnsupportedOperationException exception expected!");
		} catch (UnsupportedOperationException e) {
		}
		
		// TODO sample of BasicFileAttributeView.setTimes()
		
	}
	
	/**
	 * Quite limited, but useful when archive or system file flags are needed
	 * DosFileAttribtutes extends BasicFileAttributes
	 * @throws IOException 
	 */
	@Test
	public void testFatAttribtesView() throws IOException {
		Path music = getResourcePath("music.mp3");
		// atributes
		DosFileAttributes dosAttrs = Files.readAttributes(music, DosFileAttributes.class);
		assertTrue(dosAttrs != null);
		
		assertTrue(dosAttrs.isArchive());
		assertFalse(dosAttrs.isHidden());
		assertFalse(dosAttrs.isReadOnly());
		assertFalse(dosAttrs.isSystem());
		
		// view
		DosFileAttributeView dosView = Files.getFileAttributeView(music, DosFileAttributeView.class);
		assertTrue(dosView != null);
		DosFileAttributes attrsFromView = dosView.readAttributes();
		assertEquals(dosAttrs.isArchive(), attrsFromView.isArchive());
		assertEquals(dosAttrs.isHidden(), attrsFromView.isHidden());
		assertEquals(dosAttrs.isReadOnly(), attrsFromView.isReadOnly());
		assertEquals(dosAttrs.isSystem(), attrsFromView.isSystem());
		
		// TODO try view methods when copy of files will be available
		// .setArchive()
		// .setHidden()
		// .setReadOnly()
		// .setSystem()
		
	}
	
	/**
	 *	Owner doesn't have attribute class, it has only view class with important method {@code getOwner()} 
	 */
	@Test
	public void testOwnerAttributeView() throws IOException {
		Path music = getResourcePath("music.mp3");
		FileOwnerAttributeView ownerView = Files.getFileAttributeView(music, FileOwnerAttributeView.class);
		
		assertTrue(ownerView != null);
		assertEquals("owner", ownerView.name());
		
		UserPrincipal principal = ownerView.getOwner();
		assertTrue(principal != null);
		
		assertEquals("ANTONZSIROS-PC\\Anton Zsíros", principal.getName());
	}
	
	/**
	 * AclFilaAttributeView extends FileOwnerAttributeView
	 * @throws IOException 
	 */
	@Test
	public void testAclAttributesView() throws IOException {
		Path music = getResourcePath("music.mp3");
		AclFileAttributeView aclView = Files.getFileAttributeView(music, AclFileAttributeView.class);
		assertNotNull(aclView);
		assertEquals("acl",aclView.name());
		// inherited from FileOwnerAttributeView
		assertEquals("ANTONZSIROS-PC\\Anton Zsíros", aclView.getOwner().getName());
		List<AclEntry> aclEntries = aclView.getAcl();
		assertEquals(4, aclEntries.size());
		
		for (AclEntry entry : aclEntries) {
			System.out.println(entry.toString());
			
			System.out.println("Principal: " + entry.principal());
			System.out.println("Type: " + entry.type());

			for (AclEntryPermission permission : entry.permissions()) {
				System.out.println(permission);
			}
			

			System.out.println("Flags: ");
			for (AclEntryFlag flag : entry.flags()) {
				System.out.println(flag);
			}
			System.out.println("==");
		}
	}
	
	@Test
	public void testUserDefinedAttributes() throws IOException {
		Path music = getResourcePath("music.mp3");
		Path workFile = Paths.get("workFile");
		Files.copy(music, workFile, StandardCopyOption.REPLACE_EXISTING);
		assertTrue(Files.exists(workFile));
		
		UserDefinedFileAttributeView userView = Files.getFileAttributeView(workFile, UserDefinedFileAttributeView.class);
		assertNotNull(userView);
		// currently no attached user defined attributes
		assertEquals(0, userView.list().size());
		final String attrName = "tono";
		// create custom attribute
		userView.write(attrName, Charset.defaultCharset().encode("SLOVENSKO"));
		assertEquals(1, userView.list().size());
		// on my system default encoding
		assertEquals(9, userView.size(attrName));
		
		// reading custom attribute value
		ByteBuffer buffer = ByteBuffer.allocate(userView.size(attrName));
		int readBytes = userView.read(attrName, buffer);
		// rest buffer??
		buffer.flip();
		assertEquals(9, readBytes);
		assertEquals("SLOVENSKO", Charset.defaultCharset().decode(buffer).toString());
		
		// removing attribute
		userView.delete(attrName);
		assertEquals(0, userView.list().size());
		
		// unknown attribute
		try {
			userView.size(attrName);
			fail("NoSuchFileException exception expected!");
		} catch (NoSuchFileException e) {}
	}
	
	
	/**
	 * Returns Path object for named file.
	 * File should exists in test resources directory, otherwises assertion error is thrown
	 * @param resource
	 * 		file for which path object is returned
	 * @return
	 * 		Path object for file
	 */
	private Path getResourcePath(String resource) {
		Path resourcePath = RESOURCE_PATH.resolve(resource);
		assertTrue("File don't exists",Files.exists(resourcePath));
		return resourcePath;
	}
	
	@SuppressWarnings("unchecked")
	private <T> Set<T> cSET(T... items) {
		Set<T> rv = new HashSet<>();
		Collections.addAll(rv, items);
		return rv;
	}
}
