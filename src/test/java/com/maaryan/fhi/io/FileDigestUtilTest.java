package com.maaryan.fhi.io;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDigestUtilTest {
	protected Logger logger = LoggerFactory.getLogger(FileDigestUtilTest.class);

	@Test
	public void testGetFileHash() {
		try {
			
			Path file = Paths.get(FileDigestUtilTest.class.getResource("FileDigestUtilTest.class").toURI());
			long startTime = System.currentTimeMillis();
			FileHash fileHash = new FileHash(file);
			Assert.assertNotNull(fileHash.getFileHashKey());
			long endTime = System.currentTimeMillis();
			logger.info("Took "+ (endTime-startTime) +"msecs");
		} catch (Exception e) {
			Assert.fail("Exception not expected.");
		}
	}
}
