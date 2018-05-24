package eu.fraho.libs.beencode;

/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */

import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Immutable
public final class BList extends BNode<List<BNode<?>>> {
    private static final long serialVersionUID = 1L;
    private static final byte PREFIX = 'l';
    private static final byte SUFFIX = 'e';

    private BList(@NotNull List<BNode<?>> nodes) {
        super(nodes);
    }

    @NotNull
    public static BList of(@NotNull BNode<?>... nodes) {
        Objects.requireNonNull(nodes, "nodes may not be null");
        return of(Arrays.asList(nodes));
    }

    @NotNull
    public static BList of(@NotNull List<BNode<?>> nodes) {
        Objects.requireNonNull(nodes, "nodes may not be null");
        List<BNode<?>> temp = new ArrayList<>(nodes);
        return new BList(Collections.unmodifiableList(temp));
    }

    @NotNull
    public static BList of(@NotNull InputStream is) throws IOException {
        return of(is, (byte) is.read());
    }

    @NotNull
    public static BList of(@NotNull InputStream is, byte prefix) throws IOException {
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
    public void write(@NotNull OutputStream os) throws IOException {
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

    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean contains(Object o) {
        return getValue().contains(o);
    }

    public Iterator<BNode<?>> iterator() {
        return getValue().iterator();
    }

    public BNode<?>[] toArray() {
        return getValue().toArray(new BNode[0]);
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    public <T> T[] toArray(T[] a) {
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
            return Optional.of((T) getValue().get(index));
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public int indexOf(Object o) {
        return getValue().indexOf(o);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public int lastIndexOf(Object o) {
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

    @NotNull
    public BList remove(int index) {
        List<BNode<?>> temp = new ArrayList<>(getValue());
        temp.remove(index);
        return of(temp);
    }

    @NotNull
    public BList remove(@NotNull BNode<?> node) {
        List<BNode<?>> temp = new ArrayList<>(getValue());
        if (temp.remove(node)) return of(temp);
        else return this;
    }

    @NotNull
    public BList add(@NotNull BNode<?>... values) {
        Objects.requireNonNull(values, "values may not be null");
        List<BNode<?>> temp = new ArrayList<>(getValue());
        Collections.addAll(temp, values);
        return of(temp);
    }

    @NotNull
    public BList join(@NotNull BList... others) {
        Objects.requireNonNull(others, "others may not be null");
        List<BNode<?>> temp = new ArrayList<>(getValue());
        for (BList other : others) temp.addAll(other.getValue());
        return of(temp);
    }
}
