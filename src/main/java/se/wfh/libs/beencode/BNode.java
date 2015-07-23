package se.wfh.libs.beencode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;

public abstract class BNode<T> implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	public static final Charset DEFAULT_CHARSET = Charset.forName("ASCII");

	protected T value;

	protected BNode(T value) {
		this.value = value;
	}

	@Override
	protected abstract BNode<T> clone();

	protected abstract void writeTo(OutputStream os) throws IOException;

	protected abstract void readFrom(InputStream is, byte prefix)
			throws IOException;

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !BNode.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		boolean result = getClass().equals(obj.getClass());

		if (result) {
			BNode<?> other = (BNode<?>) obj;
			if (this.getClass().isAssignableFrom(BString.class)) {
				result = Arrays.equals((byte[]) value, (byte[]) other.getValue());
			} else {
				result = value.equals(other.getValue());
			}
		}

		return result;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode() + value.hashCode();
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public T getValue() {
		return value;
	}
}
