package com.maaryan.fhi.vo;

import java.util.Date;


public class FileMeta {
	private String fileHash;
	private Long fileSize;
	private String filePath;
	private Date createdTime;
	private Date modifiedTime;
	public String getFileHash() {
		return fileHash;
	}
	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public boolean isAcceptable() {
		return fileSize != null && fileHash!=null && !fileHash.trim().isEmpty();
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public Date getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fileHash == null) ? 0 : fileHash.hashCode());
		result = prime * result
				+ ((fileSize == null) ? 0 : fileSize.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileMeta other = (FileMeta) obj;
		if (fileHash == null) {
			if (other.fileHash != null)
				return false;
		} else if (!fileHash.equals(other.fileHash))
			return false;
		if (fileSize == null) {
			if (other.fileSize != null)
				return false;
		} else if (!fileSize.equals(other.fileSize))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "FileMeta [fileHash=" + fileHash + ", fileSize=" + fileSize
				+ ", filePath=" + filePath + "]";
	}
}
