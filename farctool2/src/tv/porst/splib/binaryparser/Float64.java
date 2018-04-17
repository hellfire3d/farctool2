package tv.porst.splib.binaryparser;

/**
 * Represents a parsed float.
 */
public final class Float64 extends ParsedType<Double> {

	/**
	 * Creates a new float object.
	 * 
	 * @param bytePosition Byte position of the float in the input stream.
	 * @param value Float value.
	 */
	public Float64(final int bytePosition, final Double value) {
		super(bytePosition, 64, value);
	}
}
