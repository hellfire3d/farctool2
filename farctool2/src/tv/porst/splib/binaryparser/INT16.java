package tv.porst.splib.binaryparser;

/**
 * Represents a parsed short int.
 */
public final class INT16 extends ParsedType<Integer> {

	/**
	 * Creates a new short int object.
	 * 
	 * @param bytePosition Byte position of the short int in the input stream.
	 * @param value Integer value.
	 */
	public INT16(final int bytePosition, final int value) {
		super(bytePosition, 16, value);
	}
}