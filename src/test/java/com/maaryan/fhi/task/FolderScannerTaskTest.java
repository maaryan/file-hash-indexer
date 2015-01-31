package com.maaryan.fhi.task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.maaryan.fhi.FileHashIndexerTest;
import com.maaryan.fhi.excp.FileHashIndexerException;
import com.maaryan.fhi.io.PathFileFilter;
import com.maaryan.fhi.io.PathUtil;

public class FolderScannerTaskTest extends FileHashIndexerTest {
	private static Path testFolder;

	@BeforeClass
	public static void beforeClass() {
		try {
			testFolder = Files.createTempDirectory("fhi-folder-scanner");
			Files.createDirectories(testFolder);
			Path a = testFolder.resolve("a");
			Files.createDirectory(a);
			Path b = testFolder.resolve("b");
			Files.createDirectory(b);
		} catch (IOException e) {
			throw new FileHashIndexerException(e);
		}
	}

	@Test
	public void testScanFolder() throws IOException {
		try {
			BlockingQueue<Path> fileQueue = new LinkedBlockingQueue<Path>();
			FolderScannerTask folderScannerTask = new FolderScannerTask(
					testFolder, null, fileQueue);
			folderScannerTask.scanFolder();
			Assert.assertTrue(fileQueue.size() == 0);
			Files.createFile(testFolder.resolve("a/f1.txt"));
			folderScannerTask.scanFolder();
			Assert.assertTrue(fileQueue.size() == 1);
			Files.delete(testFolder.resolve("a/f1.txt"));
		} finally {
			Files.deleteIfExists(testFolder.resolve("a/f1.txt"));
		}
	}

	@Test
	public void testScanFolderWithFilter() throws IOException {
		try {
			BlockingQueue<Path> fileQueue = new LinkedBlockingQueue<Path>();
			FolderScannerTask folderScannerTask = new FolderScannerTask(
					testFolder, new PathFileFilter() {
						@Override
						public boolean accept(Path path) {
							return path.getFileName().toString().endsWith("jpg");
						}
					}, fileQueue);
			folderScannerTask.scanFolder();
			Assert.assertTrue(fileQueue.size() == 0);
			Files.createFile(testFolder.resolve("b/f1.txt"));
			Files.createFile(testFolder.resolve("a/f1.jpg"));
			folderScannerTask.scanFolder();
			Assert.assertTrue(fileQueue.size() == 1);
		} finally {
			Files.deleteIfExists(testFolder.resolve("b/f1.txt"));
			Files.deleteIfExists(testFolder.resolve("a/f1.jpg"));
		}
	}

	@AfterClass
	public static void afterClass() {
		PathUtil.deleteRecursively(testFolder);
	}
}
