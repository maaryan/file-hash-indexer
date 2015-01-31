package com.maaryan.fhi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maaryan.fhi.commons.SleepUtil;
import com.maaryan.fhi.conf.FileHashIndexerConfig;
import com.maaryan.fhi.excp.FileHashIndexerException;
import com.maaryan.fhi.task.FileMetaFeeder;
import com.maaryan.fhi.task.FolderScannerRejectionHandler;
import com.maaryan.fhi.task.FolderScannerTask;
import com.maaryan.fhi.vo.FileHashIndex;

public abstract class FileHashIndexer implements Runnable {
	protected Logger logger;
	protected Set<Path> foldersToScan;
	protected FileHashIndexerConfig indexerConfig;
	protected ThreadPoolExecutor folderScannerExector;
	protected Thread fileMetaFeederTask;
	protected BlockingQueue<Path> fileQueue;
	protected FileMetaFeeder fileMetaFeeder;
	protected FileHashIndex fileHashIndex;
	protected Comparator<Path> fileSizeComparator = new Comparator<Path>() {
		@Override
		public int compare(Path o1, Path o2) {
			try {
				return Long.compare(Files.size(o1), Files.size(o2));
			} catch (IOException e) {
				throw new FileHashIndexerException(e);
			}
		}
	};

	private FileHashIndexer() {
		logger = LoggerFactory.getLogger(this.getClass());
	};

