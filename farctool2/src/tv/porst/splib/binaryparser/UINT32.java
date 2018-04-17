package tv.porst.splib.binaryparser;

/**
 * Represents a parsed unsigned int.
 */
public class UINT32 extends ParsedType<Long> implements IParsedINTElement {

	/**
	 * Length of the type in bytes.
	 */
	public static final int BYTE_LENGTH = 4;

	/**
	 * Creates a new unsigned int object.
	 * 
	 * @param bytePosition Byte position of the unsigned int in the input stream.
	 * @param value Integer value.
	 */
	public UINT32(final int bytePosition, final long value) {
		super(bytePosition, 32, value);
	}
}