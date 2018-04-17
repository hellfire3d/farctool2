package tv.porst.splib.binaryparser;

/**
 * Base class for all parsed types.
 *
 * @param <ValueType> Type of the parsed value.
 */
public abstract class ParsedType<ValueType> implements IFileElement {

	/**
	 * Bit position of the parsed value in the input stream.
	 */
	private final int bitPosition;

	/**
	 * Parsed value.
	 */
	private final ValueType value;

	/**
	 * Length of the field in bits.
	 */
	private final int bitLength;

	/**
	 * Creates a new parsed type object.
	 * 
	 * @param bitPosition Bit position of the parsed value in the input stream.
	 * @param bitLength Length of the field in bits.
	 * @param value Parsed value.
	 */
	public ParsedType(final int bitPosition, final int bitLength, final ValueType value) {

		if (bitPosition < 0) {
			throw new IllegalArgumentException("Byte position must not be negative");
		}

		if (value == null) {
			throw new IllegalArgumentException("Value argument must not be null");
		}

		this.bitPosition = bitPosition;
		this.bitLength = bitLength;
		this.value = value;
	}

	@Override
	public int getBitLength() {
		return bitLength;
	}

	/**
	 * Returns the byte position of the parsed value in the input stream.
	 *
	 * @return The byte position of the parsed value in the input stream.
	 */
	@Override
	public int getBitPosition() {
		return bitPosition;
	}

	/**
	 * Returns the parsed value.
	 * 
	 * @return The parsed value.
	 */
	public ValueType value() {
		return value;
	}
}