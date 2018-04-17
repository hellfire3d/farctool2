package tv.porst.splib.binaryparser;

/**
 * Represents a parsed unsigned short int.
 */
public final class UINT16 extends ParsedType<Integer> implements IParsedINTElement {

	/**
	 * Length of the type in bytes.
	 */
	public static final int BYTE_LENGTH = 2;

	/**
	 * Creates a new unsigned short int object.
	 * 
	 * @param bytePosition Byte position of the unsigned short int in the input stream.
	 * @param value Integer value.
	 */
	public UINT16(final int bytePosition, final int value) {
		super(bytePosition, 16, value);
	}
}