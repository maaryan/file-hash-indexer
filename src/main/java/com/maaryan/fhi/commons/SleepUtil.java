package com.maaryan.fhi.commons;

import com.maaryan.fhi.excp.FileHashIndexerException;


public class SleepUtil {
	public static void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new FileHashIndexerException(e);
		}
	}
}
