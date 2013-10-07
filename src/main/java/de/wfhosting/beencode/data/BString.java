package de.wfhosting.beencode.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;

import de.wfhosting.beencode.util.LanguageFields;
import de.wfhosting.beencode.util.Tools;
import de.wfhosting.common.R;

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
	public BString(String value) {
		super(value.getBytes(Tools.UTF8));
	}

	/**
	 * Create a new beencoded string.
	 * 
	 * @param value
	 *          The value
	 */
	public BString(byte[] value) {
		super(value);
	}

	/**
	 * @see BNode#BNode(InputStream, byte)
	 */
	public BString(InputStream inp, byte prefix) throws IOException {
		super(inp, prefix);
	}

	private long readLength(InputStream inp, byte prefix) throws IOException {
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
			if (buf == SEPERATOR) {
				/* the number is finished. if nothing was else was read, abort */
				finished = started;
				break;
			} else if (Tools.isDigit(buf)) {
				/* check for preceeding 0s */
				Tools.checkLeadingZero(started, buf);

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
			throw new IOException(R.t(LanguageFields.ERROR_INTEGER_INVALID_DATA, buf,
					inp.available() == 0));
		}

		return number;
	}

	@Override
	protected byte[] read(InputStream inp, byte prefix) throws IOException {
		/* prepare result */
		long result = readLength(inp, prefix);

		/* if the string length to read is longer then the maximum or zero, abort */
		if (result > Integer.MAX_VALUE || result == 0) {
			throw new IOException(R.t(LanguageFields.ERROR_STRING_LENGTH, result,
					Integer.MAX_VALUE));
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
			throw new IOException(R.t(LanguageFields.ERROR_UNEXPECTED_END));
		}

		/* return the string */
		return str;
	}

	@Override
	public void write(OutputStream out) throws IOException {
		/* write the length of the data */
		out.write(String.valueOf(value.length).getBytes(Tools.UTF8));

		/* write the seperator */
		out.write(SEPERATOR);

		/* write the data */
		out.write(value);
	}

	@Override
	public String toString() {
		return getReadableString();
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj instanceof BString) {
			/* compare the values */
			result = Arrays.equals(value, ((BString) obj).getValue());
		}

		/* return result */
		return result;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(value);
	}

	@Override
	protected String getReadableString(int level) {
		/* initialize buffer */
		final StringBuilder buf = new StringBuilder();

		/* indent */
		indent(buf, level);

		/* append seperator */
		buf.append('"');

		/* append string */
		buf.append(new String(value, Tools.UTF8));

		/* append seperator */
		buf.append('"');

		/* return result */
		return buf.toString();
	}

	@Override
	public Object clone() {
		/* create a new BString */
		return new BString(value);
	}
}
