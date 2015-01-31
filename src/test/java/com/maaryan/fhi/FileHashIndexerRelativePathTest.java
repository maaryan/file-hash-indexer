package com.maaryan.fhi;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.maaryan.fhi.excp.FileHashIndexerException;
import com.maaryan.fhi.io.PathUtil;
import com.maaryan.fhi.vo.FileHashIndex;

public class FileHashIndexerRelativePathTest extends FileHashIndexerTest {
	private static Path testFolder;
	private static Set<Path> foldersToScan= new HashSet<Path>();
	@BeforeClass
	public static void beforeClass() {
		try {
			testFolder = Files.createTempDirectory("fhi-folder-scanner");
			Files.createDirectories(testFolder);
			Path a = testFolder.resolve("a");
			Files.createDirectory(a);
			Path b = testFolder.resolve("b");
			Files.createDirectory(b);
			Files.createFile(a.resolve("f1.txt"));
			Files.createFile(b.resolve("f2.jpg"));
			foldersToScan.add(a.toRealPath());
			foldersToScan.add(b.toRealPath());
		} catch (IOException e) {
			throw new FileHashIndexerException(e);
		}
	}

	@Test
	public void testIndexFiles() {
		FileHashIndexer fhixr = new FileHashIndexerRelativePath(testFolder, foldersToScan);
		FileHashIndex fhi = fhixr.indexFiles();
		System.out.println(fhi.getFileIndexMap());
		System.out.println(fhi.getFileIndexMap().size());
		Assert.assertTrue(fhi.getFileIndexMap().size()==1);
		fail("Not yet implemented");
	}
	@AfterClass
	public static void afterClass() {
		PathUtil.deleteRecursively(testFolder);
	}
}
