package tv.porst.splib.binaryparser;

/**
 * Represents a parsed unsigned bit field.
 */
public final class UBits extends ParsedType<Integer> {

	/**
	 * Creates a new unsigned bit field object.
	 * 
	 * @param bitPosition Bit position of the unsigned bit field in the input stream.
	 * @param bitLength Length of the field in bits.
	 * @param value Unsigned bit field value.
	 */
	public UBits(final int bitPosition, final int bitLength, final Integer value) {
		super(bitPosition, bitLength, value);
	}
}