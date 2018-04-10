/*  bigdoc Java lib for easy to read/search from a big document
 *
 *  Copyright (c) 2006-2016 Tom Misawa, riversun.org@gmail.com
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 *  DEALINGS IN THE SOFTWARE.
 *  
 */
package org.riversun.bigdoc.bin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.riversun.bigdoc.bin.BinFileSearcher.BinFileProgressListener;

/**
 * 
 * Search bytes from big file<br>
 * Available for giga-bytes order file <br>
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 */
public class BigFileSearcher {

	public static interface OnProgressListener {
		public void onProgress(float progress);
	}

	public static interface OnRealtimeResultListener {
		public void onRealtimeResultListener(float progress, List<Long> pointerList);
	}

	private interface BinFileProgressListenerEx {
		public void onProgress(int workerNumber, int workerSize, List<Long> pointerList, float progress);
	}

	private ProgressCache progressCache;

	private boolean useOptimization = true;

	/**
	 * Use memory and threading optimization<br>
	 * (default is true)
	 * 
	 * @param enabled
	 *            optimization enabled or not
	 */
	public void setUseOptimization(boolean enabled) {
		this.useOptimization = enabled;
	}

	private final BinFileSearcher binFileSearcher = new BinFileSearcher();

	// max number of thread
	public static final int DEFAULT_MAX_NUM_OF_THREADS = 24;

	// thread number no limit
	public static final int THREADS_NO_LIMIT = 0;

	// Unit size when split loading
	public static final int DEFAULT_BLOCK_SIZE = 10 * 1024 * 1024;

	private int bufferSizePerWorker = BinFileSearcher.DEFAULT_BUFFER_SIZE;

	/**
	 * Number of threads used at the same time in one search
	 */
	private int subThreadSize = BinFileSearcher.DEFAULT_SUB_THREAD_SIZE;

	/**
	 * The size of the window used to scan memory
	 */
	private int subBufferSize = BinFileSearcher.DEFAULT_SUB_BUFFER_SIZE;

	/**
	 * Size per unit when divide loading big sized file into multiple pieces<br>
	 */
	private long blockSize = DEFAULT_BLOCK_SIZE;

	private int maxNumOfThreads = DEFAULT_MAX_NUM_OF_THREADS;

	private OnProgressListener onProgressListener;
	private OnRealtimeResultListener onRealtimeResultListener;

	private long _profile_lastStartTime;
	private long _profile_lastEndTime;

	/**
	 * Set size per unit when divide loading big sized file into multiple pieces<br>
	 * <br>
	 * In order to make this method effective,call setUseOptimization(false) to
	 * turn off the optimization.<br>
	 * 
	 * @param blockSize
	 *            size per unit when divide loading big sized file
	 */
	public void setBlockSize(long blockSize) {
		this.blockSize = blockSize;
	}

	/**
	 * Set size to be read into memory at one search <br>
	 * In order to make this method effective,call setUseOptimization(false) to
	 * turn off the optimization.<br>
	 * 
	 * @param bufferSize
	 *            size(byte) to be read into memory at one search operation
	 */
	public void setBufferSizePerWorker(int bufferSize) {
		this.bufferSizePerWorker = bufferSize;
	}

	/**
	 * Set max number of thread to concurrent load to file <br>
	 * Increased the number of threads does not means improving a performance<br>
	 * <br>
	 * In order to make this method effective,call setUseOptimization(false) to
	 * turn off the optimization.<br>
	 * 
	 * @param maxNumOfThreads
	 *            number of threads(concurrency)
	 */
	public void setMaxNumOfThreads(int maxNumOfThreads) {
		this.maxNumOfThreads = maxNumOfThreads;
	}

	/**
	 * Set number of threads used in each worker <br>
	 * In order to make this method effective,call setUseOptimization(false) to
	 * turn off the optimization.<br>
	 * 
	 * @param subThreadSize
	 *            number of threads for sub threads(concurrency)
	 */
	public void setSubThreadSize(int subThreadSize) {
		this.subThreadSize = subThreadSize;
	}

	/**
	 * Set the size of the window used to scan memory used in each worker <br>
	 * In order to make this method effective,call setUseOptimization(false) to
	 * turn off the optimization.<br>
	 * 
	 * @param subBufferSize
	 *            size(bytes) of the window
	 */
	public void setSubBufferSize(int subBufferSize) {
		this.subBufferSize = subBufferSize;
	}

