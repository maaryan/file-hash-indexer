package com.maaryan.fhi.task;

import java.nio.file.Path;

import com.maaryan.fhi.FileHashIndexerRelativePath;

public class FileMetaRelativePathTask extends FileMetaTask {
	private Path baseFolder;

	public FileMetaRelativePathTask(
			FileHashIndexerRelativePath fileIndexerRelativePath, Path file) {
		super(fileIndexerRelativePath, file);
		this.baseFolder = fileIndexerRelativePath.getBaseFolder();
	}

	protected String getPath(Path file) {
		return baseFolder.relativize(file).toString();
	}
}