	protected FileHashIndexer(Set<Path> foldersToScan,
			FileHashIndexerConfig indexerConfig) {
		this();
		this.foldersToScan = filter(foldersToScan);
		this.indexerConfig = indexerConfig;
		this.folderScannerExector = new ThreadPoolExecutor(
				indexerConfig.getFolderScannerCorePoolSize(),
				indexerConfig.getFolderScannerMaxPoolSize(),
				indexerConfig.getFolderScannerKeepAliveTimeMsecs(),
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
						indexerConfig.getFolderScannerQueueSize()));
		folderScannerExector
				.setRejectedExecutionHandler(new FolderScannerRejectionHandler());
		this.fileQueue = new PriorityBlockingQueue<Path>(
				this.indexerConfig.getFileQueueSize(), fileSizeComparator);
		fileHashIndex = new FileHashIndex();
		fileHashIndex.setHashAlgorithm(indexerConfig.getFileHashAlgorithm());
	}

	public void run() {
		logger.info("FileHashIndexerFullPath start");
		indexFiles();
		logger.info("FileHashIndexerFullPath end");
	}

	public FileHashIndex indexFiles() {
		logger.info("Indexing started..");
		fileMetaFeederTask = new Thread(fileMetaFeeder);
		fileMetaFeederTask.start();
		for (Path f : foldersToScan) {
			folderScannerExector.execute(new FolderScannerTask(f, indexerConfig
					.getPathFileFilter(), fileQueue));
		}
		while (folderScannerExector.getActiveCount() != 0
				|| fileMetaFeeder.getFileMetaTaskExecutor().getActiveCount() != 0
				|| fileQueue.size() != 0) {
			logger.debug("folderScannerExector.getActiveCount():"
					+ folderScannerExector.getActiveCount()
					+ " , fileMetaFeeder.getFileMetaTaskExecutor().getActiveCount():"
					+ fileMetaFeeder.getFileMetaTaskExecutor().getActiveCount()
					+ " , fileQueue.size():"
					+ fileQueue.size()
					);
			SleepUtil.sleep(1000);
		}
		folderScannerExector.shutdown();
		try {
			logger.info("Awaiting shutdown of folderScannerExector");
			folderScannerExector.awaitTermination(60, TimeUnit.MINUTES);
			logger.info("folderScannerExector is shutted down");
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		fileMetaFeeder.setShutDownFlag(true);
		while(fileMetaFeederTask.isAlive()){
			logger.info("Awaiting shutdown of fileMetaFeeder");
			SleepUtil.sleep(1000);
		}
		fileMetaFeeder.getFileMetaTaskExecutor().shutdown();
		try {
			logger.info("Awaiting shutdown of fileMetaFeeder");
			fileMetaFeeder.getFileMetaTaskExecutor().awaitTermination(60,
					TimeUnit.MINUTES);
			logger.info("fileMetaFeeder is shutted down");
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}

		logger.info("Indexing done..");
		return fileHashIndex;
	}

	protected void validate() {
		if (indexerConfig == null) {
			throw new FileHashIndexerException("indexerConfig cannot be null.");
		}
	}

	protected Set<Path> filter(Set<Path> foldersToScan) {
		PathNode pathNode = new PathNode();
		for (Path p : foldersToScan) {
			if (!Files.isDirectory(p)) {
				logger.warn(p.toString() + " is not directory.");
				continue;
			}
			String[] pathElements = getPathElements(p);
			pathNode.addPath(pathElements);
		}
		Set<Path> nFoldersToScan = new HashSet<Path>();
		pathNode.addFilteredPaths(nFoldersToScan);
		return nFoldersToScan;
	}

	private String[] getPathElements(Path p) {
		try {
			p = p.toRealPath();
			String[] elements = new String[p.getNameCount() + 1];
			Iterator<Path> itr = p.iterator();
			String seperator = p.getFileSystem().getSeparator();
			if ("\\".equals(seperator)) {
				elements[0] = p.getRoot().toString().toLowerCase()
						.replace(seperator, "");
				int i = 1;
				while (itr.hasNext()) {
					elements[i++] = itr.next().toString().toLowerCase();
				}
			} else {
				elements[0] = p.getRoot().toString().replace(seperator, "");
				int i = 1;
				while (itr.hasNext()) {
					elements[i++] = itr.next().toString();
				}
			}
			return elements;
		} catch (IOException e) {
			throw new FileHashIndexerException(e);
		}
	}

	public Set<Path> getFoldersToScan() {
		return foldersToScan;
	}

	public FileHashIndexerConfig getIndexerConfig() {
		return indexerConfig;
	}

	public BlockingQueue<Path> getFileQueue() {
		return fileQueue;
	}

	public FileMetaFeeder getFileMetaFeeder() {
		return fileMetaFeeder;
	}

	public FileHashIndex getFileHashIndex() {
		return fileHashIndex;
	}

	private static class PathNode {
		private Map<String, PathNode> pathNodeMap = new HashMap<>();
		private String name = "";
		private int depth = -1;
		private boolean flag = false;
		private String[] path = {};

		public PathNode() {

		}

		public void addFilteredPaths(Set<Path> filteredpaths) {
			if (flag) {
				filteredpaths.add(getPath());
			} else {
				for (PathNode pn : pathNodeMap.values()) {
					pn.addFilteredPaths(filteredpaths);
				}
			}
		}

		private Path getPath() {
			StringBuilder sb = new StringBuilder();
			for (String s : path) {
				sb.append(s).append("/");
			}
			return Paths.get(sb.toString());
		}

		public void addPath(String[] path) {
			if (path == null || path.length == 0) {
				flag = true;
				return;
			}
			addPath(path, depth + 1);
		}

		private void addPath(String[] path, int depth) {
			PathNode pathNode = pathNodeMap.get(path[depth]);
			if (pathNode == null) {
				pathNode = new PathNode();
				pathNode.name = path[depth];
				pathNode.depth = depth;
				pathNode.path = Arrays.copyOf(path, depth + 1);
				pathNodeMap.put(pathNode.name, pathNode);
			}
			if (path.length == depth + 1) {
				pathNode.flag = true;
			} else {
				pathNode.addPath(path, pathNode.depth + 1);
			}
		}
		// public void print(){
		// System.out.println(tabs(depth+1)+name+"("+flag+")");
		// for(PathNode p:pathNodeMap.values()){
		// p.print();
		// }
		// }
		// private String tabs(int count){
		// StringBuilder sb = new StringBuilder();
		// for(int i=0;i<count;i++){
		// sb.append("|---");
		// }
		// return sb.toString();
		// }
	}

}
