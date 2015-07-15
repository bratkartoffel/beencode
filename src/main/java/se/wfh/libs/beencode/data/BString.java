package se.wfh.libs.beencode.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Class to represent an string for beencoded data.<br>
 * A beencoded string is an array of byte-values.<br>
 * <br>
 * <code>
 * Strings are length-prefixed base ten followed by a colon and the string.
 * For example 4:spam corresponds to 'spam'.
 * </code>
 *
 * @since 0.1
 */
public final class BString extends BNode<byte[]> implements Serializable,
		Cloneable {
	private static final long serialVersionUID = 1L;

	/** The seperator between the length and the data */
	public static final byte SEPERATOR = ':';

	/**
	 * Create a new beencoded string.
	 *
	 * @param value
	 *          The value
	 */
	public BString(final byte[] value) {
		super(value);
	}

	/**
	 * Create a new string according to the data in the given stream.
	 *
	 * @param inp
	 *          The stream to read from
	 * @param prefix
	 *          The first read byte from the stream
	 *
	 * @throws IOException
	 *           If something goes wrong while reading from the Stream.
	 * @throws IllegalArgumentException
	 *           If the given prefix is invalid.
	 */
	public BString(final InputStream inp, final byte prefix) throws IOException {
		super(inp, prefix);
	}

	/**
	 * Create a new beencoded string.<br>
	 * This method always used UTF-8 encoding, although the standard declares
	 * that the string should not take care of any encoding. Now it relies on the
	 * plattfrom specific default encoding. For correct decoding
	 * please use another constructor, e.g. {@link #BString(byte[])}.
	 *
	 * @param value
	 *          The value
	 */
	@Deprecated
	public BString(final String value) {
		super(value.getBytes(Charset.defaultCharset()));
	}

	/**
	 * Create a new beencoded string.<br>
	 *
	 * @param value
	 *          The value
	 * @param charset
	 *          charset for the input data
	 */
	public BString(final String value, final Charset charset) {
		super(value.getBytes(charset));
	}

	@Override
	public BString clone() {
		/* create a new BString */
		return new BString(value);
	}

	@Override
	protected String getReadableString(final int level) {
		/* initialize buffer */
		final StringBuilder buf = new StringBuilder();

		/* indent */
		indent(buf, level);

		/* append seperator */
		buf.append('"');

		/* append string */
		buf.append(new String(value, Charset.defaultCharset()));

		/* append seperator */
		buf.append('"');

		/* return result */
		return buf.toString();
	}

	@Override
	protected byte[] read(final InputStream inp, final byte prefix)
			throws IOException {
		/* prepare result */
		long result = readLength(inp, prefix);

		/*
		 * if the string length to read is longer then the maximum or zero,
		 * abort
		 */
		if (result > Integer.MAX_VALUE || result == 0) {
			throw new IOException("Invalid string length: " + result + " > "
					+ Integer.MAX_VALUE + ".");
		}

		/* initialize buffer for string */
		byte[] str = new byte[(int) result];

		/* how much data was read so far? */
		int read = 0;

		/* last attempt read bytes count */
		int temp = 0;

		/* read string in pieces, but try to read all at once */
		while (temp != -1 && read < result) {
			temp = inp.read(str, read, (int) (result - read));

			read += temp;
		}

		/* if not enough data was read, abort */
		if (temp == -1) {
			throw new IOException("Unexpected end of data.");
		}

		/* return the string */
		return str;
	}

	private void checkLeadingZero(final boolean started, final int digit)
			throws IOException {
		if (!started && digit == '0') {
			throw new IOException("Leading zeros are not permitted.");
		}
	}

	private long readLength(final InputStream inp, final byte prefix)
			throws IOException {
		/* prepare buffer for reading */
		int buf = prefix;

		/* prepare result */
		long number = 0;

		/* read and parsed data are valid? */
		boolean finished = false;

		/* started yet? */
		boolean started = false;

		/* first interprete the prefix, then read the next byte */
		do {
			/* interprete the read byte */
			if (buf == BString.SEPERATOR) {
				/* the number is finished. if nothing was else was read, abort */
				finished = started;
				break;
			} else if (Character.isDigit(buf)) {
				/* check for preceeding 0s */
				checkLeadingZero(started, buf);

				/* append the read digit to the result */
				number = number * 10 + buf - 0x30;
				started = true;
			} else {
				break;
			}
		} while (!finished && (buf = inp.read()) != 1);
		/* as long as we have more data to read and the integer was not finished */

		/* if end of stream was reached without completing the integer, abort */
		if (!finished) {
			throw new IOException("Invalid data in stream. Read: '" + buf
					+ "', EOF: '" + (inp.available() == 0) + "'.");
		}

		return number;
	}

	@Override
	public String toString() {
		return getReadableString();
	}

	@Override
	public void write(final OutputStream out) throws IOException {
		/* write the length of the data */
		out.write(String.valueOf(value.length).getBytes(StandardCharsets.US_ASCII));

		/* write the seperator */
		out.write(BString.SEPERATOR);

		/* write the data */
		out.write(value);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(value);
	}

	@Deprecated
	public String asString() {
		return asString(Charset.defaultCharset());
	}

	public String asString(Charset charset) {
		return new String(value, charset);
	}
}
