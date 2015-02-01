package com.maaryan.fhi.task;

import java.nio.file.Path;

import com.maaryan.fhi.FileHashIndexerFullPath;
import com.maaryan.fhi.helper.FileMetaHelper;
import com.maaryan.fhi.vo.FileMeta;

public class FileMetaFullPathTask extends FileMetaTask{

	public FileMetaFullPathTask(FileHashIndexerFullPath fileIndexerFullPath, Path file) {
		super(fileIndexerFullPath, file);
	}

	protected FileMeta getFileMeta(){
		return FileMetaHelper.getFullPathMeta(file, fileHashIndexer.getIndexerConfig().getFileHashAlgorithm());
	}
}
