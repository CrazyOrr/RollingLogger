package com.wanglei.rollinglogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * RollingLogger for saving log to external storage
 * 
 * @author wanglei
 *
 */
public class RollingLogger {
	/**
	 * separate log file name from index
	 */
	private static final String COUNT_SEPARATOR = "_";

	/**
	 * log file dir
	 */
	private String mDir;
	/**
	 * log file name
	 */
	private String mName;
	/**
	 * sing log file max size in byte
	 */
	private long mFileSize;
	/**
	 * log file count
	 */
	private int mMaxFileCount;

	/**
	 * construct a RollingLogger
	 * 
	 * @param dir
	 *            log dir
	 * @param name
	 *            log file name
	 * @param fileSize
	 *            single log file size
	 * @param maxFileCount
	 *            max log file count
	 */
	public RollingLogger(String dir, String name, long fileSize,
			int maxFileCount) {
		mDir = dir;
		mName = name;
		mFileSize = fileSize;
		mMaxFileCount = maxFileCount;

		File dirFile = new File(mDir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
	}

	/**
	 * write a log
	 * 
	 * @param log
	 *            the log content
	 */
	public synchronized void writeLog(String log) {
		File file = new File(mDir, mName + COUNT_SEPARATOR + 0);
		if (file.length() < mFileSize) {
			writeLogToFile(log, file, true);
		} else {
			rotateLogFiles();
			writeLogToFile(log, file, false);
		}

	}

	private void writeLogToFile(String log, File file, boolean append) {
		try {
			FileOutputStream fos = new FileOutputStream(file, append);
			fos.write(log.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			file.getParentFile().mkdirs();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * get the log file index from the log file name
	 * 
	 * @param fileName
	 *            log file name
	 * @return log file index
	 */
	public int getLogFileIndex(String fileName) {
		int fileIndex = 0;
		try {
			fileIndex = Integer.parseInt(fileName.substring(fileName
					.lastIndexOf(COUNT_SEPARATOR) + 1));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return fileIndex;
	}

	/**
	 * rotate log file, filt at index to index+1
	 * 
	 * @return
	 */
	private boolean rotateLogFiles() {
		boolean isSuccess = true;
		File[] files = getFilesList();
		if ((files != null) && (files.length > 0)) {
			List<File> fileList = Arrays.asList(files);
	    	Collections.sort(fileList, new Comparator<File>() {
	    		
				@Override
				public int compare(File lhs, File rhs) {
					return getLogFileIndex(lhs.getName()) - getLogFileIndex(rhs.getName());
				}
			});
			
			int count = fileList.size();
			for (int index = count - 1; index >= 0; index--) {
				if (index < mMaxFileCount - 1) {
					File file = fileList.get(index);
					if (index < count - 1) {
						isSuccess = file.renameTo(fileList.get(index + 1));
					} else {
						isSuccess = file.renameTo(new File(mDir, mName
								+ COUNT_SEPARATOR + (index + 1)));
					}
					if (!isSuccess) {
						break;
					}
				}
			}
		}

		return isSuccess;
	}

	private File[] getFilesList() {
		File dir = new File(mDir);
		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(mName);
			}
		});
	}
}
