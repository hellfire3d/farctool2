package tv.porst.splib.binaryparser;


/**
 * Class that can be used to parse simple data structures from
 * byte streams.
 */
public class BinaryParser {

	/**
	 * The data to parse.
	 */
	private final byte[] data;

	/**
	 * The current byte position.
	 */
	private int bytePosition = 0;

	/**
	 * The current bit position.
	 */
	private int bitPosition = 0;

	/**
	 * Creates a new binary parser object that parses data from
	 * the given byte array.
	 * 
	 * @param data The source array.
	 */
	public BinaryParser(final byte[] data) {

		if (data == null) {
			throw new IllegalArgumentException("Data argument must not be null");
		}

		this.data = data.clone();
	}

	/**
	 * Aligns the parser to the next byte.
	 */
	public void align() {
		if (bitPosition != 0) {
			bytePosition++;
			bitPosition = 0;
		}
	}

	/**
	 * Returns the current bit position.
	 * 
	 * @return The current bit position.
	 */
	public int getBitPosition() {
		return bitPosition;
	}

	/**
	 * Returns the current byte position.
	 * 
	 * @return The current byte position.
	 */
	public int getBytePosition() {
		return bytePosition;
	}

	/**
	 * Returns the length of the byte stream.
	 * 
	 * @return The length of the byte stream.
	 */
	public int getLength() {
		return data.length;
	}

	/**
	 * Returns whether parsing the whole byte stream is complete.
	 * 
	 * @return True, if parsing is complete. False, if there is data left to parse.
	 */
	public boolean isDone() {
		return bytePosition == data.length;
	}

	/**
	 * Peeks at the next few bits without moving the current parsing
	 * position forward.
	 * 
	 * @param numberOfBits The number of bits to peek at. This value must be between 0 and 32.
	 * 
	 * @return The peeked bits.
	 */
	public UBits peekBits(final int numberOfBits) {

		if (numberOfBits < 0 || numberOfBits > 32) {
			throw new IllegalArgumentException("Number of bits argument must be between 0 and 32");
		}

		final int bytePosition = this.bytePosition;
		final int bitPosition = this.bitPosition;

		final UBits value = readBits(numberOfBits);

		this.bitPosition = bitPosition;
		this.bytePosition = bytePosition;

		return value;
	}

	/**
	 * Peeks at the next UINT16 value without moving the current parsing
	 * position forward.
	 * 
	 * @return The peeked UINT16 value.
	 */
	public UINT16 peekUInt16() {

		final int bytePosition = this.bytePosition;
		final int bitPosition = this.bitPosition;

		final UINT16 value = readUInt16();

		this.bitPosition = bitPosition;
		this.bytePosition = bytePosition;

		return value;
	}

	/**
	 * Peeks at the next UINT32 value without moving the current parsing
	 * position forward.
	 * 
	 * @return The peeked UINT32 value.
	 */
	public UINT32 peekUInt32() {

		final int bytePosition = this.bytePosition;
		final int bitPosition = this.bitPosition;

		final UINT32 value = readUInt32();

		this.bitPosition = bitPosition;
		this.bytePosition = bytePosition;

		return value;
	}

	/**
	 * Peeks at the next UINT8 value without moving the current parsing
	 * position forward.
	 * 
	 * @return The peeked UINT8 value.
	 */
	public UINT8 peekUInt8() {

		final int bytePosition = this.bytePosition;
		final int bitPosition = this.bitPosition;

		final UINT8 value = readUInt8();

		this.bitPosition = bitPosition;
		this.bytePosition = bytePosition;

		return value;
	}

	/**
	 * Reads the next few bits from the byte stream.
	 * 
	 * @param numberOfBits The number of bits to read. This value must be between 0 and 32.
	 * 
	 * @return The read bits.
	 */
	public UBits readBits(final int numberOfBits) {

		if (bytePosition * 8 + bitPosition + numberOfBits > data.length * 8) {
			throw new IllegalArgumentException("Not enough data left");
		}

		final int oldBytePosition = bytePosition;
		final int oldBitPosition = bitPosition;

		int value = 0;

		for (int i=0;i<numberOfBits;i++) {
			value = (value << 1) | ((data[bytePosition] >> (7 - bitPosition)) & 1);

			bitPosition++;

			if (bitPosition == 8) {
				bitPosition = 0;
				bytePosition++;
			}
		}

		return new UBits(8 * oldBytePosition + oldBitPosition, numberOfBits, value);
	}

