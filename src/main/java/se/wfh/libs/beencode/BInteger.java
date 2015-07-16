package se.wfh.libs.beencode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class BInteger extends BNode<Long> {
	private static final long serialVersionUID = 1L;
	private static final byte PREFIX = 'i';
	private static final byte SUFFIX = 'e';

	public static BInteger of(long value) {
		return new BInteger(value);
	}

	public static BInteger of(Long value) {
		return new BInteger(value);
	}

	static BInteger empty() {
		return of(null);
	}

	protected static boolean canParsePrefix(byte prefix) {
		return prefix == PREFIX;
	}

	private BInteger(Long value) {
		super(value);
	}

	@Override
	protected BInteger clone() {
		return BInteger.of(value);
	}

	public void setValue(Long value) {
		this.value = value;
	}

	@Override
	protected void writeTo(OutputStream os) throws IOException {
		os.write(PREFIX);
		os.write(String.valueOf(value).getBytes(StandardCharsets.US_ASCII));
		os.write(SUFFIX);
	}

	@Override
	protected void readFrom(InputStream is, byte prefix) throws IOException {
		StringBuilder str = new StringBuilder(21);
		byte read = 0;
		for (int i = 0; i < 21; i++) {
			read = (byte) is.read();

			if (read == SUFFIX) {
				break;
			}

			str.append((char) read);
		}

		if (read != SUFFIX) {
			throw new BencodeException(
					"Invalid data, did not find suffix within 21 bytes");
		}

		String input = str.toString();
		int length = input.length();
		if (length == 0) {
			throw new BencodeException("Invalid data, no data read");
		}
		if (input.equals("-")) {
			throw new BencodeException("Invalid data, only a dash was read");
		}
		if (input.equals("-0")) {
			throw new BencodeException("Invalid data, negative zero is not allowed");
		}
		if (input.startsWith("0") && length > 1 || input.startsWith("-0")
				&& length > 2) {
			throw new BencodeException("Invalid data, leading zeros are not allowed");
		}

		try {
			value = Long.parseLong(input);
		} catch (NumberFormatException nfe) {
			throw new BencodeException("Invalid data, could not parse number", nfe);
		}
	}
}
