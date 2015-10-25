package org.tondo.Java7Features.paths;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class PathTest {

	/**
	 * Test of behavior of basic Path methods
	 */
	@Test
	public void testPathMethodsUnix() {
		Path path = FileSystems.getDefault().getPath("/home/tondo/zsiros/pokec");
		assertEquals(4, path.getNameCount());
		// ? why
		assertFalse(path.isAbsolute());
		// mention that path separators are from current system
		assertEquals("\\home\\tondo\\zsiros", path.getParent().toString());
		assertEquals("zsiros", path.getName(2).toString());
		assertEquals("\\", path.getRoot().toString());
		assertEquals("\\home\\tondo\\zsiros\\pokec", path.toString());
		assertEquals("pokec", path.getFileName().toString());
		assertEquals("home\\tondo", path.subpath(0, 2).toString());
		assertEquals("home\\tondo\\zsiros\\pokec", path.subpath(0, path.getNameCount()).toString());
		assertEquals("tondo", path.subpath(1, 2).toString());
		// absolute path is created with adding drive letter (system specific)
		// path doesn't have to exist
		assertEquals("D:\\home\\tondo\\zsiros\\pokec", path.toAbsolutePath().toString());
		
		//--------------------------------------
		
		Path path2 = FileSystems.getDefault().getPath("zajac/pes/macka/");
		assertEquals(3, path2.getNameCount());
		// ? why
		assertFalse(path.isAbsolute());
		assertEquals("zajac\\pes", path2.getParent().toString());
		// path doens't have root element
		assertNull(path2.getRoot());
		// without last slash?
		assertEquals("macka", path2.getFileName().toString());
		assertEquals("pes\\macka", path2.subpath(1, path2.getNameCount()).toString());
		// if path not begin with slash, absolute path is created by adding CWD to path
		assertEquals("D:\\Projekty\\Java\\fxWS\\Java7Features\\zajac\\pes\\macka", path2.toAbsolutePath().toString());
		
		// path with space
		Path withSpace = Paths.get("/c/pracovna plocha/jazer");
		assertEquals("\\c\\pracovna plocha\\jazer", withSpace.toString());
		
		// create path by parts
		Path fromParts = Paths.get("ahoj", "svet", "skaredy");
		assertEquals("ahoj\\svet\\skaredy", fromParts.toString());
		
		// path file only
		Path fileOnly = Paths.get("slnko");
		assertNull(fileOnly.getRoot());
		assertNull(fileOnly.getParent());
		assertFalse(path.isAbsolute());
		
		assertNull(Paths.get("/").getParent());
		assertEquals(Paths.get("/"), Paths.get("/").getRoot());
		
		// last slash sensitivity
		assertEquals(Paths.get("ahoj/slnko"), Paths.get("ahoj/slnko/"));
		// case sensitivity - may be OS dependent
		assertEquals(Paths.get("ahoj/slnko"), Paths.get("aHOJ/SLNKO"));
		
		// doubled slash in path
		assertEquals("ahoj\\slnko", Paths.get("ahoj//slnko").toString());
		assertEquals("\\\\ahoj\\slnko\\", Paths.get("///ahoj//slnko").toString());
		assertEquals("\\\\ahoj\\slnko\\", Paths.get("//ahoj//slnko").toString());
		
		assertEquals(Paths.get("C:\\"), Paths.get("C:\\dobry\\den").getRoot());
	}
	
	/**
	 * Creating absolute paths
	 */
	@Test
	public void testAbsolutePath() {
		assertFalse(Paths.get("BA/KE/ZA").isAbsolute());
		
		assertTrue(Paths.get("//BA/KE/ZA").isAbsolute());
		assertTrue(Paths.get("R:/BA/KE/ZA").isAbsolute());
		
		Path path = Paths.get("//BA/KE/ZA");
		Path absPath = path.toAbsolutePath();
		assertEquals(path, absPath);
		// same instance
		assertTrue(path == absPath);
		
		// is location dependent, but file is not checked if exists
		String currentDir = System.getProperty("user.dir");
		String rootDir = Paths.get(currentDir).getRoot().toString();
		assertEquals(currentDir, Paths.get("").toAbsolutePath().toString());
		assertEquals(Paths.get(currentDir).getRoot(), Paths.get("/").toAbsolutePath());
		assertEquals(rootDir + "ahoj\\ako", Paths.get("/ahoj/ako").toAbsolutePath().toString());
		assertEquals(currentDir + "\\ahoj\\ako", Paths.get("ahoj/ako").toAbsolutePath().toString());
		
	}

	/**
	 * Test case for cases when Paths creation fails
	 */
	@Test
	public void testBadPathCharacter() {
		int exceptionsRisen = 0;
		
		try {
			Paths.get("path/with/*star");
		} catch (InvalidPathException e) {
			exceptionsRisen++;
		}
		
		try {
			Paths.get("invalid/Charact\0erpath");
		} catch (InvalidPathException e) {
			exceptionsRisen++;
		}
		
		try {
			Paths.get("//");
		} catch (InvalidPathException e) {
			exceptionsRisen++;
		}
		
		try {
			Paths.get(":dsd");
		} catch (InvalidPathException e) {
			exceptionsRisen++;
		}
		
		try {
			Paths.get("//..");
		} catch (InvalidPathException e) {
			exceptionsRisen++;
		}
		
		try {
			Paths.get("//.");
		} catch (InvalidPathException e) {
			exceptionsRisen++;
		}
		
		try {
			Paths.get(null);
		} catch (NullPointerException e) {
			exceptionsRisen++;
		}
		
		assertEquals(7, exceptionsRisen);
	}
	
	/**
	 * toRealPath() check real filesystem if file exists.
	 * Uses toAbsolutePath()
	 * Returned path is normalized and symbolic lincs are handled (according to arguments)
	 */
	@Test
	public void testToAbsolutePath() {
		// we can use Files.exists() method to check if file exists on file system
		
		Path nonExistingPath = Paths.get("ahoj");
		assertFalse(Files.exists(nonExistingPath));
		boolean exceptionCaught = false;
		try {
			nonExistingPath.toRealPath();
		} catch (IOException e) {
			exceptionCaught = e instanceof NoSuchFileException;
		}
		assertTrue(exceptionCaught);
		
		
		// existing file, this is location dependent
		Path existing = null;
		try {
			existing = Paths.get("pom.xml").toRealPath();
		} catch (IOException e) {
			existing = null;
		}
		
		assertNotNull(existing);
		assertEquals("D:\\Projekty\\Java\\fxWS\\Java7Features\\pom.xml", existing.toString()); 
		assertTrue(Files.exists(existing));
		
		try {
			existing = Paths.get("../ID3/").toRealPath();
		} catch (IOException e) {
			existing = null;
		}
		
		assertNotNull(existing);
		// real path is normalized
		assertEquals("D:\\Projekty\\Java\\fxWS\\ID3", existing.toString()); 
	}
	
	/**
	 * Test demonstration normalizing functionality
	 */
	@Test
	public void testNormalizingPaths() {
		// with parent directory ..
		Path redundand = Paths.get("/aaa/bbb/../cc/ahoj");
		assertEquals("\\aaa\\bbb\\..\\cc\\ahoj", redundand.toString());
		// after normalization
		assertEquals("\\aaa\\cc\\ahoj", redundand.normalize().toString());
		
		// original and normalized are not "equals()"
		Path normalized = redundand.normalize();
		assertNotEquals(redundand, normalized);
		
		// normalization without effect
		Path orig = Paths.get("/ahoj/svet/");
		Path norm = orig.normalize();
		assertEquals(orig, norm);
		assertTrue(orig == norm);
		
		// with dot (current directory)
		assertEquals("prsi\\len\\sa\\leje", Paths.get("prsi/len/./sa/leje").normalize().toString());
		assertEquals("prsi\\len\\leje", Paths.get("prsi/len/./sa/../leje").normalize().toString());
		assertEquals("leje", Paths.get("prsi/len/../../leje").normalize().toString());
		
		// some weird cases
		assertEquals("", Paths.get(".").normalize().toString());
		assertEquals("..", Paths.get("..").normalize().toString());
		assertEquals("...", Paths.get("...").normalize().toString());
		assertEquals("..", Paths.get("./..").normalize().toString());
		assertEquals("..", Paths.get("../.").normalize().toString());
		
		assertEquals("\\", Paths.get("/.").normalize().toString());
		assertEquals("\\", Paths.get("/..").normalize().toString());
		assertEquals("\\...", Paths.get("/...").normalize().toString());
		assertEquals("\\", Paths.get("/./..").normalize().toString());
		assertEquals("\\", Paths.get("/../.").normalize().toString());
		
		assertEquals("..", Paths.get("../").normalize().toString());
		assertEquals("", Paths.get("a/../").normalize().toString());
		assertEquals("", Paths.get("a/..").normalize().toString());
		assertEquals("\\", Paths.get("/a/..").normalize().toString());
		assertEquals("\\a", Paths.get("/../a").normalize().toString());
		assertEquals("..\\a", Paths.get("../a").normalize().toString());
		assertEquals("\\\\..\\a\\", Paths.get("//../a").normalize().toString());
		assertEquals("\\\\a\\..\\", Paths.get("//a/..").normalize().toString());
		assertEquals("C:\\", Paths.get("C:\\..").normalize().toString());
	}
	
	/**
	 * Something like concatenation of paths.
	 * Demenostrated behaviour when one path is root, second path is subpath
	 * and combinations between them
	 */
	@Test
	public void testResolve() {
		// if root is absolute, and subpath not, its "concatenation" is returned
		Path root = Paths.get("/ahoj/slko");
		Path subpath = Paths.get("dnes/pekne");
		assertEquals("\\ahoj\\slko\\dnes\\pekne", root.resolve(subpath).toString());
		
		// if parameter is absolute, parameter paths is returned
		assertEquals(root, subpath.resolve(root));
		assertFalse(root == subpath.resolve(root));
		
		assertEquals(root, root.resolve(Paths.get("")));
		assertEquals(root, Paths.get("").resolve(root));
		assertEquals(Paths.get(""), Paths.get("").resolve(Paths.get("")));
		
		Path secRoot = Paths.get("/fc/barca");
		Path secSub = Paths.get("praci/prasok");
		
		// two  paths with root
		assertEquals(root, secRoot.resolve(root));
		assertEquals(secRoot, root.resolve(secRoot));
		
		// two relative paths
		assertEquals(Paths.get("praci/prasok/dnes/pekne"), secSub.resolve(subpath));
		assertEquals(Paths.get("dnes/pekne/praci/prasok"), subpath.resolve(secSub));
		
		// resolve has overload with string argument
		assertEquals(Paths.get("/dnes/je/pondelok"), Paths.get("/dnes/je").resolve("pondelok"));
		
		// files only
		assertEquals(Paths.get("piatok/trinasteho"), Paths.get("piatok").resolve("trinasteho"));
		
		// absolute path
		Path absPath = Paths.get("C:\\dnes\\je");
		assertEquals(Paths.get("C:\\dnes\\je\\pondelok"), absPath.resolve("pondelok"));
		assertEquals(absPath, Paths.get("pondelok").resolve(absPath));
	}
	
	/**
	 * Similar like resolve() but it replaces last part of "parent"
	 * with first part of "apendee"
	 */
	@Test
	public void testResolveSiblingMethod() {
		Path root = Paths.get("/ahoj/slnko");
		Path subpath = Paths.get("dnes/pekne");
		Path secRoot = Paths.get("/fc/barca");
		Path secSub = Paths.get("praci/prasok");
		Path parentless = Paths.get("nedela");
		Path absolute = Paths.get("C:\\dobre\\rano");
		Path empty = Paths.get("");
		
		assertEquals(Paths.get("/ahoj/dnes/pekne"), root.resolveSibling(subpath));
		
		// if argument has root, argument is returned
		assertEquals(root, subpath.resolveSibling(root));
		assertEquals(secRoot, root.resolveSibling(secRoot));
		
		// combining two rootless paths
		assertEquals(Paths.get("dnes/praci/prasok"), subpath.resolveSibling(secSub));
		
		// absolute with root - root of absolute is kept and remainder is replacet with argument
		assertEquals(Paths.get("C:\\ahoj\\slnko"), absolute.resolveSibling(root));
		// absolute with rootless subpath
		assertEquals(Paths.get("C:\\dobre\\dnes\\pekne"), absolute.resolveSibling(subpath));
		
		assertEquals(Paths.get("/ahoj/nedela"), root.resolveSibling(parentless));
		assertEquals(Paths.get("dnes/nedela"), subpath.resolveSibling(parentless));
		// parentless with argument - returns argument
		assertEquals(root, parentless.resolveSibling(root));
		assertEquals(absolute, parentless.resolveSibling(absolute));
		assertEquals(subpath, parentless.resolveSibling(subpath));
		
		// empty with argument - returns argument
		assertEquals(root, empty.resolveSibling(root));
		assertEquals(absolute, empty.resolveSibling(absolute));
		assertEquals(subpath, empty.resolveSibling(subpath));
		assertEquals(parentless, empty.resolveSibling(parentless));
		assertEquals(Paths.get(""), empty.resolveSibling(Paths.get("")));
		
		// path with empty argument, returns parent of path
		assertEquals(Paths.get("/ahoj"), root.resolveSibling(empty));
		assertEquals(root.getParent(), root.resolveSibling(empty));
		assertEquals(Paths.get("C:\\dobre"), absolute.resolveSibling(empty));
		assertEquals(absolute.getParent(), absolute.resolveSibling(empty));
		assertEquals(Paths.get("dnes"), subpath.resolveSibling(empty));
		assertEquals(subpath.getParent(), subpath.resolveSibling(empty));
		// this is exeption, because parentless dont have parent
		assertEquals(Paths.get(""), parentless.resolveSibling(empty));
		assertNull(parentless.getParent());
		assertNotEquals(parentless.getParent(), parentless.resolveSibling(empty));	
	}
	
	/**
	 * Relativize paths means to create a path based o two other paths such the new path 
	 * represents a way of navigation from one of the original paths to the other.
	 * This is inversion of resolution operation.
	 * 
	 * HELP: P1.relativize(P2) - how to get from P1 to P2
	 */
	@Test
	public void testRelatizevePaths() {
		Path root = Paths.get("/ahoj/slnko/peniaze/auto");
		Path subpath = Paths.get("peniaze/auto");
		Path secRoot = Paths.get("/fc/barca/hnoj");
		Path secSub = Paths.get("praci/prasok/nojo");
		Path parentless = Paths.get("auto");
		Path empty = Paths.get("");
		
		Path rp1 = Paths.get("/ahoj/slnko");
		
		// ak nemaju rooty, da sa vytvorit
		assertEquals(Paths.get("../../praci/prasok/nojo"), subpath.relativize(secSub));
		assertEquals(Paths.get("../../../peniaze/auto"), secSub.relativize(subpath));
		
		// ak maju oba rooty, tak sa da zostrojit - implementacne zavisle
		assertEquals(Paths.get("../../../../fc/barca/hnoj"), root.relativize(secRoot));
		assertEquals(Paths.get("../../../ahoj/slnko/peniaze/auto"), secRoot.relativize(root));
		
		// ak su pathy rovnake tak ta sa vrati prazdna path
		assertEquals(empty, root.relativize(root));
		
		assertEquals(Paths.get("peniaze/auto"), rp1.relativize(root));
		assertEquals(Paths.get("../.."), root.relativize(rp1));
		
		assertEquals(Paths.get("../../auto"), Paths.get("peniaze/auto").relativize(Paths.get("auto")));
		assertEquals(Paths.get("../peniaze/auto"), Paths.get("auto").relativize(Paths.get("peniaze/auto")));
		
		assertEquals(Paths.get(".."), Paths.get("peniaze/auto").relativize(Paths.get("peniaze")));
		assertEquals(Paths.get("auto"), Paths.get("peniaze").relativize(Paths.get("peniaze/auto")));
		
		// --- error states
		int exeptionsThrown = 0;
		
		// root with subpath
		try {
			root.relativize(subpath);
		} catch (IllegalArgumentException e) {
			exeptionsThrown++;
		}
		
		// subpath with root
		try {
			subpath.relativize(root);
		} catch (IllegalArgumentException e) {
			exeptionsThrown++;
		}
		
		// root with parentless
		try {
			root.relativize(parentless);
		} catch (IllegalArgumentException e) {
			exeptionsThrown++;
		}
		
		// empty with root
		try {
			empty.relativize(root);
		} catch (IllegalArgumentException e) {
			exeptionsThrown++;
		}
		
		// root with empty
		try {
			root.relativize(empty);
		} catch (IllegalArgumentException e) {
			exeptionsThrown++;
		}
		
		assertEquals(5, exeptionsThrown);
	}
	
}
