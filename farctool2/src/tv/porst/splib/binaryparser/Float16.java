package tv.porst.splib.binaryparser;

/**
 * Represents a parsed short float.
 */
public class Float16 extends ParsedType<Float> {

	/**
	 * Creates a new short float object.
	 * 
	 * @param bytePosition Byte position of the short float in the input stream.
	 * @param value Float value.
	 */
	public Float16(final int bytePosition, final Float value) {
		super(bytePosition, 16, value);
	}
}