	/**
	 * Returns the index within this file of the first occurrence of the
	 * specified substring.
	 * 
	 * @param f
	 *            target file
	 * @param searchBytes
	 *            sequence of bytes you want to search
	 * @return
	 */
	public Long indexOf(File f, byte[] searchBytes) {
		return indexOf(f, searchBytes, 0);
	}

	/**
	 * Returns the index within this file of the first occurrence of the
	 * specified substring, starting at the specified position.
	 * 
	 * @param f
	 *            target file
	 * @param searchBytes
	 *            a sequence of bytes you want to find
	 * @param fromPosition
	 *            "0" means the beginning of the file
	 * @return position of the first occurence. '-1' means that it was not
	 *         found.
	 */
	public Long indexOf(File f, byte[] searchBytes, long fromPosition) {
		return binFileSearcher.indexOf(f, searchBytes, fromPosition);
	}

	/**
	 * Search bytes from big file faster with realtime result callback<br>
	 * <br>
	 * This callbacks the result in real time, but since the concurrency is
	 * inferior to #searchBigFile,so the execution speed is slower than
	 * #searchBigFile
	 * 
	 * @param f
	 *            targetFile
	 * @param searchBytes
	 *            sequence of bytes you want to search
	 * @param listener
	 *            callback for progress and realtime result
	 */
	public List<Long> searchBigFileRealtime(File f, byte[] searchBytes, OnRealtimeResultListener listener) {
		return searchBigFileRealtime(f, searchBytes, 0, listener);
	}

	/**
	 * * Search bytes from big file faster with realtime result callback<br>
	 * <br>
	 * This callbacks the result in real time, but since the concurrency is
	 * inferior to #searchBigFile,so the execution speed is slower than
	 * #searchBigFile
	 * 
	 * @param f
	 *            targetFile
	 * @param searchBytes
	 *            sequence of bytes you want to search
	 * @param startPosition
	 *            starting position
	 * @param listener
	 *            callback for progress and realtime result
	 * @return
	 */
	public List<Long> searchBigFileRealtime(File f, byte[] searchBytes, long startPosition, OnRealtimeResultListener listener) {

		this.onRealtimeResultListener = listener;
		this.onProgressListener = null;

		int numOfThreadsOptimized = (int) (f.length() / blockSize);

		if (numOfThreadsOptimized == 0) {
			numOfThreadsOptimized = 1;
		}

		final long fileLen = f.length();

		// optimize before calling the method
		optimize(fileLen);

		setMaxNumOfThreads(1);
		setBlockSize(fileLen);

		return searchBigFile(f, searchBytes, numOfThreadsOptimized, false, startPosition);
	}

	/**
	 * Search bytes from big file faster in a concurrent processing with
	 * progress callback
	 * 
	 * @param f
	 *            target file
	 * @param searchBytes
	 *            sequence of bytes you want to search
	 * @return
	 */
	public List<Long> searchBigFile(File f, byte[] searchBytes) {
		return searchBigFile(f, searchBytes, null);
	}

	/**
	 * Search bytes from big file faster in a concurrent processing with
	 * progress callback
	 * 
	 * @param f
	 *            target file
	 * @param searchBytes
	 *            sequence of bytes you want to search
	 * @param listener
	 *            callback for progress
	 * @return
	 */
	public List<Long> searchBigFile(File f, byte[] searchBytes, OnProgressListener listener) {

		this.onRealtimeResultListener = null;
		this.onProgressListener = listener;

		int numOfThreadsOptimized = (int) (f.length() / (long) blockSize);

		if (numOfThreadsOptimized == 0) {
			numOfThreadsOptimized = 1;
		}

		return searchBigFile(f, searchBytes, numOfThreadsOptimized, this.useOptimization, 0);
	}

