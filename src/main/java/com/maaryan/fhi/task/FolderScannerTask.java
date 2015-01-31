package com.maaryan.fhi.task;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maaryan.fhi.io.PathFileFilter;

public class FolderScannerTask implements Runnable, FileVisitor<Path> {
	protected Logger logger = LoggerFactory.getLogger(FolderScannerTask.class);
	protected Path folder;
	protected PathFileFilter pathFileFilter;
	protected final BlockingQueue<Path> fileQueue;

	public FolderScannerTask(Path folder, PathFileFilter pathFileFilter,
			BlockingQueue<Path> fileQueue) {
		this.folder = folder;
		this.fileQueue = fileQueue;
		this.pathFileFilter = pathFileFilter;
	}

	@Override
	public void run() {
		logger.info("FolderScanner task Started");
		scanFolder();
		logger.info("FolderScanner task Done");
	}

	public void scanFolder() {
		logger.info("Scanning folder " + folder);
		if (!Files.exists(folder)) {
			logger.warn("Folder not found " + folder);
		}
		try {
			Files.walkFileTree(folder, this);
			logger.info("Scanning done for folder " + folder);
		} catch (IOException e) {
			logger.error("Error while scanning " + folder, e);
		}
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			throws IOException {
		logger.debug("previsit " + dir);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException {
		logger.debug("Producing file to fileQueue: " + file);
		if (pathFileFilter == null || pathFileFilter.accept(file))
			fileQueue.add(file);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc)
			throws IOException {
		logger.info("visitFileFailed: " + file, exc);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc)
			throws IOException {
		logger.debug("postvisit " + dir);
		return FileVisitResult.CONTINUE;
	}

}
