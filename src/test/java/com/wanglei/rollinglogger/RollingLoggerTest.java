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
	
	private static long mMaxLogFileSize;
	private static int mMaxLogFileCount;
	
	private File mLogDir;
	private FilenameFilter mFilter;
	private RollingLogger mLogger;
	
    @BeforeClass
    public static void oneTimeSetUp() {
        // one-time initialization code
    	mMaxLogFileSize = 1024;
    	mMaxLogFileCount = 5;
    }
 
    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
    }
 
    @Before
    public void setUp() throws IOException {
    	mLogDir = testFolder.newFolder("log");
    	final String logFileName = "log";
    	mFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(logFileName);
			}
		};
    	mLogger = new RollingLogger(mLogDir.getPath(), logFileName, 
    			mMaxLogFileSize, mMaxLogFileCount);
    }
 
    @After
    public void tearDown() {
    }
    
    @Test
    public void testWriteLog() throws IOException {
    	writeLogs("This is a short log.", 5);
    	writeLogs("This is another short log.", 100);
    	writeLogs("This is a very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very long log.",
    			10000);
    	
    	checkLogFilesModifiedOrder();
    }
    
    @Test
    public void testWriteLogLine() throws IOException {
    	writeLogLines("This is a short log.", 5);
    	writeLogLines("This is another short log.", 100);
    	writeLogLines("This is a very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very long log.",
    			10000);
    	
    	checkLogFilesModifiedOrder();
    }
    
    private void writeLogs(String log, int lineCount){
    	for(int index = 0; index < lineCount; index++){
    		mLogger.writeLog(log);
    	}
    	checkLogFilesCount();
    }
    
    private void writeLogLines(String log, int lineCount){
    	for(int index = 0; index < lineCount; index++){
    		mLogger.writeLogLine(log);
    	}
    	checkLogFilesCount();
    }
    
    private void checkLogFilesCount(){
    	// make sure log file count is no more than maxLogFileCount
    	Assert.assertTrue(mLogDir.listFiles(mFilter).length <= mMaxLogFileCount);
    }
    
    private void checkLogFilesModifiedOrder(){
    	List<File> fileList = Arrays.asList(mLogDir.listFiles(mFilter));
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
    		Assert.assertEquals(index, mLogger.getLogFileIndex(file.getName()));
    	}
    }
 
}
