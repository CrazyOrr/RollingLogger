package com.github.crazyorr.rollinglogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit test for RollingLogger.
 */
public class RollingLoggerTest {
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	private static long mLogFileMaxSize;
	private static int mMaxLogFileCount;

	private File mLogDir;
	private FilenameFilter mFilter;
	private RollingLogger mLogger;

	@BeforeClass
	public static void oneTimeSetUp() {
		// one-time initialization code
		mLogFileMaxSize = 1024;
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
				mLogFileMaxSize, mMaxLogFileCount);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testWriteLog() throws IOException {
		mLogger.writeLog("This is a log.");
		checkLogFilesNumber();
		
		int count = 0;
		String logTemplate = "This is log <%s>.";
		while(mLogDir.listFiles(mFilter).length < mMaxLogFileCount){
			mLogger.writeLog(String.format(logTemplate, count++));
		}
		for(int index = 0; index < count; index++){
			mLogger.writeLog(String.format(logTemplate, (count + index)));
		}
		checkLogFilesNumber();

		Map<File, List<Integer>> map = new HashMap<File, List<Integer>>();
		for(File file : mLogDir.listFiles(mFilter)){
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String line = br.readLine();
			
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(line);
			List<Integer> list = new ArrayList<Integer>();
			while(m.find()){
				list.add(Integer.parseInt(m.group()));
			}
			map.put(file, list);
			br.close();
		}
		
		checkLogFilesOrder(map);
	}
	
	@Test
	public void testWriteLogLine() throws IOException {
		mLogger.writeLogLine("This is a log.");
		checkLogFilesNumber();
		
		int count = 0;
		String logTemplate = "This is log <%s>.";
		while(mLogDir.listFiles(mFilter).length < mMaxLogFileCount){
			mLogger.writeLogLine(String.format(logTemplate, count++));
		}
		for(int index = 0; index < count; index++){
			mLogger.writeLogLine(String.format(logTemplate, (count + index)));
		}
		checkLogFilesNumber();
		
		Map<File, List<Integer>> map = new HashMap<File, List<Integer>>();
		for(File file : mLogDir.listFiles(mFilter)){
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			List<Integer> list = new ArrayList<Integer>();
			String line;
			while ((line = br.readLine()) != null) {
				Pattern p = Pattern.compile("\\d+");
				Matcher m = p.matcher(line);
				while(m.find()){
					list.add(Integer.parseInt(m.group()));
				}
		    }
			map.put(file, list);
			br.close();
		}
		
		checkLogFilesOrder(map);
	}

	private void checkLogFilesNumber() {
		// make sure log file count is no more than maxLogFileCount
		int logFilesNumber = mLogDir.listFiles(mFilter).length;
		Assert.assertTrue(logFilesNumber > 0 && logFilesNumber <= mMaxLogFileCount);
	}

	private void checkLogFilesOrder(Map<File, List<Integer>> map) {
		TreeSet<File> fileSet = new TreeSet<File>(map.keySet());
		final int NONE = 0;
		int lastFileFirstLogNumber = NONE;
		for(File file : fileSet){
			System.out.println(file.getName());
			List<Integer> logNumberList = map.get(file);
			int firstLogNumber = logNumberList.get(0);
			int lastLogNumber = logNumberList.get(logNumberList.size() - 1);
			System.out.println(file.getName() + " firstLogNumber = " + firstLogNumber
					+ " lastLogNumber = " + lastLogNumber);
			if(lastFileFirstLogNumber != NONE){
				Assert.assertEquals(lastFileFirstLogNumber - 1, lastLogNumber);
			}
			lastFileFirstLogNumber = firstLogNumber;
			Assert.assertTrue(firstLogNumber <= lastLogNumber);
		}
	}
}
