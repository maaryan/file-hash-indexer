package com.maaryan.fhi.task;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maaryan.fhi.FileHashIndexer;
import com.maaryan.fhi.vo.FileMeta;

public abstract class FileMetaTask implements Runnable {
	protected Logger logger;
	protected FileHashIndexer fileHashIndexer;
	protected Path file;
	private FileMetaTask(){
		logger = LoggerFactory.getLogger(this.getClass());
	}
	public FileMetaTask(FileHashIndexer fileIndexer, Path file) {
		this();
		this.fileHashIndexer = fileIndexer;
		this.file = file;
	}

	@Override
	public void run() {
		logger.debug("Fetching fileMetaTask for file: " + file);
		try {
			FileMeta fileMeta = getFileMeta();
			fileHashIndexer.getFileHashIndex().addFile(fileMeta);
			logger.debug("Added file to fileIndex: " + file);
		} catch (Exception e) {
			logger.error("Error while getting meta for file:" + file, e);
		}
	}
	
	protected abstract FileMeta getFileMeta();
}
