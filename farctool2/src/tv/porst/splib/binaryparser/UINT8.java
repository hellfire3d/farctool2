package tv.porst.splib.binaryparser;

/**
 * Represents a parsed unsigned byte.
 */
public final class UINT8 extends ParsedType<Integer> implements IParsedINTElement {

	/**
	 * Length of the type in bytes.
	 */
	public static final int BYTE_LENGTH = 1;

	/**
	 * Creates a new unsigned byte object.
	 * 
	 * @param bytePosition Byte position of the unsigned byte in the input stream.
	 * @param value Integer value.
	 */
	public UINT8(final int bytePosition, final int value) {
		super(bytePosition, 8, value);
	}
}