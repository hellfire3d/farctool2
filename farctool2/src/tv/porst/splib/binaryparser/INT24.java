package tv.porst.splib.binaryparser;

/**
 * Represents a parsed 3-byte int.
 */
public final class INT24 extends ParsedType<Integer> {

	/**
	 * Creates a new 3-byte int object.
	 * 
	 * @param bytePosition Byte position of the 3-byte int in the input stream.
	 * @param value Integer value.
	 */
	public INT24(final int bytePosition, final int value) {
		super(bytePosition, 24, value);
	}
}