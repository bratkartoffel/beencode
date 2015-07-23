package se.wfh.libs.beencode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class BList extends BNode<List<BNode<?>>> implements
		List<BNode<?>> {
	private static final long serialVersionUID = 1L;
	private static final byte PREFIX = 'l';
	private static final byte SUFFIX = 'e';

	public static BList of(BNode<?>... nodes) {
		Java6Helper.requireNonNull(nodes, "nodes may not be null");

		return new BList(new ArrayList<BNode<?>>(Arrays.asList(nodes)));
	}

	public static BList of(List<BNode<?>> nodes) {
		Java6Helper.requireNonNull(nodes, "nodes may not be null");

		return new BList(nodes);
	}

	public static BList empty() {
		return new BList(new ArrayList<BNode<?>>());
	}

	protected static boolean canParsePrefix(byte prefix) {
		return prefix == PREFIX;
	}

	private BList(List<BNode<?>> nodes) {
		super(nodes);
	}

	@Override
	protected BList clone() {
		List<BNode<?>> copy = new ArrayList<BNode<?>>();

		for (BNode<?> entry : value) {
			copy.add(entry.clone());
		}

		return BList.of(copy);
	}

	@Override
	protected void writeTo(OutputStream os) throws IOException {
		os.write(PREFIX);

		for (BNode<?> node : value) {
			node.writeTo(os);
		}

		os.write(SUFFIX);
	}

	@Override
	protected void readFrom(InputStream is, byte prefix) throws IOException {
		byte read;

		while ((read = (byte) is.read()) != SUFFIX) {
			add(NodeFactory.decode(is, read));
		}
	}

	/*
	 * ===============================================================
	 * Implements java.util.List
	 * ===============================================================
	 */

	@Override
	public int size() {
		return value.size();
	}

	@Override
	public boolean isEmpty() {
		return value.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		if (o == null || !BNode.class.isAssignableFrom(o.getClass())) {
			return false;
		}

		return value.contains(o);
	}

	@Override
	public Iterator<BNode<?>> iterator() {
		return value.iterator();
	}

	@Override
	public BNode<?>[] toArray() {
		return value.toArray(new BNode[value.size()]);
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return value.toArray(a);
	}

	@Override
	public boolean add(BNode<?> e) {
		return value.add(e);
	}

	@Override
	public boolean remove(Object o) {
		if (o == null || !BNode.class.isAssignableFrom(o.getClass())) {
			return false;
		}

		return value.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return value.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends BNode<?>> c) {
		return value.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends BNode<?>> c) {
		return value.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return value.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return value.retainAll(c);
	}

	@Override
	public void clear() {
		value.clear();
	}

	@Override
	public BNode<?> get(int index) {
		return value.get(index);
	}

	@Override
	public BNode<?> set(int index, BNode<?> element) {
		return value.set(index, element);
	}

	@Override
	public void add(int index, BNode<?> element) {
		value.add(index, element);
	}

	@Override
	public BNode<?> remove(int index) {
		return value.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		if (o == null || !BNode.class.isAssignableFrom(o.getClass())) {
			return -1;
		}

		return value.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		if (o == null || !BNode.class.isAssignableFrom(o.getClass())) {
			return -1;
		}

		return value.lastIndexOf(o);
	}

	@Override
	public ListIterator<BNode<?>> listIterator() {
		return value.listIterator();
	}

	@Override
	public ListIterator<BNode<?>> listIterator(int index) {
		return value.listIterator(index);
	}

	@Override
	public List<BNode<?>> subList(int fromIndex, int toIndex) {
		return value.subList(fromIndex, toIndex);
	}
}
