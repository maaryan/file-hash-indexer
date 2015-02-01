package com.maaryan.fhi.task;

import java.nio.file.Path;

import com.maaryan.fhi.FileHashIndexerRelativePath;
import com.maaryan.fhi.helper.FileMetaHelper;
import com.maaryan.fhi.vo.FileMeta;

public class FileMetaRelativePathTask extends FileMetaTask {
	private Path baseFolder;

	public FileMetaRelativePathTask(
			FileHashIndexerRelativePath fileIndexerRelativePath, Path file) {
		super(fileIndexerRelativePath, file);
		this.baseFolder = fileIndexerRelativePath.getBaseFolder();
	}

	protected FileMeta getFileMeta(){
		return FileMetaHelper.getRelativePathMeta(baseFolder,file, fileHashIndexer.getIndexerConfig().getFileHashAlgorithm());
	}
}
