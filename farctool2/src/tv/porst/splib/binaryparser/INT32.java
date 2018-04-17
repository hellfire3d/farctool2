package tv.porst.splib.binaryparser;

/**
 * Represents a parsed int.
 */
public final class INT32 extends ParsedType<Integer> implements IParsedINTElement {

	/**
	 * Length of the type in bytes.
	 */
	public static final int BYTE_LENGTH = 4;

	/**
	 * Creates a new short int object.
	 * 
	 * @param bytePosition Byte position of the int in the input stream.
	 * @param value Integer value.
	 */
	public INT32(final int bytePosition, final int value) {
		super(bytePosition, 32, value);
	}
}
