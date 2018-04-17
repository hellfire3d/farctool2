package tv.porst.splib.binaryparser;

/**
 * Represents a parsed float.
 */
public final class Float32 extends ParsedType<Float> {

	/**
	 * Creates a new float object.
	 * 
	 * @param bytePosition Byte position of the float in the input stream.
	 * @param value Float value.
	 */
	public Float32(final int bytePosition, final Float value) {
		super(bytePosition, 32, value);
	}
}
