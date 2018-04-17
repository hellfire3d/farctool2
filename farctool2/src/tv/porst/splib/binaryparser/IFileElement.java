package tv.porst.splib.binaryparser;

/**
 * Interface to be implemented by all parsed types.
 */
public interface IFileElement {

	/**
	 * Returns the length of the element in bits.
	 * 
	 * @return The length of the element in bits.
	 */
	int getBitLength();

	/**
	 * Returns the position of the element in bits.
	 * 
	 * @return The position of the element in bits.
	 */
	int getBitPosition();
}
