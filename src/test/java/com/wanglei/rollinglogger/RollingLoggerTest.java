package com.wanglei.rollinglogger;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit test for RollingLogTest.
 */
public class RollingLoggerTest {
	@Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
	
    @BeforeClass
    public static void oneTimeSetUp() {
        // one-time initialization code
    }
 
    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
    }
 
    @Before
    public void setUp() {
    }
 
    @After
    public void tearDown() {
    }
    
    @Test
    public void testWriteLog() throws IOException {
    	File logFolder = testFolder.newFolder("log");
    	final String logFileName = "log";
    	FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(logFileName);
			}
		};
    	long maxLogFileSize = 1024;
    	int maxLogFileCount = 5;
    	RollingLogger logger = new RollingLogger(logFolder.getPath(), logFileName, 
    			maxLogFileSize, maxLogFileCount);
    	
    	writeLog(logger, "This is a short log.", 5);
    	System.out.println(logFolder.listFiles(filter).length);
    	// make sure log file count is no more than maxLogFileCount
    	Assert.assertTrue(logFolder.listFiles(filter).length <= maxLogFileCount);
    	
    	writeLog(logger, "This is another short log.", 100);
    	System.out.println(logFolder.listFiles(filter).length);
    	// make sure log file count is no more than maxLogFileCount
    	Assert.assertTrue(logFolder.listFiles(filter).length <= maxLogFileCount);
    	
    	writeLog(logger, "This is a very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very long log.",
    			10000);
    	System.out.println(logFolder.listFiles(filter).length);
    	// make sure log file count is no more than maxLogFileCount
    	Assert.assertTrue(logFolder.listFiles(filter).length <= maxLogFileCount);
    	
    	List<File> fileList = Arrays.asList(logFolder.listFiles(filter));
    	Collections.sort(fileList, new Comparator<File>() {
    		
			@Override
			public int compare(File lhs, File rhs) {
				// the one which had been modified later comes in front
				return (int)(rhs.lastModified() - lhs.lastModified());
			}
		});
    	// make sure the more recently modified file has the smaller index
    	for(int index = 0; index < fileList.size(); index++){
    		File file = fileList.get(index);
    		Assert.assertEquals(index, logger.getLogFileIndex(file.getName()));
    	}
    	
    }
    
    private void writeLog(RollingLogger logger, String log, int lineCount){
    	for(int index = 0; index < lineCount; index++){
    		logger.writeLog(log);
    	}
    }
 
}
