package com.maaryan.fhi.conf;

public class DefaultConfigFactory {
	
	public static FileHashIndexerConfig getFileHashIndexerRelativePathConfig(){
		FileHashIndexerConfig fileIndexerConfig = new FileHashIndexerConfig();
		//TODO: Configure based on system resources
		return fileIndexerConfig;
	}
	
	public static FileHashIndexerConfig getFileHashIndexerFullPathConfig(){
		FileHashIndexerConfig fileIndexerConfig = new FileHashIndexerConfig();
		//TODO: Configure based on system resources
		return fileIndexerConfig;
	}
	
}
