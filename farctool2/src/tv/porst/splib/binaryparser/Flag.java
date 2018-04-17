package tv.porst.splib.binaryparser;

/**
 * Represents a parsed flag.
 */
public final class Flag extends ParsedType<Boolean> {

	/**
	 * Creates a new flag object.
	 * 
	 * @param bitPosition Bit position of the flag in the input stream.
	 * @param value Floag value.
	 */
	public Flag(final int bitPosition, final boolean value) {
		super(bitPosition, 1, value);
	}
}