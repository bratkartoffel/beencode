/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class BList extends BNode<List<BNode<?>>> {
    private static final long serialVersionUID = 1L;
    private static final byte PREFIX = 'l';
    private static final byte SUFFIX = 'e';

    private BList(List<BNode<?>> nodes) {
        super(nodes);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static BList of(BNode<?>... nodes) {
        Objects.requireNonNull(nodes, "nodes may not be null");
        return of(Arrays.asList(nodes));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static BList of(List<BNode<?>> nodes) {
        Objects.requireNonNull(nodes, "nodes may not be null");
        List<BNode<?>> temp = new ArrayList<>(nodes);
        return new BList(Collections.unmodifiableList(temp));
    }

    @Contract("_ -> new")
    @NotNull
    public static BList of(InputStream is) throws IOException {
        return of(is, (byte) is.read());
    }

    @Contract("_, _ -> new")
    @NotNull
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

    @Contract(pure = true)
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

    @Contract(pure = true)
    public int size() {
        return getValue().size();
    }

    @Contract(pure = true)
    public boolean isEmpty() {
        return getValue().isEmpty();
    }

    @Contract(pure = true)
    public boolean contains(BNode<?> o) {
        return getValue().contains(o);
    }

    @Contract(pure = true)
    @NotNull
    public Iterator<BNode<?>> iterator() {
        return getValue().iterator();
    }

    @Contract(pure = true)
    @NotNull
    public BNode<?>[] toArray() {
        return getValue().toArray(new BNode[0]);
    }

    @Contract(pure = true)
    @NotNull
    public BNode<?>[] toArray(BNode<?>[] a) {
        return getValue().toArray(a);
    }

    @Contract(pure = true)
    public boolean containsAll(Collection<?> c) {
        return getValue().containsAll(c);
    }

    @Contract(pure = true)
    public Spliterator<BNode<?>> spliterator() {
        return getValue().spliterator();
    }

    @Contract(pure = true)
    public Stream<BNode<?>> stream() {
        return getValue().stream();
    }

    @Contract(pure = true)
    public void forEach(Consumer<? super BNode<?>> action) {
        getValue().forEach(action);
    }

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public <T extends BNode<?>> Optional<T> get(int index) {
        if (index >= size()) {
            return Optional.empty();
        } else {
            BNode<?> value = getValue().get(index);
            return Optional.of((T) value);
        }
    }

    @Contract(pure = true)
    public int indexOf(BNode<?> o) {
        return getValue().indexOf(o);
    }

    @Contract(pure = true)
    public int lastIndexOf(BNode<?> o) {
        return getValue().lastIndexOf(o);
    }

    @Contract(pure = true)
    @NotNull
    public ListIterator<BNode<?>> listIterator() {
        return getValue().listIterator();
    }

    @Contract(pure = true)
    @NotNull
    public ListIterator<BNode<?>> listIterator(int index) {
        return getValue().listIterator(index);
    }

    @Contract(pure = true)
    @NotNull
    public List<BNode<?>> subList(int fromIndex, int toIndex) {
        return getValue().subList(fromIndex, toIndex);
    }

    @Contract(pure = true, value = "_ -> new")
    @NotNull
    public BList remove(int index) {
        List<BNode<?>> temp = new ArrayList<>(getValue());
        temp.remove(index);
        return of(temp);
    }

    @Contract(pure = true)
    @NotNull
    public BList remove(BNode<?> node) {
        List<BNode<?>> temp = new ArrayList<>(getValue());
        if (temp.remove(node)) return of(temp);
        else return this;
    }

    @Contract(pure = true, value = "_ -> new")
    @NotNull
    public BList add(BNode<?>... values) {
        Objects.requireNonNull(values, "values may not be null");
        List<BNode<?>> temp = new ArrayList<>(getValue());
        Collections.addAll(temp, values);
        return of(temp);
    }

    @Contract(pure = true, value = "_ -> new")
    @NotNull
    public BList join(BList... others) {
        Objects.requireNonNull(others, "others may not be null");
        List<BNode<?>> temp = new ArrayList<>(getValue());
        for (BList other : others) temp.addAll(other.getValue());
        return of(temp);
    }
}
