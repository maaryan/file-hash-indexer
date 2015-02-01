package com.maaryan.fhi.vo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.maaryan.fhi.excp.FileHashIndexerException;

public class FileHashIndex {
	private String hashAlgorithm;
	private String baseFolderPath;
	private Map<String, Set<FileMeta>> fileIndexMap = new HashMap<>();

	public String getHashAlgorithm() {
		return hashAlgorithm;
	}

	public void setHashAlgorithm(String hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}

	public String getBaseFolderPath() {
		return baseFolderPath;
	}

	public void setBaseFolderPath(String baseFolderPath) {
		this.baseFolderPath = baseFolderPath;
	}

	public Map<String, Set<FileMeta>> getFileIndexMap(){
		return fileIndexMap;
	}
	
	public synchronized boolean addFile(FileMeta fileMeta) {
		if (!fileMeta.isAcceptable()) {
			throw new FileHashIndexerException("Not acceptable: "+fileMeta);
		}
		Set<FileMeta> fileSet = fileIndexMap.get(fileMeta.getFileHash());
		if (fileSet == null) {
			fileSet = new TreeSet<FileMeta>(new Comparator<FileMeta>() {
				public int compare(FileMeta o1, FileMeta o2) {
					if (o1 == null && o2 == null)
						return 0;
					if (o1 == null || o2 == null)
						return -1;
					return o1.getFilePath().compareTo(o2.getFilePath());
				}
			});
			fileIndexMap.put(fileMeta.getFileHash(), fileSet);
		}
		return fileSet.add(fileMeta);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("hashAlgorith:").append("\"").append(hashAlgorithm).append("\"").append(",");
		sb.append("baseFolderPath:").append("\"").append(baseFolderPath).append("\"").append(",");
		sb.append(fileIndexMap.toString());
		sb.append("}");
		return sb.toString();
	}

	public List<Set<FileMeta>> findDuplicates() {
		List<Set<FileMeta>> duplicateFilesList = new ArrayList<Set<FileMeta>>();
		for (Map.Entry<String, Set<FileMeta>> entry : fileIndexMap.entrySet()) {
			if (entry.getValue().size() > 1) {
				duplicateFilesList.add(entry.getValue());
			}
		}
		return duplicateFilesList;
	}
	public FileMeta getUniqueFileMeta(String fileHashKey){
		Set<FileMeta> s = fileIndexMap.get(fileHashKey);
		if(s.size()>0){
			return s.iterator().next();
		}
		return null;
	}
	public void addUniqueFileMeta(FileMeta fileMeta) {
		if (!fileMeta.isAcceptable()) {
			throw new FileHashIndexerException("Not acceptable: "+fileMeta);
		}
		Set<FileMeta> fileSet = fileIndexMap.get(fileMeta.getFileHash());
		fileSet = new TreeSet<FileMeta>(new Comparator<FileMeta>() {
			public int compare(FileMeta o1, FileMeta o2) {
				if (o1 == null && o2 == null)
					return 0;
				if (o1 == null || o2 == null)
					return -1;
				return o1.getFilePath().compareTo(o2.getFilePath());
			}
		});
		fileIndexMap.put(fileMeta.getFileHash(), fileSet);
		fileSet.add(fileMeta);
	}

}
