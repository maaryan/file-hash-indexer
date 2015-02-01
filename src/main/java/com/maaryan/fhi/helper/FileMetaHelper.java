package com.maaryan.fhi.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import com.maaryan.fhi.excp.FileHashIndexerException;
import com.maaryan.fhi.io.FileHash;
import com.maaryan.fhi.vo.FileMeta;

public class FileMetaHelper {
	public static FileMeta getFullPathMeta(Path file, String hashAlgorithm){
		try {
			FileMeta fileMeta = getFileMeta(file, hashAlgorithm);
			fileMeta.setFilePath(file.toRealPath().toString());
			return fileMeta;
		} catch (IOException e) {
			throw new FileHashIndexerException(e);
		}
		
	}
	public static FileMeta getRelativePathMeta(Path baseFolder, Path file, String hashAlgorithm){
		try {
			FileMeta fileMeta = getFileMeta(file, hashAlgorithm);
			fileMeta.setFilePath(baseFolder.relativize(file).toString());
			return fileMeta;
		} catch (IOException e) {
			throw new FileHashIndexerException(e);
		}
		
	}
	private static FileMeta getFileMeta(Path file, String hashAlgorithm) throws IOException{
		FileMeta fileMeta = new FileMeta();
		fileMeta.setFileSize(Files.size(file));
		FileHash fileHash = new FileHash(file, hashAlgorithm);
		fileMeta.setFileHash(fileHash.getFileHashKey());
		BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class); 	
		fileMeta.setCreatedTime(new Date(attrs.creationTime().toMillis()));
		fileMeta.setModifiedTime(new Date(attrs.lastModifiedTime().toMillis()));
		return fileMeta;
	}
}
