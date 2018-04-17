package tv.porst.splib.binaryparser;

/**
 * Represents a parsed long.
 */
public final class INT64 extends ParsedType<Long> implements IParsedINTElement {

	/**
	 * Creates a new long object.
	 * 
	 * @param bytePosition Byte position of the long in the input stream.
	 * @param value Long value.
	 */
	public INT64(final int bytePosition, final long value) {
		super(bytePosition, 64, value);
	}
}
