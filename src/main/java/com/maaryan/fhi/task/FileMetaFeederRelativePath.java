package com.maaryan.fhi.task;

import java.nio.file.Path;

import com.maaryan.fhi.FileHashIndexerRelativePath;
import com.maaryan.fhi.commons.SleepUtil;

public class FileMetaFeederRelativePath extends FileMetaFeeder {

	public FileMetaFeederRelativePath(FileHashIndexerRelativePath fileHashIndexerRelativePath) {
		super(fileHashIndexerRelativePath);
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
					fileMetaTaskExecutor.execute(new FileMetaRelativePathTask(
							(FileHashIndexerRelativePath) fileHashIndexer, file));
				}

			} catch (InterruptedException e) {
				logger.warn(e.getMessage(), e);
			}
		}
	}
}
