/*  finbin - Hi-speed search byte[] data from big byte[]
 *
 *  Copyright (c) 2015 Tom Misawa(riversun.org@gmail.com)
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
package org.riversun.finbin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 
 * Search target big bytes(byte[]) from source bytes(byte[])<br>
 * <br>
 * This class is going to scan byte[] data in concurrent by using threads, it is
 * effective(means faster speed) in big size(kiloBytes-megaBytes) byte[] data.<br>
 *
 * @author Tom Misawa (riversun.org@gmail.com)
 */
public class BigBinarySearcher extends BinarySearcher {

	// max number of thread
	private static final int DEFAULT_MAX_NUM_OF_THREADS = 128;

	// thread number no limit
	public static final int THREADS_NO_LIMIT = 0;

	// magic number guided by experiment
	private static final int DEFAULT_ANALYZE_BYTE_ARRAY_UNIT_SIZE = 512;

	private int analyzeByteArrayUnitSize = DEFAULT_ANALYZE_BYTE_ARRAY_UNIT_SIZE;

	private int maxNumOfThreads = DEFAULT_MAX_NUM_OF_THREADS;

	/**
	 * Set buffer size for searching a sequence of bytes Increased the size of
	 * buffer does not means improving a performance<br>
	 * 
	 * @param bufferSize
	 */
	public void setBufferSize(int bufferSize) {
		this.analyzeByteArrayUnitSize = bufferSize;
	}

	/**
	 * Set max number of thread to concurrent access to byte[] data <br>
	 * Increased the number of threads does not means improving a performance<br>
	 * 
	 * @param maxNumOfThreads
	 */
	public void setMaxNumOfThreads(int maxNumOfThreads) {
		this.maxNumOfThreads = maxNumOfThreads;
	}

	/**
	 * Search bytes faster in a concurrent processing.
	 * 
	 * @param srcBytes
	 * @param searchBytes
	 * @return
	 */
	public List<Integer> searchBigBytes(byte[] srcBytes, byte[] searchBytes) {

		int numOfThreadsOptimized = (srcBytes.length / analyzeByteArrayUnitSize);

		if (numOfThreadsOptimized == 0) {
			numOfThreadsOptimized = 1;
		}

		return searchBigBytes(srcBytes, searchBytes, numOfThreadsOptimized);
	}

	/**
	 * Search bytes faster in a concurrent processing with concurrency level.
	 * 
	 * @param srcBytes
	 * @param searchBytes
	 * @param numOfThreads
	 * @return
	 */
	public List<Integer> searchBigBytes(byte[] srcBytes, byte[] searchBytes, int numOfThreads) {

		if (numOfThreads == 0) {
			numOfThreads = 1;
		}

		final int sizeOfSrcBytes = srcBytes.length;
		final int sizeOfSearchBytes = searchBytes.length;

		final int bytesToReadBlockSize = (srcBytes.length - (sizeOfSearchBytes)) / numOfThreads;

		final int threadPoolSize;

		if (maxNumOfThreads == THREADS_NO_LIMIT) {
			threadPoolSize = numOfThreads;
		} else {
			threadPoolSize = maxNumOfThreads;
		}

		final ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

		final List<Future<List<Integer>>> futureList = new ArrayList<Future<List<Integer>>>();

		for (int i = 0; i < numOfThreads; i++) {

			final int offset = bytesToReadBlockSize * i;
			final int readLeng;

			if (i == numOfThreads - 1) {
				// if it's the last element.
				readLeng = sizeOfSrcBytes - offset - 1;
			} else {
				// else , add the overlapping part size to blockSize
				readLeng = bytesToReadBlockSize + sizeOfSearchBytes;
			}

			final Future<List<Integer>> future = executorService.submit(new BinarySearchTask(srcBytes, searchBytes, offset, readLeng));

			futureList.add(future);
		}
		executorService.shutdown();

		// Remove duplicate indexes
		final List<Integer> resultIndexList = new CopyOnWriteArrayList<Integer>();

		for (Future<List<Integer>> future : futureList) {

			try {
				List<Integer> rawIndexList = future.get();

				for (int i = 0; i < rawIndexList.size(); i++) {

					Integer integer = rawIndexList.get(i);

					if (resultIndexList.contains(integer)) {
						// if already exists , skip
					} else {
						resultIndexList.add(integer);
					}
				}
			} catch (InterruptedException e) {
			} catch (ExecutionException e) {
			}

		}

		// Sort in ascending order
		resultIndexList.sort(new Comparator<Integer>() {

			public int compare(Integer num1, Integer num2) {
				if (num1 > num2) {
					return 1;
				} else if (num1 < num2) {
					return -1;
				}
				return 0;
			}
		});

		return resultIndexList;
	}

	final class BinarySearchTask implements Callable<List<Integer>> {

		final byte[] srcBytes;
		final byte[] searchBytes;

		final int offset;
		final int readLeng;

		BinarySearchTask(byte[] srcBytes, byte[] searchBytes, int offset, int readLeng) {
			this.srcBytes = srcBytes;
			this.offset = offset;
			this.readLeng = readLeng;
			this.searchBytes = searchBytes;
		}

		public List<Integer> call() throws Exception {
			final BinarySearcher binSearcher = new BinarySearcher();

			final List<Integer> resultIndexList = binSearcher.searchBytes(srcBytes, searchBytes, offset, offset + readLeng);
			return resultIndexList;
		}
	}

}