	/**
	 * Reads the next byte from the byte stream.
	 * 
	 * @return The read byte.
	 */
	public byte readByte() {

		if (bytePosition * 8 + bitPosition + 8 > data.length * 8) {
			throw new IllegalArgumentException("Not enough data left");
		}

		if (bitPosition != 0) {
			throw new IllegalStateException("Parser is not byte aligned");
		}

		return data[bytePosition++];
	}

	/**
	 * Reads the next bit from the byte stream.
	 * 
	 * @return The read bit.
	 */
	public Flag readFlag() {

		final int bytePosition = this.bytePosition;
		final int bitPosition = this.bitPosition;

		final UBits value = readBits(1);

		return new Flag(8 * bytePosition + bitPosition, value.value() == 1);
	}

	/**
	 * Reads the next float from the byte stream.
	 * 
	 * @return The next float.
	 */
	public Float32 readFloat() {

		final float value = Float.intBitsToFloat(readInt32().value());

		return new Float32(8 * bytePosition + bitPosition - 32, value);
	}

	/**
	 * Reads the next short float from the byte stream.
	 * 
	 * @return The next short float.
	 */
	public Float16 readFloat16() {
		readInt16();

		return new Float16(8 * bytePosition + bitPosition - 16, (float) 0.0);
	}

	/**
	 * Reads the next float from the byte stream.
	 * 
	 * @return The next float.
	 */
	public Float64 readFloat64() {

		final double value = Double.longBitsToDouble(readInt64().value());

		return new Float64(8 * bytePosition + bitPosition - 64, value);
	}

	/**
	 * Reads the next short integer from the byte stream.
	 * 
	 * @return The next short integer.
	 */
	public INT16 readInt16() {

		if (bytePosition * 8 + bitPosition + 16 > data.length * 8) {
			throw new IllegalArgumentException("Not enough data left");
		}

		if (bitPosition != 0) {
			throw new IllegalStateException("Parser is not byte aligned");
		}

		final int firstByte = readByte() & 0xFF;
		final int secondByte = readByte() & 0xFF;

		return new INT16(8 * bytePosition + bitPosition - 16, secondByte << 8 | firstByte);
	}

	/**
	 * Reads the next three-byte integer from the byte stream.
	 * 
	 * @return The next three-byte integer.
	 */
	public INT24 readInt24() {

		if (bytePosition * 8 + bitPosition + 24 > data.length * 8) {
			throw new IllegalArgumentException("Not enough data left");
		}

		if (bitPosition != 0) {
			throw new IllegalStateException("Parser is not byte aligned");
		}

		final int firstByte = readByte() & 0xFF;
		final int secondByte = readByte() & 0xFF;
		final int thirdByte = readByte() & 0xFF;

		int value = thirdByte << 16 | secondByte << 8 | firstByte;

		if (value > 0x7FFFFF) {
			value = value - 0x1000000;
		}

		return new INT24(8 * bytePosition + bitPosition - 24, value);
	}

	/**
	 * Reads the next integer from the byte stream.
	 * 
	 * @return The next integer.
	 */
	public INT32 readInt32() {

		if (bytePosition * 8 + bitPosition + 32 > data.length * 8) {
			throw new IllegalArgumentException("Not enough data left");
		}

		if (bitPosition != 0) {
			throw new IllegalStateException("Parser is not byte aligned");
		}

		final int firstByte = readByte() & 0xFF;
		final int secondByte = readByte() & 0xFF;
		final int thirdByte = readByte() & 0xFF;
		final int fourthByte = readByte() & 0xFF;

		return new INT32(8 * bytePosition + bitPosition - 32, fourthByte << 24 | thirdByte << 16 | secondByte << 8 | firstByte);
	}

	/**
	 * Reads the next long from the byte stream.
	 * 
	 * @return The next integer.
	 */
	public INT64 readInt64() {

		if (bytePosition * 8 + bitPosition + 64 > data.length * 8) {
			throw new IllegalArgumentException("Not enough data left");
		}

		if (bitPosition != 0) {
			throw new IllegalStateException("Parser is not byte aligned");
		}

		final int firstByte = readByte() & 0xFF;
		final int secondByte = readByte() & 0xFF;
		final int thirdByte = readByte() & 0xFF;
		final int fourthByte = readByte() & 0xFF;
		final int fifthByte = readByte() & 0xFF;
		final int sixthByte = readByte() & 0xFF;
		final int seventhByte = readByte() & 0xFF;
		final int eigthByte = readByte() & 0xFF;

		return new INT64(8 * bytePosition + bitPosition - 64, eigthByte << 56 | seventhByte << 48 | sixthByte << 40 | fifthByte << 32 | fourthByte << 24 | thirdByte << 16 | secondByte << 8 | firstByte);
	}

