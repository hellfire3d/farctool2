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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Helper class for finbin test<br>
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 * 
 */
public class BinaryUtil {

	/**
	 * Get UTF-8 without BOM encoded bytes from String
	 * 
	 * @param text
	 * @return
	 */
	public static byte[] getBytes(String text) {
		byte[] bytes = new byte[] {};
		try {
			bytes = text.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		return bytes;
	}

	/**
	 * Copy src bytes[] into dest bytes[]
	 * 
	 * @param dest
	 * @param src
	 * @param copyToIndex
	 */
	public static void memcopy(byte[] dest, byte[] src, int copyToIndex) {
		System.arraycopy(src, 0, dest, copyToIndex, src.length);
	}

	/**
	 * load from file
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] loadBytesFromFile(File file) {

		byte[] bytes = null;
		try {
			bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	/**
	 * save to file
	 * 
	 * @param data
	 * @param file
	 */
	public static void saveBytesToFile(byte[] data, File file) {
		if (data == null) {
			return;
		}
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(data);
			bos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Pretty print byte[] with Ascii char and subscript of the array
	 * 
	 * @param array
	 */
	public static void print(byte[] array) {
		String[] name = new String[] { "+0", "+1", "+2", "+3", "+4", "+5", "+6", "+7", "+8", "+9", "+a", "+b", "+c", "+d", "+e", "+f" };
		for (int i = 0; i < 16; i++) {
			System.out.print(name[i] + " ");
		}
		System.out.println();
		for (int i = 0; i < array.length; i++) {

			if (i != 0 && i % 16 == 0) {
				System.out.println();
			}
			byte b = array[i];

			System.out.print(String.format("%02x", b).toUpperCase() + " ");

		}
		System.out.println();

	}

	/**
	 * Pretty print byte[] with Ascii char
	 * 
	 * @param array
	 */
	public static void printWithAscii(byte[] array) {
		String[] name = new String[] { "+0", "+1", "+2", "+3", "+4", "+5", "+6", "+7", "+8", "+9", "+a", "+b", "+c", "+d", "+e", "+f" };
		for (int i = 0; i < 16; i++) {
			System.out.print(name[i] + " " + "   ");
		}
		System.out.println();
		for (int i = 0; i < array.length; i++) {

			if (i != 0 && i % 16 == 0) {
				System.out.println();
			}
			byte b = array[i];

			String str = " ";
			if (32 <= (int) b && (int) b <= 126) {
				str = String.valueOf((char) b);
			}
			System.out.print(String.format("%02x", b).toUpperCase() + "(" + str + ")" + " ");

		}
		System.out.println();

	}

	/**
	 * Pretty print byte[] with Ascii char and subscript of the array
	 * 
	 * @param array
	 */
	public static void printWithAsciiWithIndex(byte[] array) {
		String[] name = new String[] { "+0", "+1", "+2", "+3", "+4", "+5", "+6", "+7", "+8", "+9", "+a", "+b", "+c", "+d", "+e", "+f" };
		for (int i = 0; i < 16; i++) {
			System.out.print(name[i] + " " + "   " + "     ");
		}
		System.out.println();
		for (int i = 0; i < array.length; i++) {

			if (i != 0 && i % 16 == 0) {
				System.out.println();
			}
			byte b = array[i];

			String str = " ";
			if (32 <= (int) b && (int) b <= 126) {
				str = String.valueOf((char) b);
			}
			System.out.print(String.format("%02x", b).toUpperCase() + "(" + str + ")" + "[" + String.format("%03d", i) + "]" + " ");

		}
		System.out.println();

	}
}
