package com.maaryan.fhi.task;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maaryan.fhi.FileHashIndexer;
import com.maaryan.fhi.io.FileHash;
import com.maaryan.fhi.vo.FileMeta;

public abstract class FileMetaTask implements Runnable {
	protected Logger logger;
	protected FileHashIndexer fileIndexer;
	protected Path file;
	private FileMetaTask(){
		logger = LoggerFactory.getLogger(this.getClass());
	}
	public FileMetaTask(FileHashIndexer fileIndexer, Path file) {
		this();
		this.fileIndexer = fileIndexer;
		this.file = file;
	}

	@Override
	public void run() {
		logger.debug("Fetching fileMetaTask for file: " + file);
		try {
			FileMeta fileMeta = new FileMeta();
			fileMeta.setFilePath(getPath(file));
			fileMeta.setFileSize(Files.size(file));
			logger.debug("Fetching hash key for file: " + file);
			FileHash fileHash = new FileHash(file, fileIndexer
					.getIndexerConfig().getFileHashAlgorithm());
			fileMeta.setFileHash(fileHash.getFileHashKey());
			fileIndexer.getFileHashIndex().addFile(fileMeta);
			logger.debug("Added file to fileIndex: " + file);
		} catch (Exception e) {
			logger.error("Error while getting meta for file:" + file, e);
		}
	}

	protected abstract String getPath(Path file);
}
