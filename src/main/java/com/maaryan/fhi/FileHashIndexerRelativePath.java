package com.maaryan.fhi;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import com.maaryan.fhi.conf.DefaultConfigFactory;
import com.maaryan.fhi.conf.FileHashIndexerConfig;
import com.maaryan.fhi.excp.FileHashIndexerException;
import com.maaryan.fhi.task.FileMetaFeederRelativePath;

public class FileHashIndexerRelativePath extends FileHashIndexer {

	private Path baseFolder;
	
	public FileHashIndexerRelativePath(Path baseFolder, Set<Path> foldersToScan) {
		this(baseFolder, foldersToScan, DefaultConfigFactory.getFileHashIndexerRelativePathConfig());
	}
	
	public FileHashIndexerRelativePath(Path baseFolder, Set<Path> foldersToScan, FileHashIndexerConfig indexerConfig) {
		super(foldersToScan, indexerConfig);
		try {
			this.baseFolder = baseFolder.toRealPath();
		} catch (IOException e) {
			throw new FileHashIndexerException(e);
		}
		fileMetaFeeder = new FileMetaFeederRelativePath(this);
		fileHashIndex.setBaseFolderPath(this.baseFolder.toString());
		validate();
	}
	
	protected void validate() {
		super.validate();
		if(baseFolder==null)
			throw new FileHashIndexerException("Base Folder cannot be null.");
		for(Path p:foldersToScan){
			if(!p.startsWith(baseFolder)){
				throw new FileHashIndexerException("All folders to scan should be children of base folder's root.");
			}
		}
	}

	public Path getBaseFolder() {
		return baseFolder;
	}
	
}
