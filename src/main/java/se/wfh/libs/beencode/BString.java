package se.wfh.libs.beencode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public final class BString extends BNode<byte[]> implements Comparable<BString> {
	private static final long serialVersionUID = 1L;
	private static final byte SEPARATOR = ':';

	/** Maximum size of strings to read */
	private static int maxReadSize = 1024 * 1024 * 16;

	public static void setMaxReadSize(int maxReadSize) {
		BString.maxReadSize = maxReadSize;
	}

	public static BString of(byte[] data) {
		Objects.requireNonNull(data, "data may not be null");

		return new BString(data);
	}

	public static BString of(String data) {
		Objects.requireNonNull(data, "data may not be null");

		return of(data.getBytes(Charset.defaultCharset()));
	}

	public static BString empty() {
		return of(new byte[0]);
	}

	protected static boolean canParsePrefix(byte prefix) {
		return prefix >= '0' && prefix <= '9';
	}

	private BString(byte[] data) {
		super(data);
	}

	public String getString() {
		return getString(Charset.defaultCharset());
	}

	public String getString(Charset encoding) {
		return new String(value, encoding);
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public void setValue(String value) {
		setValue(value, Charset.defaultCharset());
	}

	public void setValue(String value, Charset encoding) {
		setValue(value.getBytes(encoding));
	}

	@Override
	protected BString clone() {
		return BString.of(Arrays.copyOf(value, value.length));
	}

	@Override
	protected void writeTo(OutputStream os) throws IOException {
		os.write(String.valueOf(value.length).getBytes(StandardCharsets.US_ASCII));
		os.write(SEPARATOR);
		os.write(value);
	}

	@Override
	protected void readFrom(InputStream is, byte prefix) throws IOException {
		long length = prefix - '0';

		if (length == 0) {
			throw new IOException("Leading zeros are not allowed.");
		}

		byte cur;
		while ((cur = (byte) is.read()) != SEPARATOR) {
			if (!canParsePrefix(cur)) {
				throw new IOException("Unexpected data, expected an digit but got a '"
						+ cur + "'");
			}

			length = length * 10 + (cur - '0');
		}

		if (length > maxReadSize) {
			throw new IOException("Denied attempt to read " + length + " bytes.");
		}

		int ilength = (int) length;
		value = new byte[ilength];
		int offset = 0;
		while (offset != ilength) {
			int temp = is.read(value, offset, ilength - offset);

			if (temp >= 0) {
				offset += temp;
			} else {
				throw new IOException("Premature end of stream, missing "
						+ (ilength - offset) + " bytes.");
			}
		}
	}

	@Override
	public String toString() {
		return new String(value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.getClass()) + Arrays.hashCode(value);
	}

	@Override
	public int compareTo(BString o) {
		return getString().compareTo(((BString) o).getString());
	}
}
