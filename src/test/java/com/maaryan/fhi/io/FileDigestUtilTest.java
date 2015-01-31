package com.maaryan.fhi.io;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDigestUtilTest {
	protected Logger logger = LoggerFactory.getLogger(FileDigestUtilTest.class);

	@Test
	public void testGetFileHash() {
		try {
			
//			Path file = Paths.get(FileDigestUtilTest.class.getResource("FileDigestUtilTest.class").toURI());
			Path file = Paths.get("C:\\my-drive\\swares\\spring-tool-suite-3.6.2.RELEASE-e4.4.1-win32-x86_64.zip");
			long startTime = System.currentTimeMillis();
			FileHash fileHash = new FileHash(file);
			logger.info(fileHash.getFileHashKey());
			long endTime = System.currentTimeMillis();
			logger.info("Took "+ (endTime-startTime) +"msecs");
			
			try(FileInputStream fis = new FileInputStream(file.toFile())){
				startTime = System.currentTimeMillis();
				logger.info(DigestUtils.sha512Hex(fis));
				endTime = System.currentTimeMillis();
				logger.info("Took "+ (endTime-startTime) +"msecs");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
