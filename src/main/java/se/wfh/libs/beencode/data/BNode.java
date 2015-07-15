package se.wfh.libs.beencode.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Abstract super-class for all types of beencoded-data.<br>
 * Defines basic methods to work with a node, regardless which data it stores.
 *
 * @since 0.1
 *
 * @param <T>
 *          The type of data this node contains
 */
public abstract class BNode<T> implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	/** each level to indent is represented by this sequence of chars. */
	public static final String INDENTS = "  ";

	/** the value for this node */
	protected T value;

	/**
	 * 
	 * @param inp
	 * @param expected
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends BNode<?>> T of(final InputStream inp,
			Class<T> expected) throws IOException {
		int prefix = inp.read();

		if (prefix == BDict.PREFIX && !BDict.class.equals(expected)) {
			throw new IOException("Expected a " + expected + ", but got a "
					+ BDict.class);
		}
		if (prefix == BList.PREFIX && !BList.class.equals(expected)) {
			throw new IOException("Expected a " + expected + ", but got a "
					+ BList.class);
		}
		if (prefix == BInteger.PREFIX && !BInteger.class.equals(expected)) {
			throw new IOException("Expected a " + expected + ", but got a "
					+ BInteger.class);
		}
		if (prefix >= '0' && prefix <= '9' && !BString.class.equals(expected)) {
			throw new IOException("Expected a " + expected + ", but got a "
					+ BString.class);
		}

		return (T) of(inp, prefix);
	}

	/**
	 * 
	 * @param inp
	 * @return
	 * @throws IOException
	 */
	public static BNode<?> of(final InputStream inp) throws IOException {
		return of(inp, inp.read());
	}

	/**
	 * 
	 * @param inp
	 * @param prefix
	 * @return
	 * @throws IOException
	 */
	public static BNode<?> of(final InputStream inp, final int prefix)
			throws IOException {
		BNode<?> value;

		switch (prefix) {
			case BDict.PREFIX:
				value = new BDict(inp, BDict.PREFIX);
				break;
			case BInteger.PREFIX:
				value = new BInteger(inp, BInteger.PREFIX);
				break;
			case BList.PREFIX:
				value = new BList(inp, BList.PREFIX);
				break;
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				value = new BString(inp, (byte) prefix);
				break;
			default:
				throw new IllegalArgumentException("Invalid prefix for an "
						+ BNode.class.getSimpleName() + ". Is '" + prefix
						+ "', expected one of 'd,l,i,0-9'");
		}

		return value;
	}

	/**
	 * Default constructor when the value should be parsed and generated from
	 * the given inputstream.<br>
	 * As the type of a node can only be decided when the first byte is read,
	 * this first byte (called prefix) is also given to the constructor.<br>
	 * The child implementations should check if the prefix is correct for this
	 * type of node and throw an IllegalArgumentException if it's wrong.
	 *
	 * @param inp
	 *          The stream to read from.
	 * @param prefix
	 *          The prefix of the node
	 *
	 * @throws IOException
	 *           If anything goes wrong while reading from the stream or the
	 *           read values are not parseable.
	 */
	public BNode(final InputStream inp, final byte prefix) throws IOException {
		setValue(read(inp, prefix));
	}

	/**
	 * Default constructor, created the node with the given value.
	 *
	 * @param value
	 *          The value this node should hold.
	 */
	public BNode(final T value) {
		this.value = value;
	}

	@Override
	public abstract BNode<T> clone();

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !BNode.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		boolean result = Objects.equals(this.getClass(), obj.getClass());

		if (result) {
			BNode<?> other = (BNode<?>) obj;
			if (this.getClass().isAssignableFrom(BString.class)) {
				result = Arrays.equals((byte[]) this.value, (byte[]) other.value);
			} else {
				result = Objects.equals(this.value, other.value);
			}
		}

		return result;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	/**
	 * This function creates and returns a human readable representation of this
	 * node.
	 *
	 * @return Human readable node representation.
	 */
	public final String getReadableString() {
		return getReadableString(0);
	}

	/**
	 * Implementation for the {@link #getReadableString()}.
	 *
	 * @param level
	 *          The level to indent.
	 *
	 * @return A human readable representation of this node.
	 */
	protected abstract String getReadableString(int level);

	/**
	 * @return The {@link #value} of this node.
	 */
	public final T getValue() {
		return value;
	}

	/**
	 * Helper method to indent the data for the {@link #getReadableString(int)}.
	 *
	 * @param buf
	 *          The buffer to write the indents to
	 * @param level
	 *          The levels to indent.
	 *
	 * @return The given level, absolute (allways positive)
	 */
	protected int indent(final StringBuilder buf, final int level) {
		for (int i = 0; i < level; i++) {
			buf.append(BNode.INDENTS);
		}

		return Math.abs(level);
	}

	/**
	 * Parses the given inputstream and read all the data which belongs to this
	 * node.<br>
	 * As the type of a node can only be decided when the first byte is read,
	 * this first byte (called prefix) is also given.<br>
	 * The child implementations should check if the prefix is correct for this
	 * type of node and throw an IllegalArgumentException if it's wrong. This
	 * method is only called by the {@link #BNode(InputStream, byte)}.<br>
	 * If invalid data is encountered and the child cannot recover (as pretty
	 * allways), this method should throw an {@link IOException}.
	 *
	 * @param inp
	 *          The stream to read from.
	 * @param prefix
	 *          The prefix of the node
	 *
	 * @return A Node representing the given data
	 *
	 * @throws IOException
	 *           If anything goes wrong while reading from the stream or the
	 *           read values are not parseable.
	 */
	protected abstract T read(InputStream inp, byte prefix) throws IOException;

	/**
	 * @param value
	 *          The {@link #value} to set.
	 */
	public final void setValue(final T value) {
		this.value = value;
	}

	@Override
	public abstract String toString();

	/**
	 * Write this node out to the given stream. As every node is represented in
	 * other ways, this methods has to be implemented by the child class.
	 *
	 * @param out
	 *          The stream to write to.
	 *
	 * @throws IOException
	 *           If anything goes wring while writing the string.
	 */
	public abstract void write(OutputStream out) throws IOException;

	/**
	 * @return the serialized node in bencoded form
	 */
	public byte[] getEncoded() {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			write(bos);
			return bos.toByteArray();
		} catch (IOException ioe) {
			throw new IllegalStateException(
					"ByteAraryOutputStream should not throw an IOException!");
		}
	}
}
