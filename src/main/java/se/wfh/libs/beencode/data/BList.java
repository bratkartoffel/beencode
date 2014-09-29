package se.wfh.libs.beencode.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import se.wfh.libs.beencode.util.LanguageFields;
import se.wfh.libs.beencode.util.NodeFactory;
import se.wfh.libs.common.utils.R;

/**
 * Class to represent a list of nodes for beencoded data.<br>
 * This class provides methods to create and fill a new list, as also parse
 * beencoded data from an {@link InputStream}.<br>
 * <br>
 * <code>
 * Lists are encoded as an 'l' followed by their elements (also bencoded)
 * followed by an 'e'. For example l4:spam4:eggse corresponds to ['spam', 'eggs'].
 * </code>
 * 
 * @since 0.1
 */
public final class BList extends BNode<List<BNode<?>>> implements Serializable,
		Cloneable {
	private static final long serialVersionUID = 1L;

	/** Prefix declaring the start of a list */
	public static final byte PREFIX = 'l';

	/** Suffix marking the end of a dictionary */
	public static final byte SUFFIX = 'e';

	/**
	 * Create a new empty list.
	 */
	public BList() {
		super(new ArrayList<BNode<?>>());
	}

	/**
	 * Create a new list according to the data in the given stream.
	 * 
	 * @param inp
	 *            The stream to read from
	 * @param prefix
	 *            The first read byte from the stream, has to be the
	 *            {@link #PREFIX}
	 * 
	 * @throws IOException
	 *             If something goes wrong while reading from the Stream.
	 * @throws IllegalArgumentException
	 *             If the given prefix is not the {@link #PREFIX}
	 */
	public BList(final InputStream inp, final byte prefix) throws IOException {
		super(inp, prefix);
	}

	/**
	 * @see BNode#BNode(Object)
	 */
	public BList(final List<BNode<?>> value) {
		super(value);
	}

	@Override
	public Object clone() {
		/* create a new list */
		final List<BNode<?>> neu = new ArrayList<>();

		synchronized (this) {
			/* clone all elements */
			value.forEach(node -> neu.add((BNode<?>) node.clone()));
		}

		/* create a new list with the cloned values */
		return new BList(neu);
	}

	@Override
	public boolean equals(final Object obj) {
		boolean result = false;

		if (obj instanceof BList) {
			/* compare the lists */
			result = ((BList) obj).getValue().equals(value);
		}

		/* return result */
		return result;
	}

	/**
	 * @return The underlying list of elements.
	 */
	public List<BNode<?>> getList() {
		return value;
	}

	@Override
	protected String getReadableString(final int level) {
		/* initialize buffer */
		final StringBuilder buf = new StringBuilder();

		/* indent level */
		final int i_level = indent(buf, level);

		/* append prefix */
		buf.append("[\n");

		/* iterate over all elements */
		value.forEach(node -> {
			/* write value */
			buf.append(node.getReadableString(i_level + 1));

			/* write line break */
			buf.append('\n');
		});

		/* indent */
		indent(buf, i_level);

		/* append suffix */
		buf.append(']');

		/* return result */
		return buf.toString();
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	protected List<BNode<?>> read(final InputStream inp, final byte prefix)
			throws IOException {
		/* abort when wrong prefix is given */
		if (prefix != PREFIX) {
			throw new IllegalArgumentException(R.t(
					LanguageFields.ERROR_INVALID_PREFIX,
					BList.class.getSimpleName(), prefix, PREFIX));
		}

		/* prepare buffer for reading */
		int buf = -1;

		/* prepare result map */
		final List<BNode<?>> result = new ArrayList<>();

		/* read and parsed data are valid? */
		boolean success = false;

		/* as long as we have more data to read and the list was not finished */
		while (!success && (buf = inp.read()) != -1) {
			/* if the read byte is the suffix, then we are finished */
			if (buf == SUFFIX) {
				success = true;
				break;
			}

			/* parse and add next node */
			result.add(NodeFactory.parseByPrefix(buf, inp));
		}

		/* if end of stream was reached without completing the list, abort */
		if (!success) {
			throw new IOException(R.t(LanguageFields.ERROR_UNEXPECTED_END));
		}

		/* return the parsed data */
		return result;
	}

	@Override
	public String toString() {
		return getReadableString();
	}

	@Override
	public void write(final OutputStream out) throws IOException {
		/* write prefix */
		out.write(PREFIX);

		/* write each element in the list */
		for (final BNode<?> data : value) {
			data.write(out);
		}

		/* write the suffix */
		out.write(SUFFIX);
	}
}
