package com.maaryan.fhi.io;

import java.nio.file.Path;

public interface PathFileFilter {
	boolean accept(Path path);
}
