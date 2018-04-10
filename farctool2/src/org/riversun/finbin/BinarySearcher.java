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
import java.util.List;

/**
 * 
 * Search target bytes(byte[]) from source bytes(byte[])<br>
 * <br>
 * This class is going to scan byte[] data to sequential, it is effective in
 * small size(less than 1kBytes) byte[] data.<br>
 *
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class BinarySearcher {

	/**
	 * Returns the index within this byte-array of the first occurrence of the
	 * specified(search bytes) byte array.<br>
	 * 
	 * @param srcBytes
	 * @param searchBytes
	 * @return
	 */
	public int indexOf(byte[] srcBytes, byte[] searchBytes) {
		final int startIndex = 0;
		final int endIndex = srcBytes.length - 1;
		return indexOf(srcBytes, searchBytes, startIndex, endIndex);
	}

	/**
	 * Returns the index within this byte-array of the first occurrence of the
	 * specified(search bytes) byte array.<br>
	 * Starting the search at the specified index<br>
	 * 
	 * @param srcBytes
	 * @param searchBytes
	 * @param startIndex
	 * @return
	 */
	public int indexOf(byte[] srcBytes, byte[] searchBytes, int startIndex) {
		final int endIndex = srcBytes.length - 1;
		return indexOf(srcBytes, searchBytes, startIndex, endIndex);
	}

	/**
	 * Returns the index within this byte-array of the first occurrence of the
	 * specified(search bytes) byte array.<br>
	 * Starting the search at the specified index, and end at the specified
	 * index.
	 * 
	 * @param srcBytes
	 * @param searchBytes
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public int indexOf(byte[] srcBytes, byte[] searchBytes, int startIndex, int endIndex) {

		final int searchBytesLeng = searchBytes.length;

		if (searchBytesLeng == 0 || (endIndex - startIndex + 1) < searchBytesLeng) {

			return -1;
		}

		final int srcBytesLeng = srcBytes.length;

		final int maxScanStartPosIdx = srcBytesLeng - searchBytesLeng;

		final int loopEndIdx;

		if (endIndex < maxScanStartPosIdx) {
			loopEndIdx = endIndex;
		} else {
			loopEndIdx = maxScanStartPosIdx;
		}

		int lastScanIdx = -1;

		label: // goto label
		for (int i = startIndex; i <= loopEndIdx; i++) {

			for (int j = 0; j < searchBytesLeng; j++) {

				if (srcBytes[i + j] != searchBytes[j]) {
					continue label;
				}

				lastScanIdx = i + j;

			}

			if (endIndex < lastScanIdx || lastScanIdx - i + 1 < searchBytesLeng) {
				// it becomes more than the last index
				// or less than the number of search bytes
				return -1;
			}

			return i;
		}
		return -1;
	}

	/**
	 * Search bytes in byte array returns indexes within this byte-array of all
	 * occurrences of the specified(search bytes) byte array.
	 * 
	 * @param srcBytes
	 * @param searchBytes
	 * @return result index list
	 */
	public List<Integer> searchBytes(byte[] srcBytes, byte[] searchBytes) {
		final int startIdx = 0;
		final int endIdx = srcBytes.length - 1;
		return searchBytes(srcBytes, searchBytes, startIdx, endIdx);
	}

	public List<Integer> searchBytes(byte[] srcBytes, byte[] searchBytes, int searchStartIndex) {
		final int endIdx = srcBytes.length - 1;
		return searchBytes(srcBytes, searchBytes, searchStartIndex, endIdx);
	}

	/**
	 * Search bytes in byte array returns indexes within this byte-array of all
	 * occurrences of the specified(search bytes) byte array in the specified
	 * range
	 * 
	 * @param srcBytes
	 * @param searchBytes
	 * @param searchStartIndex
	 * @param searchEndIndex
	 * @return result index list
	 */
	public List<Integer> searchBytes(byte[] srcBytes, byte[] searchBytes, int searchStartIndex, int searchEndIndex) {
		final int destSize = searchBytes.length;

		final List<Integer> positionIndexList = new ArrayList<Integer>();

		int cursor = searchStartIndex;

		while (cursor < searchEndIndex + 1) {

			final int index = indexOf(srcBytes, searchBytes, cursor, searchEndIndex);

			if (index >= 0) {

				positionIndexList.add(index);

				cursor = index + destSize;
			} else {
				cursor++;
			}
		}
		return positionIndexList;
	}

}
