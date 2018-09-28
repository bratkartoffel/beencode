/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class BList extends BNodeBase<List<BNode<?>>> {
    private static final long serialVersionUID = 100L;
    private static final byte PREFIX = 'l';
    private static final byte SUFFIX = 'e';

    private BList(List<BNode<?>> nodes) {
        super(Collections.unmodifiableList(new ArrayList<>(nodes)));
    }

    public static BList of(BNode<?>... nodes) {
        Objects.requireNonNull(nodes, "nodes may not be null");
        return of(Arrays.asList(nodes));
    }

    public static BList of(List<BNode<?>> nodes) {
        Objects.requireNonNull(nodes, "nodes may not be null");
        return new BList(nodes);
    }

    public static BList of(InputStream is) throws IOException {
        return of(is, (byte) is.read());
    }

    public static BList of(InputStream is, byte prefix) throws IOException {
        if (!canParsePrefix(prefix)) {
            throw new BencodeException("Unknown prefix, cannot parse: " + prefix);
        }

        List<BNode<?>> temp = new ArrayList<>();
        byte read;
        while ((read = (byte) is.read()) != SUFFIX) {
            temp.add(NodeFactory.decode(is, read));
        }
        return of(temp);
    }

    public static boolean canParsePrefix(byte prefix) {
        return prefix == PREFIX;
    }

    @Override
    public void write(OutputStream os) throws IOException {
        os.write(PREFIX);
        for (BNode<?> node : getValue()) {
            node.write(os);
        }
        os.write(SUFFIX);
    }

    public int size() {
        return getValue().size();
    }

    public boolean isEmpty() {
        return getValue().isEmpty();
    }

    public boolean contains(BNode<?> o) {
        return getValue().contains(o);
    }

    public Iterator<BNode<?>> iterator() {
        return getValue().iterator();
    }

    public BNode<?>[] toArray() {
        return getValue().toArray(new BNode[0]);
    }

    public BNode<?>[] toArray(BNode<?>[] a) {
        return getValue().toArray(a);
    }

    public boolean containsAll(Collection<?> c) {
        return getValue().containsAll(c);
    }

    public Spliterator<BNode<?>> spliterator() {
        return getValue().spliterator();
    }

    public Stream<BNode<?>> stream() {
        return getValue().stream();
    }

    public void forEach(Consumer<? super BNode<?>> action) {
        getValue().forEach(action);
    }

    @SuppressWarnings("unchecked")
    public <T extends BNode<?>> Optional<T> get(int index) {
        if (index >= size()) {
            return Optional.empty();
        } else {
            BNode<?> value = getValue().get(index);
            return Optional.of((T) value);
        }
    }

    public int indexOf(BNode<?> o) {
        return getValue().indexOf(o);
    }

    public int lastIndexOf(BNode<?> o) {
        return getValue().lastIndexOf(o);
    }

    public ListIterator<BNode<?>> listIterator() {
        return getValue().listIterator();
    }

    public ListIterator<BNode<?>> listIterator(int index) {
        return getValue().listIterator(index);
    }

    public List<BNode<?>> subList(int fromIndex, int toIndex) {
        return getValue().subList(fromIndex, toIndex);
    }

    public BList remove(int index) {
        List<BNode<?>> temp = new ArrayList<>(getValue());
        temp.remove(index);
        return of(temp);
    }

    public BList remove(BNode<?> node) {
        List<BNode<?>> temp = new ArrayList<>(getValue());
        if (temp.remove(node)) return of(temp);
        else return this;
    }

    public BList add(BNode<?>... values) {
        Objects.requireNonNull(values, "values may not be null");
        List<BNode<?>> temp = new ArrayList<>(getValue());
        Collections.addAll(temp, values);
        return of(temp);
    }

    public BList join(BList... others) {
        Objects.requireNonNull(others, "others may not be null");
        List<BNode<?>> temp = new ArrayList<>(getValue());
        for (BList other : others) temp.addAll(other.getValue());
        return of(temp);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BList clone() {
        try {
            return (BList) super.clone();
        } catch (BencodeException be) {
            return BList.of(getValue());
        }
    }
}