	/**
	 * Reads the next few signed bits from the byte stream.
	 * 
	 * @param numberOfBits The number of signed bits to read. This value must be between 0 and 32.
	 * 
	 * @return The read signed bits.
	 */
	public Bits readSBits(final int numberOfBits) {

		if (bytePosition * 8 + bitPosition + numberOfBits > data.length * 8) {
			throw new IllegalArgumentException("Not enough data left");
		}

		final int oldBytePosition = bytePosition;
		final int oldBitPosition = bitPosition;

		int value = 0;

		for (int i=0;i<numberOfBits;i++) {
			value = (value << 1) | ((data[bytePosition] >> (7 - bitPosition)) & 1);

			bitPosition++;

			if (bitPosition == 8) {
				bitPosition = 0;
				bytePosition++;
			}
		}

		return new Bits(8 * oldBytePosition + oldBitPosition, numberOfBits, value);
	}

	/**
	 * Reads the next 0-terminated ASCII string from the byte stream.
	 * 
	 * @return The read string.
	 */
	public AsciiString readString() {

		final int bytePosition = this.bytePosition;
		final int bitPosition = this.bitPosition;

		final StringBuffer value = new StringBuffer();

		byte b;

		while ((b = readByte()) != 0)
		{
			value.append((char) b);
		}

		return new AsciiString(8 * bytePosition + bitPosition, value.toString(), true);
	}

	/**
	 * Reads the next number of bytes from the byte stream into an ASCII string.
	 * 
	 * @param numberOfBytes The number of bytes to add to the string.
	 * 
	 * @return The read string.
	 */
	public AsciiString readString(final int numberOfBytes) {

		if (numberOfBytes < 0) {
			throw new IllegalArgumentException("The number of bytes in the string can not be negative");
		}

		final StringBuffer value = new StringBuffer();

		for (int i=0;i<numberOfBytes;i++)
		{
			value.append((char) readByte());
		}

		return new AsciiString(8 * bytePosition - 8 * numberOfBytes, value.toString(), false);
	}

	/**
	 * Reads the next unsigned short integer from the byte stream.
	 * 
	 * @return The next unsigned short integer.
	 */
	public UINT16 readUInt16() {

		if (bytePosition * 8 + bitPosition + 16 > data.length * 8) {
			throw new IllegalArgumentException("Not enough data left");
		}

		if (bitPosition != 0) {
			throw new IllegalStateException("Parser is not byte aligned");
		}

		final int firstByte = readByte() & 0xFF;
		final int secondByte = readByte() & 0xFF;

		return new UINT16(8 * bytePosition + bitPosition - 16, secondByte << 8 | firstByte);
	}

	/**
	 * Reads the next unsigned integer from the byte stream.
	 * 
	 * @return The next unsigned integer.
	 */
	public UINT32 readUInt32() {

		if (bytePosition * 8 + bitPosition + 32 > data.length * 8) {
			throw new IllegalArgumentException("Not enough data left");
		}

		if (bitPosition != 0) {
			throw new IllegalStateException("Parser is not byte aligned");
		}

		final int firstByte = readByte() & 0xFF;
		final int secondByte = readByte() & 0xFF;
		final int thirdByte = readByte() & 0xFF;
		final int fourthByte = readByte() & 0xFF;

		return new UINT32(8 * bytePosition + bitPosition - 32, fourthByte << 24 | thirdByte << 16 | secondByte << 8 | firstByte);
	}

	/**
	 * Reads the next unsigned byte from the byte stream.
	 * 
	 * @return The next unsigned byte.
	 */
	public UINT8 readUInt8() {

		if (bytePosition * 8 + bitPosition + 32 > data.length * 8) {
			throw new IllegalArgumentException("Not enough data left");
		}

		if (bitPosition != 0) {
			throw new IllegalStateException("Parser is not byte aligned");
		}

		final int firstByte = readByte() & 0xFF;

		return new UINT8(8 * bytePosition + bitPosition - 8, firstByte);
	}

	/**
	 * Sets the current read position in the byte stream.
	 * 
	 * @param bytePosition The new byte position.
	 * @param bitPosition The new bit position.
	 */
	public void setPosition(final int bytePosition, final int bitPosition) {

		if (bytePosition < 0) {
			throw new IllegalArgumentException("Byte position argument must not be negative");
		}

		if (bitPosition < 0) {
			throw new IllegalArgumentException("Bit position argument must not be negative");
		}

		if (bytePosition * 8 + bitPosition > data.length * 8) {
			throw new IllegalArgumentException("Can not move read position beyond the end of the input buffer");
		}

		this.bytePosition = bytePosition;
		this.bitPosition = bitPosition;
	}
}