	/**
	 * Search bytes faster in a concurrent processing with concurrency level.
	 * 
	 * @param srcFile
	 *            target file
	 * @param searchBytes
	 *            sequence of bytes you want to search
	 * @param numOfThreads
	 *            number of threads
	 * @param useOptimization
	 *            use optimization or not
	 * @param startPosition
	 *            starting position
	 * @return
	 */
	private List<Long> searchBigFile(File srcFile, byte[] searchBytes, int numOfThreads, boolean useOptimization, long startPosition) {

		progressCache = null;

		if (useOptimization) {
			optimize(srcFile.length());
		}

		_profile_lastStartTime = System.currentTimeMillis();

		if (numOfThreads == 0) {
			numOfThreads = 1;
		}

		final long sizeOfSrcBytes = srcFile.length();
		final int sizeOfSearchBytes = searchBytes.length;
		final long bytesToReadBlockSize = (sizeOfSrcBytes - (long) sizeOfSearchBytes) / (long) numOfThreads;

		final int threadPoolSize;

		if (maxNumOfThreads == THREADS_NO_LIMIT) {
			threadPoolSize = numOfThreads;
		} else {
			threadPoolSize = maxNumOfThreads;
		}

		final ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

		final List<Future<List<Long>>> futureList = new ArrayList<Future<List<Long>>>();

		for (int i = 0; i < numOfThreads; i++) {

			final long offset = bytesToReadBlockSize * (long) i + startPosition;
			final long readLeng;

			if (i == numOfThreads - 1) {

				// if it's the last element.
				readLeng = sizeOfSrcBytes - offset;
			} else {
				// else , add the overlapping part size to blockSize
				readLeng = bytesToReadBlockSize + sizeOfSearchBytes;
			}

			final BinFileProgressListenerEx progressListener;

			if (onProgressListener == null && onRealtimeResultListener == null) {
				progressListener = null;
			} else {

				progressListener = new BinFileProgressListenerEx() {

					@Override
					public void onProgress(int workerNumber, int workerSize, List<Long> pointerList, float progress) {
						BigFileSearcher.this.onProgress(workerNumber, workerSize, pointerList, progress);
					}
				};
			}

			final int workerSize = numOfThreads;
			final int workerNumber = i;

			final Future<List<Long>> future = executorService.submit(new BigFileSearchTask(srcFile, searchBytes, offset, readLeng, workerNumber, workerSize, progressListener));

			futureList.add(future);
		}
		executorService.shutdown();

		// Remove duplicate indexes
		final List<Long> resultIndexList = new CopyOnWriteArrayList<Long>();

		for (Future<List<Long>> future : futureList) {

			try {
				List<Long> rawIndexList = future.get();

				for (int i = 0; i < rawIndexList.size(); i++) {

					Long longVal = rawIndexList.get(i);

					if (resultIndexList.contains(longVal)) {
						// if already exists , skip
					} else {
						resultIndexList.add(longVal);
					}
				}
			} catch (InterruptedException e) {
			} catch (ExecutionException e) {
			}

		}

		// Sort in ascending order
		binFileSearcher.sort(resultIndexList);

		_profile_lastEndTime = System.currentTimeMillis();

		return resultIndexList;
	}

	private final class BigFileSearchTask implements Callable<List<Long>> {

		final int workerSize;
		final int workerNumber;
		final File srcFile;
		final byte[] searchBytes;

		final long startPostion;
		final long readLeng;

		final BinFileProgressListenerEx binFileProgressListener;

		BigFileSearchTask(File srcFile, byte[] searchBytes, long startPosition, long readLeng, int workerNumber, int workerSize, BinFileProgressListenerEx listener) {

			this.srcFile = srcFile;
			this.startPostion = startPosition;
			this.readLeng = readLeng;
			this.searchBytes = searchBytes;
			this.binFileProgressListener = listener;
			this.workerNumber = workerNumber;
			this.workerSize = workerSize;
		}

		public List<Long> call() throws Exception {

			final BinFileSearcher blockSearchWorker = new BinFileSearcher();
			blockSearchWorker.setBufferSize(bufferSizePerWorker);
			blockSearchWorker.setSubThreadSize(subThreadSize);
			blockSearchWorker.setSubBufferSize(subBufferSize);

			if (this.binFileProgressListener != null) {
				blockSearchWorker.setBigFileProgressListener(new BinFileProgressListener() {

					@Override
					public void onProgress(List<Long> pointerList, float progress, float currentPosition, float startPosition, long maxSizeToRead) {
						binFileProgressListener.onProgress(workerNumber, workerSize, pointerList, progress);
					}
				});
			}

			final List<Long> pointerList = blockSearchWorker.searchPartially(srcFile, searchBytes, startPostion, readLeng);

			return pointerList;
		}
	}

	final static class ProgressCache {

		volatile float[] progress;
		volatile List<Long>[] pointerList;

