package com.maaryan.fhi.conf;

import com.maaryan.fhi.io.FileHash;
import com.maaryan.fhi.io.PathFileFilter;

public class FileHashIndexerConfig {
	private int fileQueueSize = 10000;

	private int fileMetaTaskCorePoolSize = 100;
	private int fileMetaTaskMaxPoolSize = 500;
	private int fileMetaTaskQueueSize = 10000;
	private long fileMetaTaskKeepAliveTimeMsecs = 60 * 1000;

	private int folderScannerCorePoolSize = 10;
	private int folderScannerMaxPoolSize = 20;
	private int folderScannerQueueSize = 20;
	private long folderScannerKeepAliveTimeMsecs = 10 * 60 * 1000;

	private String fileHashAlgorithm = FileHash.ALG_SHA_512;
	private PathFileFilter pathFileFilter;
	public int getFileQueueSize() {
		return fileQueueSize;
	}
	public void setFileQueueSize(int fileQueueSize) {
		this.fileQueueSize = fileQueueSize;
	}
	public int getFileMetaTaskCorePoolSize() {
		return fileMetaTaskCorePoolSize;
	}
	public void setFileMetaTaskCorePoolSize(int fileMetaTaskCorePoolSize) {
		this.fileMetaTaskCorePoolSize = fileMetaTaskCorePoolSize;
	}
	public int getFileMetaTaskMaxPoolSize() {
		return fileMetaTaskMaxPoolSize;
	}
	public void setFileMetaTaskMaxPoolSize(int fileMetaTaskMaxPoolSize) {
		this.fileMetaTaskMaxPoolSize = fileMetaTaskMaxPoolSize;
	}
	public int getFileMetaTaskQueueSize() {
		return fileMetaTaskQueueSize;
	}
	public void setFileMetaTaskQueueSize(int fileMetaTaskQueueSize) {
		this.fileMetaTaskQueueSize = fileMetaTaskQueueSize;
	}
	public long getFileMetaTaskKeepAliveTimeMsecs() {
		return fileMetaTaskKeepAliveTimeMsecs;
	}
	public void setFileMetaTaskKeepAliveTimeMsecs(
			long fileMetaTaskKeepAliveTimeMsecs) {
		this.fileMetaTaskKeepAliveTimeMsecs = fileMetaTaskKeepAliveTimeMsecs;
	}
	public int getFolderScannerCorePoolSize() {
		return folderScannerCorePoolSize;
	}
	public void setFolderScannerCorePoolSize(int folderScannerCorePoolSize) {
		this.folderScannerCorePoolSize = folderScannerCorePoolSize;
	}
	public int getFolderScannerMaxPoolSize() {
		return folderScannerMaxPoolSize;
	}
	public void setFolderScannerMaxPoolSize(int folderScannerMaxPoolSize) {
		this.folderScannerMaxPoolSize = folderScannerMaxPoolSize;
	}
	public int getFolderScannerQueueSize() {
		return folderScannerQueueSize;
	}
	public void setFolderScannerQueueSize(int folderScannerQueueSize) {
		this.folderScannerQueueSize = folderScannerQueueSize;
	}
	public long getFolderScannerKeepAliveTimeMsecs() {
		return folderScannerKeepAliveTimeMsecs;
	}
	public void setFolderScannerKeepAliveTimeMsecs(
			long folderScannerKeepAliveTimeMsecs) {
		this.folderScannerKeepAliveTimeMsecs = folderScannerKeepAliveTimeMsecs;
	}
	public String getFileHashAlgorithm() {
		return fileHashAlgorithm;
	}
	public void setFileHashAlgorithm(String fileHashAlgorithm) {
		this.fileHashAlgorithm = fileHashAlgorithm;
	}
	public PathFileFilter getPathFileFilter() {
		return pathFileFilter;
	}
	public void setPathFileFilter(PathFileFilter pathFileFilter) {
		this.pathFileFilter = pathFileFilter;
	}
}
