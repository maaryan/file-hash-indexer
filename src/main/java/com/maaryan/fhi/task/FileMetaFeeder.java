package com.maaryan.fhi.task;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maaryan.fhi.FileHashIndexer;
import com.maaryan.fhi.conf.FileHashIndexerConfig;

public abstract class FileMetaFeeder implements Runnable {
	protected Logger logger = LoggerFactory.getLogger(FileMetaFeeder.class);
	protected final ThreadPoolExecutor fileMetaTaskExecutor;
	protected final FileHashIndexer fileHashIndexer;
	protected boolean shutDownFlag = false;
	public FileMetaFeeder(FileHashIndexer fileHashIndexer) {
		logger = LoggerFactory.getLogger(this.getClass());
		this.fileHashIndexer = fileHashIndexer;
		FileHashIndexerConfig config = fileHashIndexer.getIndexerConfig();
		fileMetaTaskExecutor = new ThreadPoolExecutor(
				config.getFileMetaTaskCorePoolSize(),
				config.getFileMetaTaskMaxPoolSize(),
				config.getFileMetaTaskKeepAliveTimeMsecs(),
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
						config.getFileMetaTaskQueueSize()));
		fileMetaTaskExecutor
				.setRejectedExecutionHandler(new FileMetaTaskRejectionHandler());
	}

	public ThreadPoolExecutor getFileMetaTaskExecutor() {
		return fileMetaTaskExecutor;
	}

	public void setShutDownFlag(boolean shutDownFlag) {
		this.shutDownFlag = shutDownFlag;
	}
	
}
