package com.maaryan.fhi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maaryan.fhi.commons.SleepUtil;
import com.maaryan.fhi.conf.FileHashIndexerConfig;
import com.maaryan.fhi.excp.FileHashIndexerException;
import com.maaryan.fhi.io.PathUtil;
import com.maaryan.fhi.task.FileMetaFeeder;
import com.maaryan.fhi.task.FolderScannerRejectionHandler;
import com.maaryan.fhi.task.FolderScannerTask;
import com.maaryan.fhi.vo.FileHashIndex;

public abstract class FileHashIndexer implements Runnable {
	protected Logger logger;
	protected Set<Path> foldersToScan;
	protected FileHashIndexerConfig indexerConfig;
	protected ThreadPoolExecutor folderScannerExecutor;
	protected Thread fileMetaFeederTask;
	protected BlockingQueue<Path> fileQueue;
	protected FileMetaFeeder fileMetaFeeder;
	protected FileHashIndex fileHashIndex;
	protected Comparator<Path> fileSizeComparator = new Comparator<Path>() {
		@Override
		public int compare(Path o1, Path o2) {
			try {
				return Long.compare(Files.size(o1), Files.size(o2));
			} catch (IOException e) {
				throw new FileHashIndexerException(e);
			}
		}
	};

	private FileHashIndexer() {
		logger = LoggerFactory.getLogger(this.getClass());
	};

	protected FileHashIndexer(Set<Path> foldersToScan,
			FileHashIndexerConfig indexerConfig) {
		this();
		this.foldersToScan = PathUtil.filter(foldersToScan);
		this.indexerConfig = indexerConfig;
		this.folderScannerExecutor = new ThreadPoolExecutor(
				indexerConfig.getFolderScannerCorePoolSize(),
				indexerConfig.getFolderScannerMaxPoolSize(),
				indexerConfig.getFolderScannerKeepAliveTimeMsecs(),
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
						indexerConfig.getFolderScannerQueueSize()));
		folderScannerExecutor
				.setRejectedExecutionHandler(new FolderScannerRejectionHandler());
		this.fileQueue = new PriorityBlockingQueue<Path>(
				this.indexerConfig.getFileQueueSize(), fileSizeComparator);
		fileHashIndex = new FileHashIndex();
		fileHashIndex.setHashAlgorithm(indexerConfig.getFileHashAlgorithm());
	}

	public void run() {
		logger.info("FileHashIndexerFullPath start");
		indexFiles();
		logger.info("FileHashIndexerFullPath end");
	}

	public FileHashIndex indexFiles() {
		logger.info("Indexing started..");
		fileMetaFeederTask = new Thread(fileMetaFeeder);
		fileMetaFeederTask.start();
		for (Path f : foldersToScan) {
			folderScannerExecutor.execute(new FolderScannerTask(f, indexerConfig
					.getPathFileFilter(), fileQueue));
		}
		while (folderScannerExecutor.getActiveCount() != 0
				|| fileMetaFeeder.getFileMetaTaskExecutor().getActiveCount() != 0
				|| fileQueue.size() != 0) {
			logger.debug("folderScannerExector.getActiveCount():"
					+ folderScannerExecutor.getActiveCount()
					+ " , fileMetaFeeder.getFileMetaTaskExecutor().getActiveCount():"
					+ fileMetaFeeder.getFileMetaTaskExecutor().getActiveCount()
					+ " , fileQueue.size():"
					+ fileQueue.size()
					);
			SleepUtil.sleep(1000);
		}
		folderScannerExecutor.shutdown();
		try {
			logger.info("Awaiting shutdown of folderScannerExector");
			folderScannerExecutor.awaitTermination(60, TimeUnit.MINUTES);
			logger.info("folderScannerExector is shutted down");
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		fileMetaFeeder.setShutDownFlag(true);
		while(fileMetaFeederTask.isAlive()){
			logger.info("Awaiting shutdown of fileMetaFeeder");
			SleepUtil.sleep(1000);
		}
		fileMetaFeeder.getFileMetaTaskExecutor().shutdown();
		try {
			logger.info("Awaiting shutdown of fileMetaFeeder");
			fileMetaFeeder.getFileMetaTaskExecutor().awaitTermination(60,
					TimeUnit.MINUTES);
			logger.info("fileMetaFeeder is shutted down");
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}

		logger.info("Indexing done..");
		return fileHashIndex;
	}

	protected void validate() {
		if (indexerConfig == null) {
			throw new FileHashIndexerException("indexerConfig cannot be null.");
		}
	}

	public Set<Path> getFoldersToScan() {
		return foldersToScan;
	}

	public FileHashIndexerConfig getIndexerConfig() {
		return indexerConfig;
	}

	public BlockingQueue<Path> getFileQueue() {
		return fileQueue;
	}

	public FileMetaFeeder getFileMetaFeeder() {
		return fileMetaFeeder;
	}

	public FileHashIndex getFileHashIndex() {
		return fileHashIndex;
	}

}
