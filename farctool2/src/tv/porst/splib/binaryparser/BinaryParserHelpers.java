package tv.porst.splib.binaryparser;

/**
 * Contains helper functions for working with the binary parser.
 */
public final class BinaryParserHelpers {

	/**
	 * Checks whether a certain number of bits can be read from the input stream before
	 * the end of the stream is reached.
	 * 
	 * @param parser The parser whose stream is checked.
	 * @param numberOfBits The number of bits to check for.
	 * 
	 * @return True, if there are the requested number of bits left in the input stream. False, otherwise.
	 */
	public static boolean hasBitsLeft(final BinaryParser parser, final long numberOfBits) {

		if (parser == null) {
			throw new IllegalArgumentException("Parser argument must not be null");
		}

		if (numberOfBits < 0) {
			throw new IllegalArgumentException("Number of requested bits can not be negative");
		}

		return parser.getBytePosition() * 8 + parser.getBitPosition() + numberOfBits <= parser.getLength() * 8;
	}

	/**
	 * Checks whether a certain number of bytes can be read from the input stream before
	 * the end of the stream is reached.
	 * 
	 * @param parser The parser whose stream is checked.
	 * @param numberOfBytes The number of bytes to check for.
	 * 
	 * @return True, if there are the requested number of bytes left in the input stream. False, otherwise.
	 */
	public static boolean hasBytesLeft(final BinaryParser parser, final long numberOfBytes) {

		if (parser == null) {
			throw new IllegalArgumentException("Parser argument must not be null");
		}

		if (numberOfBytes < 0) {
			throw new IllegalArgumentException("Number of requested bytes can not be negative");
		}

		return hasBitsLeft(parser, numberOfBytes * 8);
	}

	/**
	 * Reads a byte array from the input stream of a parser.
	 * 
	 * @param parser The parser from which the byte array is read.
	 * @param length The length of the byte array to read.
	 * 
	 * @return The byte array.
	 */
	public static byte[] readByteArray(final BinaryParser parser, final int length) {

		if (parser == null) {
			throw new IllegalArgumentException("Parser argument must not be null");
		}

		if (length < 0) {
			throw new IllegalArgumentException("The length of the byte array must not be negative");
		}

		if (!hasBytesLeft(parser, length)) {
			throw new IllegalArgumentException("Not enough bytes left in the input stream");
		}

		final byte[] array = new byte[length];

		for (int i=0;i<array.length;i++) {
			array[i] = parser.readByte();
		}

		return array;
	}
}