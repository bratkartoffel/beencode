package se.wfh.libs.beencode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public abstract class BNode<T> implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

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

		boolean result = Objects.equals(this.getClass(), obj.getClass());

		if (result) {
			BNode<?> other = (BNode<?>) obj;
			if (this.getClass().isAssignableFrom(BString.class)) {
				result = Arrays.equals((byte[]) this.value, (byte[]) other.getValue());
			} else {
				result = Objects.equals(this.value, other.getValue());
			}
		}

		return result;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.getClass()) + Objects.hashCode(value);
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public T getValue() {
		return value;
	}
}
