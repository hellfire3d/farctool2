package tv.porst.splib.binaryparser;

/**
 * Represents a parsed ASCII string.
 */
public final class AsciiString extends ParsedType<String> implements IFileElement {

	/**
	 * Flag that says whether the parsed string was zero-terminated or not.
	 */
	private final boolean zeroTerminated;

	/**
	 * Creates a new AsciiString object.
	 * 
	 * @param bytePosition Byte position of the string in the input stream.
	 * @param value String value.
	 * @param zeroTerminated Flag that says whether the parsed string was zero-terminated or not.
	 */
	public AsciiString(final int bytePosition, final String value, final boolean zeroTerminated) {

		super(bytePosition, (value.length() + (zeroTerminated ? 1 : 0)) * 8, value);

		this.zeroTerminated = zeroTerminated;
	}

	/**
	 * Returns the flag that says whether the parsed string was zero-terminated or not.
	 *
	 * @return The flag that says whether the parsed string was zero-terminated or not.
	 */
	public boolean isZeroTerminated() {
		return zeroTerminated;
	}
}