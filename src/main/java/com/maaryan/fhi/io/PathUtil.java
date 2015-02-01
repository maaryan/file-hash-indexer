package com.maaryan.fhi.io;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maaryan.fhi.excp.FileHashIndexerException;

public class PathUtil {
	private static Logger logger = LoggerFactory.getLogger(PathUtil.class);
	public static void deleteRecursively(Path path) {
		try {
			if (!Files.exists(path))
				return;
			if (!Files.isDirectory(path))
				Files.delete(path);
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir,
						IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}

			});
		} catch (IOException e) {
			throw new FileHashIndexerException(e);
		}

	}

	public static String[] getPathElements(Path p) {
		try {
			p = p.toRealPath();
			String[] elements = new String[p.getNameCount() + 1];
			Iterator<Path> itr = p.iterator();
			String seperator = p.getFileSystem().getSeparator();
			if (isWindows()) {
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
	public static boolean isWindows(){
		String seperator = FileSystems.getDefault().getSeparator();
		return "\\".equals(seperator);
	}
	public static boolean isChild(Path parentFolder, Path path) {
		String[] parentElements = null;
		if(Files.isDirectory(parentFolder)){
			parentElements=getPathElements(parentFolder);
		}else{
			parentElements=getPathElements(parentFolder.getParent());
		}
		String[] pathElements = getPathElements(path);
		if(parentElements.length>pathElements.length)
			return false;
		if(isWindows()){
			for(int i=0;i<parentElements.length;i++){
				if(!parentElements[i].equalsIgnoreCase(pathElements[i])){
					return false;
				}
			}
		}else{
			for(int i=0;i<parentElements.length;i++){
				if(!parentElements[i].equals(pathElements[i])){
					return false;
				}
			}
		}
		return true;
	}
	
	public static Set<Path> filter(Set<Path> foldersToScan) {
		PathNode pathNode = new PathNode();
		for (Path p : foldersToScan) {
			if (!Files.isDirectory(p)) {
				logger.warn(p.toString() + " is not directory.");
				continue;
			}
			String[] pathElements = PathUtil.getPathElements(p);
			pathNode.addPath(pathElements);
		}
		Set<Path> nFoldersToScan = new HashSet<Path>();
		pathNode.addFilteredPaths(nFoldersToScan);
		return nFoldersToScan;
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
