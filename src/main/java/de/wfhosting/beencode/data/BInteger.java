package de.wfhosting.beencode.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Objects;

import de.wfhosting.beencode.util.LanguageFields;
import de.wfhosting.beencode.util.Tools;
import de.wfhosting.common.R;

/**
 * Class to represent an integer for beencoded data.<br>
 * A beencoded integer is a 64 bit number, represented by a {@link Long}.<br>
 * <br>
 * <code>
 * Integers are represented by an 'i' followed by the number in base 10
 * followed by an 'e'. For example i3e corresponds to 3 and i-3e corresponds
 * to -3. Integers have no size limitation. i-0e is invalid. All encodings with
 * a leading zero, such as i03e, are invalid, other than i0e, which of course
 * corresponds to 0.
 * </code>
 * 
 * @since 0.1
 */
public final class BInteger extends BNode<Long> implements Serializable,
		Cloneable {
	private static final long serialVersionUID = 1L;

	/** Prefix declaring the start of an integer */
	public static final byte PREFIX = 'i';

	/** Suffix marking the end of an integer */
	public static final byte SUFFIX = 'e';

	/**
	 * Create a new beencoded integer.
	 * 
	 * @param value
	 *          The value
	 */
	public BInteger(long value) {
		super(value);
	}

	/**
	 * @see BNode#BNode(InputStream, byte)
	 */
	public BInteger(InputStream inp, byte prefix) throws IOException {
		super(inp, prefix);
	}

	@Override
	protected Long read(InputStream inp, byte prefix) throws IOException {
		/* abort when wrong prefix is given */
		if (prefix != PREFIX) {
			throw new IllegalArgumentException(R.t(
					LanguageFields.ERROR_INVALID_PREFIX,
					BInteger.class.getSimpleName(), prefix, PREFIX));
		}

		/* prepare buffer for reading */
		int buf = -1;

		/* prepare result */
		long number = 0;

		/* started yet? */
		boolean started = false;

		/* read and parsed data are valid? */
		boolean finished = false;

		/* read number is negative? */
		boolean negative = false;

		/* as long as we have more data to read and the integer was not finished */
		while (!finished && (buf = inp.read()) != -1) {
			if (buf == 'e') {
				/* switch flag, number successfully read */
				finished = started;
				break;
			} else if (Tools.isDigit(buf)) {
				/* check for preceeding 0s */
				Tools.checkLeadingZero(started, buf);

				/* append the read digit to the result */
				number = number * 10 + buf - 0x30;
				started = true;
				continue;
			} else if (buf == '-' && !started) {
				/* if nothing else was read so far, we set the negative bit */
				negative = true;
				continue;
			}

			/* invalid character read */
			throw new IOException(R.t(LanguageFields.ERROR_INTEGER_INVALID_DATA, buf,
					inp.available() == 0));
		}

		/* return the parsed data */
		return checkResult(finished, negative, number);
	}

	private long checkResult(boolean finished, boolean negative, long number)
			throws IOException {
		/* if end of stream was reached without completing the integer, abort */
		if (!finished) {
			throw new IOException(R.t(LanguageFields.ERROR_UNEXPECTED_END));
		}

		/* negative zero is not allowed per definition */
		if (negative && number == 0) {
			throw new IOException(R.t(LanguageFields.ERROR_NEGATIVE_ZERO));
		}

		/* if the negative flag is set, turn the result */
		if (negative) {
			return -number;
		} else {
			return number;
		}
	}

	@Override
	public void write(OutputStream out) throws IOException {
		/* write the prefix */
		out.write(PREFIX);

		/* write the number */
		out.write(String.valueOf(value).getBytes(Tools.UTF8));

		/* write the suffix */
		out.write(SUFFIX);
	}

	@Override
	public String toString() {
		return getReadableString();
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj instanceof BInteger) {
			/* compare the value */
			result = Objects.equals(((BInteger) obj).getValue(), value);
		}

		/* return result */
		return result;
	}

	@Override
	public int hashCode() {
		return value.intValue();
	}

	@Override
	protected String getReadableString(int level) {
		/* initialize buffer */
		final StringBuilder buf = new StringBuilder();

		/* indent */
		indent(buf, level);

		/* write number */
		buf.append(value.toString());

		/* return result */
		return buf.toString();
	}

	@Override
	public Object clone() {
		/* create a new BInteger with the same value */
		return new BInteger(value);
	}
}
