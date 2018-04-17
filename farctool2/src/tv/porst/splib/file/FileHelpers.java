package tv.porst.splib.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Contains helper functions for working with files.
 */
public final class FileHelpers {

	/**
	 * Extracts the filename from a full path.
	 * 
	 * @param fullPath The full input path.
	 * 
	 * @return The raw filename.
	 */
	public static String extractFilename(final String fullPath) {

		final int index = fullPath.lastIndexOf(File.separator);

		return index == -1 ? fullPath : fullPath.substring(index + 1);
	}

	/**
	 * Reads a whole file into a byte array.
	 * 
	 * @param file The input file.
	 * 
	 * @return The bytes read from the file.
	 * 
	 * @throws IOException Thrown if the file could not be read.
	 */
	public static byte[] readFile(final File file) throws IOException {

		if (file == null) {
			throw new IllegalArgumentException("File argument must not be null");
		}

		final FileInputStream inputStream = new FileInputStream(file);

		final byte[] data = new byte[(int) file.length()];
		inputStream.read(data);

		return data;
	}

	/**
	 * Reads a whole file into a byte array.
	 * 
	 * @param file The input file.
	 * 
	 * @return The bytes read from the file.
	 * 
	 * @throws IOException Thrown if the file could not be read.
	 */
	public static byte[] readFile(final String file) throws IOException {

		if (file == null) {
			throw new IllegalArgumentException("File argument must not be null");
		}

		return readFile(new File(file));
	}

	/**
	 * Writes binary data to a file.
	 * 
	 * @param file The file to write to.
	 * @param data The data to write.
	 * 
	 * @throws IOException Thrown if writing the data to the byte failed.
	 */
	public static void writeFile(final File file, final byte[] data) throws IOException {

		if (file == null) {
			throw new IllegalArgumentException("File argument must not be null");
		}

		final FileOutputStream inputStream = new FileOutputStream(file);

		inputStream.write(data);

		inputStream.close();
	}

	/**
	 * Writes binary data to a file.
	 * 
	 * @param fileName The name of the file to write to.
	 * @param data The data to write.
	 * 
	 * @throws IOException Thrown if writing the data to the byte failed.
	 */
	public static void writeFile(final String fileName, final byte[] data) throws IOException {

		if (fileName == null) {
			throw new IllegalArgumentException("File name argument must not be null");
		}

		final File file = new File(fileName);

		final FileOutputStream inputStream = new FileOutputStream(file);

		inputStream.write(data);

		inputStream.close();
	}
}