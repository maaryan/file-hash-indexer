package com.maaryan.fhi;

import java.nio.file.Path;
import java.util.Set;

import com.maaryan.fhi.conf.DefaultConfigFactory;
import com.maaryan.fhi.conf.FileHashIndexerConfig;
import com.maaryan.fhi.task.FileMetaFeederFullPath;

public class FileHashIndexerFullPath extends FileHashIndexer {
	public FileHashIndexerFullPath(Set<Path> foldersToScan) {
		this(foldersToScan, DefaultConfigFactory.getFileHashIndexerFullPathConfig());
	}
	
	public FileHashIndexerFullPath(Set<Path> foldersToScan, FileHashIndexerConfig indexerConfig) {
		super(foldersToScan, indexerConfig);
		fileMetaFeeder = new FileMetaFeederFullPath(this);
		validate();
	}
}
