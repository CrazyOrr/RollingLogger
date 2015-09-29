package com.github.crazyorr.rollinglogger;

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
 * RollingLogger is for writing logs into a fix number of files in rolling
 * order, in case the log files' size grows immensely. You can specify the path
 * to store the log files, the name of log files, the maximum size of each 
 * individual log file, as well as the maximum log file count.
 * 
 * @author Lei Wang
 *
 */
public class RollingLogger {
	/**
	 * Separator between the log file name and its index
	 */
	private static final String COUNT_SEPARATOR = "_";
	/**
	 * Line separator
	 */
	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	/**
	 * Log file directory
	 */
	private String mDir;
	/**
	 * Log file name
	 */
	private String mName;
	/**
	 * Individual log file's maximum size (in bytes)
	 */
	private long mFileMaxSize;
	/**
	 * Maximum log file count
	 */
	private int mMaxFileCount;
	/**
	 * Current editing file
	 */
	private File mFile;

	/**
	 * Construct a RollingLogger
	 * 
	 * @param dir
	 *            Log file directory
	 * @param name
	 *            Log file name
	 * @param fileMaxSize
	 *            Individual log file's maximum size (in bytes)
	 * @param maxfileCount
	 *            Maximum log file count
	 */
	public RollingLogger(String dir, String name, long fileMaxSize,
			int maxfileCount) {
		if (fileMaxSize <= 0) {
			throw new IllegalArgumentException("file size must > 0 byte");
		}
		if (maxfileCount <= 0) {
			throw new IllegalArgumentException("file count must > 0");
		}
		mDir = dir;
		mName = name;
		mFileMaxSize = fileMaxSize;
		mMaxFileCount = maxfileCount;
		File dirFile = new File(mDir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		createNewFile();
	}

	private void createNewFile() {
		mFile = new File(mDir, addIndexToLogFileName(mName, 0));
	}

	private String addIndexToLogFileName(String fileName, int index) {
		return fileName + COUNT_SEPARATOR + index;
	}

	/**
	 * Write a log. (Synchronized)
	 * 
	 * @param log
	 *            The log content
	 * @throws IOException
	 */
	public synchronized void writeLog(String log) throws IOException {
		if (mFile.length() < mFileMaxSize) {
			writeLogToFile(log, mFile, true);
		} else {
			rotateLogFiles();
			createNewFile();
			writeLogToFile(log, mFile, false);
		}
	}

	/**
	 * Write a log in a line. (Synchronized)
	 * 
	 * @param log
	 *            The log content
	 * @throws IOException
	 */
	public synchronized void writeLogLine(String log) throws IOException {
		writeLog(log + LINE_SEPARATOR);
	}

	private void writeLogToFile(String log, File file, boolean append)
			throws IOException {
		try {
			FileOutputStream fos = new FileOutputStream(file, append);
			fos.write(log.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			file.getParentFile().mkdirs();
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Rotate log files, move each file at <code>index</code> to
	 * <code>index+1</code>, if <code>index+1</code> is larger than the maximum
	 * log file count, then the file at <code>index+1</code> is discarded. 
	 * In this way the number of log files never exceeds the limit.
	 * 
	 */
	private void rotateLogFiles() {
		File[] files = getFilesList();
		if ((files != null) && (files.length > 0)) {
			List<File> fileList = Arrays.asList(files);
			Collections.sort(fileList, new Comparator<File>() {

				@Override
				public int compare(File lhs, File rhs) {
					return getLogFileIndex(lhs.getName())
							- getLogFileIndex(rhs.getName());
				}
			});

			int count = fileList.size();
			for (int index = count - 1; index >= 0; index--) {
				File file = fileList.get(index);
				boolean isSuccess = true;
				if (index < count - 1) {
					File nextfile = fileList.get(index + 1);
					// Delete the next file and rename the file with it
					if(nextfile.exists()){
						nextfile.delete();
					}
					isSuccess = file.renameTo(nextfile);
				} else {
					// The log file count doesn't reach maximum
					if (count < mMaxFileCount) {
						// Create a new file at the end
						isSuccess = file.renameTo(new File(mDir,
								addIndexToLogFileName(mName, index + 1)));
					}
				}
				if (!isSuccess) {
					 throw new RuntimeException("Rotating log files failed.");
				}
			}
		}
	}

	private File[] getFilesList() {
		File dir = new File(mDir);
		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(mName);
			}
		});
	}

	/**
	 * Get the log file index from the composed log file name.
	 * 
	 * @param fileName
	 *            Log file name with index appended
	 * @return Log file index
	 */
	private int getLogFileIndex(String fileName) {
		int fileIndex = 0;
		try {
			fileIndex = Integer.parseInt(fileName.substring(fileName
					.lastIndexOf(COUNT_SEPARATOR) + 1));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return fileIndex;
	}

}
