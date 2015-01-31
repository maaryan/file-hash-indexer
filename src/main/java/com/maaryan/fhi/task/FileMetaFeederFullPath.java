package com.maaryan.fhi.task;

import java.nio.file.Path;

import com.maaryan.fhi.FileHashIndexerFullPath;
import com.maaryan.fhi.commons.SleepUtil;

public class FileMetaFeederFullPath extends FileMetaFeeder {

	public FileMetaFeederFullPath(
			FileHashIndexerFullPath fileHashIndexerFullPath) {
		super(fileHashIndexerFullPath);
	}

	@Override
	public void run() {
		logger.info("FileMetaFeederFullPath started..");
		while (!shutDownFlag) {
			try {
				if (fileHashIndexer.getFileQueue().isEmpty()) {
					logger.debug("FileQueue empty..");
					SleepUtil.sleep(1000);
				}
				while (!fileHashIndexer.getFileQueue().isEmpty()) {
					Path file = fileHashIndexer.getFileQueue().take();
					logger.debug("Consuming file on queue: " + file);
					fileMetaTaskExecutor.execute(new FileMetaFullPathTask(
							(FileHashIndexerFullPath) fileHashIndexer, file));
				}

			} catch (InterruptedException e) {
				logger.warn(e.getMessage(), e);
			}
		}
	}

}
