package se.wfh.libs.beencode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class BDict extends BNode<Map<BString, BNode<?>>> implements
		Map<BString, BNode<?>> {
	private static final long serialVersionUID = 1L;
	private static final byte PREFIX = 'd';
	private static final byte SUFFIX = 'e';

	public static BDict of(Map<BString, BNode<?>> nodes) {
		return new BDict(nodes);
	}

	public static BDict empty() {
		return new BDict(new TreeMap<>());
	}

	protected static boolean canParsePrefix(byte prefix) {
		return prefix == PREFIX;
	}

	private BDict(Map<BString, BNode<?>> nodes) {
		super(nodes);
	}

	@Override
	protected BDict clone() {
		Map<BString, BNode<?>> copy = value
				.entrySet()
				.stream()
				.collect(
						Collectors
								.toMap(e -> e.getKey().clone(), e -> e.getValue().clone()));

		return BDict.of(copy);
	}

	@Override
	protected void writeTo(OutputStream os) throws IOException {
		os.write(PREFIX);
		for (Entry<BString, BNode<?>> entry : value.entrySet()) {
			entry.getKey().writeTo(os);
			entry.getValue().writeTo(os);
		}
		os.write(SUFFIX);
	}

	@Override
	protected void readFrom(InputStream is, byte prefix) throws IOException {
		byte read;

		while ((read = (byte) is.read()) != SUFFIX) {
			if (!BString.canParsePrefix(read)) {
				throw new IOException("Expected a dictionary key (BString), but it"
						+ " cannot parse with prefix '" + read + "'.");
			}

			BString key = BString.empty();
			key.readFrom(is, read);

			read = (byte) is.read();
			if (read == SUFFIX) {
				throw new IOException(
						"Expected dictionary value, but suffix was found.");
			}

			put(key, NodeFactory.decode(is, read));
		}
	}

	/*
	 * ===============================================================
	 * Implements java.util.Map
	 * ===============================================================
	 */

	public BNode<?> put(final BString key, final BNode<?> value) {
		return this.value.put(key, value);
	}

	@Override
	public int size() {
		return value.size();
	}

	@Override
	public boolean isEmpty() {
		return value.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		if (key == null || !BString.class.isAssignableFrom(key.getClass())) {
			return false;
		}

		return value.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null || !BNode.class.isAssignableFrom(value.getClass())) {
			return false;
		}

		return this.value.containsValue(value);
	}

	@Override
	public BNode<?> get(Object key) {
		if (key == null) {
			return null;
		}

		if (key instanceof BString) {
			return value.get(key);
		} else {
			return value.get(BString.of(String.valueOf(key)));
		}
	}

	@Override
	public BNode<?> remove(Object key) {
		if (key == null || !BString.class.isAssignableFrom(key.getClass())) {
			return null;
		}

		return value.remove(key);
	}

	@Override
	public void putAll(Map<? extends BString, ? extends BNode<?>> m) {
		value.putAll(m);
	}

	@Override
	public void clear() {
		value.clear();
	}

	@Override
	public Set<BString> keySet() {
		return value.keySet();
	}

	@Override
	public Collection<BNode<?>> values() {
		return value.values();
	}

	@Override
	public Set<Entry<BString, BNode<?>>> entrySet() {
		return value.entrySet();
	}
}