		final List<Long> resultPointerList = new ArrayList<Long>();

		final boolean useResultCache;
		final int workerSize;

		@SuppressWarnings("unchecked")
		ProgressCache(int workerSize, boolean useResultCache) {
			this.workerSize = workerSize;
			this.progress = new float[workerSize];

			this.useResultCache = useResultCache;

			if (useResultCache) {
				this.pointerList = new CopyOnWriteArrayList[workerSize];
			}
		}

		synchronized void setProgress(int workerNumber, float progress, List<Long> pointerList) {
			this.progress[workerNumber] = progress;
			if (useResultCache) {
				if (this.pointerList[workerNumber] == null) {
					this.pointerList[workerNumber] = new CopyOnWriteArrayList<Long>();
				}
				this.pointerList[workerNumber].clear();
				this.pointerList[workerNumber].addAll(pointerList);
			}
		}

		// This is now only for realtime result callback interface.
		// So now not need to be thinking of multi thread but ready for multi
		// threading.
		List<Long> getResultPointers() {
			resultPointerList.clear();
			for (int i = 0; i < this.pointerList.length; i++) {
				resultPointerList.addAll(this.pointerList[i]);
			}
			// TODO add sort if needed
			return resultPointerList;
		}

		float getProgress() {
			float progress = 0;
			for (int i = 0; i < workerSize; i++) {
				progress += this.progress[i];
			}
			return progress / (float) workerSize;
		}
	}

	// Call from each worker thread
	private void onProgress(final int workerNumber, final int workerSize, final List<Long> pointerList, final float progress) {

		if (progressCache == null) {
			progressCache = new ProgressCache(workerSize, (onRealtimeResultListener != null));
		}

		progressCache.setProgress(workerNumber, progress, pointerList);

		if (onProgressListener != null) {
			onProgressListener.onProgress(progressCache.getProgress());
		}

		if (onRealtimeResultListener != null) {
			onRealtimeResultListener.onRealtimeResultListener(progressCache.getProgress(), progressCache.getResultPointers());
		}
	}

	/**
	 * 
	 * Get operation time in millis of last search
	 * 
	 * @return
	 */
	public long getEllapsedMillis() {
		return _profile_lastEndTime - _profile_lastStartTime;
	}

	/**
	 * Profiling method<br>
	 */
	public void _showProfile() {
		System.out.println("availableProcessors=" + Runtime.getRuntime().availableProcessors() + " free memory=" + getMegaBytes(Runtime.getRuntime().freeMemory()));
		System.out.println(
				"worker blockSize=" + getMegaBytes(blockSize) + " " +
						"worker buffer Size=" + getMegaBytes(bufferSizePerWorker) + ", " +
						"max num of thread=" + maxNumOfThreads + ", " +
						"sub buffer size=" + subBufferSize + "(B)" + ", " +
						"sub thread size=" + subThreadSize + ", "
				);

		System.out.println("possible max thread=" + maxNumOfThreads * subThreadSize + " " +
				"possible max memory=" + getMegaBytes(bufferSizePerWorker * maxNumOfThreads + (subBufferSize * subThreadSize))
				);
	}

	/**
	 * Optimize threading and memory
	 * 
	 * @param fileLength
	 */
	private void optimize(long fileLength) {

		final int availableProcessors = Runtime.getRuntime().availableProcessors();

		final long free = Runtime.getRuntime().freeMemory() / 2;

		int workerSize = availableProcessors / 2;
		if (workerSize < 2) {
			workerSize = 2;
		}

		long bufferSize = free / workerSize;
		if (bufferSize > 1 * 1024 * 1024) {
			bufferSize = 1 * 1024 * 1024;
		}

		long blockSize = fileLength / workerSize;
		if (blockSize > 1 * 1024 * 1024) {
			blockSize = 1 * 1024 * 1024;
		}
		int iBlockSize = (int) blockSize;

		if (bufferSize > blockSize) {
			bufferSize = blockSize;
		}
		int iBufferSize = (int) bufferSize;

		this.setBlockSize(iBlockSize);
		this.setMaxNumOfThreads(workerSize);
		this.setBufferSizePerWorker(iBufferSize);
		this.setSubBufferSize(256);

	}

	private String getMegaBytes(long valBytes) {
		return String.format("%.1f(MB)", ((float) valBytes / (1024f * 1024f)));
	}

}
