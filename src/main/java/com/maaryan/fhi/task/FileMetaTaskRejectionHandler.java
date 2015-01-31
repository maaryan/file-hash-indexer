package com.maaryan.fhi.task;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileMetaTaskRejectionHandler implements RejectedExecutionHandler{
	private Logger logger = LoggerFactory.getLogger(FileMetaTaskRejectionHandler.class);
        @Override
        public void rejectedExecution(Runnable r,
                ThreadPoolExecutor executor) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.warn(e.getMessage(),e);
            }
            executor.execute(r);
        }
}
