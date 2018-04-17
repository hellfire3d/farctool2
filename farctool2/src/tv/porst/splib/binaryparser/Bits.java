package tv.porst.splib.binaryparser;

/**
 * Represents a parsed bit field.
 */
public final class Bits extends ParsedType<Integer> {

	/**
	 * Creates a new bit field object.
	 * 
	 * @param bitPosition Bit position of the bit field in the input stream.
	 * @param bitLength Length of the field in bits.
	 * @param value Bit field value.
	 */
	public Bits(final int bitPosition, final int bitLength, final Integer value) {
		super(bitPosition, bitLength, value);
	}
}