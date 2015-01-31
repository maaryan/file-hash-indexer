package com.maaryan.fhi.task;

import java.io.IOException;
import java.nio.file.Path;

import com.maaryan.fhi.FileHashIndexerFullPath;
import com.maaryan.fhi.excp.FileHashIndexerException;

public class FileMetaFullPathTask extends FileMetaTask{

	public FileMetaFullPathTask(FileHashIndexerFullPath fileIndexerFullPath, Path file) {
		super(fileIndexerFullPath, file);
	}

	protected String getPath(Path file){
		try {
			return file.toRealPath().toString();
		} catch (IOException e) {
			throw new FileHashIndexerException(e);
		}
	}
}
