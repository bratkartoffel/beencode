/*
 * MIT Licence
 * Copyright (c) 2023 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class BList extends BNodeBase<List<BNode<?>>> implements List<BNode<?>> {
    private static final long serialVersionUID = 100L;
    private static final byte PREFIX = 'l';
    private static final byte SUFFIX = 'e';

    private BList(@NotNull List<@NotNull BNode<?>> nodes) {
        super(Collections.unmodifiableList(new ArrayList<>(nodes)));
    }

    /**
     * Create a new list containing the given elements.
     *
     * @param nodes list of elements
     * @return a new list
     * @throws BencodeException if an element is null
     */
    @Contract(pure = true, value = "_ -> new")
    public static @NotNull BList of(@NotNull BNode<?> @NotNull ... nodes) {
        List<BNode<?>> list = new ArrayList<>();
        for (BNode<?> node : nodes) {
            //noinspection ConstantValue
            if (node == null) {
                throw new BencodeException("null elements are not allowed for BList");
            }
            list.add(node);
        }
        return of(list);
    }

    /**
     * Create a new list containing the given elements.
     *
     * @param nodes list of elements
     * @return a new list
     * @throws BencodeException if an element is null
     */
    @Contract(pure = true, value = "_ -> new")
    public static @NotNull BList of(@NotNull List<@NotNull BNode<?>> nodes) {
        if (nodes.contains(null)) {
            throw new BencodeException("null elements are not allowed for BList");
        }
        return new BList(nodes);
    }

    /**
     * Parse the given stream for a BList
     *
     * @param is stream of data
     * @return new instance
     * @throws BencodeException if the given prefix is not {@link #PREFIX} or the parsed stream is invalid
     */
    @Contract(value = "_ -> new")
    public static @NotNull BList of(@NotNull InputStream is) throws IOException {
        return of(is, (byte) is.read());
    }

    /**
     * Parse the given stream for a BList
     *
     * @param is     stream of data
     * @param prefix first read byte, has to be {@link #PREFIX}
     * @return new instance
     * @throws BencodeException if the given prefix is not {@link #PREFIX} or the parsed stream is invalid
     */
    @Contract(value = "_, _ -> new")
    public static @NotNull BList of(@NotNull InputStream is, byte prefix) throws IOException {
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

    /**
     * @param prefix the prefix to check
     * @return is the given byte the expected prefix for this element?
     * @see #PREFIX
     */
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

    /**
     * Create a copy of this list with the given elements added.
     *
     * @param values new elements
     * @return new list
     */
    @Contract(pure = true, value = "_, -> new")
    public @NotNull BList with(@NotNull BNode<?> @NotNull ... values) {
        List<BNode<?>> temp = new ArrayList<>(getValue());
        Collections.addAll(temp, values);
        return of(temp);
    }

    /**
     * Create a copy of this map without the given element. If the given element is not present in this List,
     * no new instance is created.
     *
     * @param node element to remove
     * @return list without the given element, possibly a new instance
     * @see List#remove(Object)
     */
    @Contract(pure = true, value = "_ -> new")
    public @NotNull BList without(@NotNull BNode<?> node) {
        List<BNode<?>> temp = new ArrayList<>(getValue());
        if (temp.remove(node)) {
            return of(temp);
        } else {
            return this;
        }
    }


    /**
     * Create a copy of this map without the given element.
     *
     * @param index element to remove
     * @return list without the given element
     * @see List#remove(int)
     */
    @Contract(pure = true, value = "_ -> new")
    public @NotNull BList without(int index) {
        List<BNode<?>> temp = new ArrayList<>(getValue());
        temp.remove(index);
        return of(temp);
    }

    /**
     * Create a new list with all the given lists joined together.
     *
     * @param others other lists
     * @return new list
     */
    @Contract(pure = true, value = "_, -> new")
    public @NotNull BList join(@NotNull BList @NotNull ... others) {
        List<BNode<?>> temp = new ArrayList<>(getValue());
        for (BList other : others) {
            temp.addAll(other.getValue());
        }
        return of(temp);
    }

    /**
     * Clone this object
     *
     * @return copy of this object
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    @NotNull
    @Contract(pure = true, value = "-> new")
    public BList clone() {
        return BList.of(getValue());
    }

    /* implement List API */

    @Contract(pure = true)
    @Override
    public int size() {
        return getValue().size();
    }

    @Contract(pure = true)
    @Override
    public boolean isEmpty() {
        return getValue().isEmpty();
    }

    @Contract(pure = true)
    @Override
    public boolean contains(Object o) {
        return getValue().contains(o);
    }

    @Contract(pure = true, value = "-> new")
    @Override
    public @NotNull Iterator<@NotNull BNode<?>> iterator() {
        return getValue().iterator();
    }

    @Contract(pure = true, value = "-> new")
    @Override
    public @NotNull BNode<?> @NotNull [] toArray() {
        return getValue().toArray(new BNode[0]);
    }

    @Override
    public <T> @NotNull T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        return getValue().toArray(a);
    }

    @Contract(pure = true)
    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return new HashSet<>(getValue()).containsAll(c);
    }

    @Contract(pure = true)
    @Override
    public @Nullable BNode<?> get(int index) {
        return getValue().get(index);
    }

    @Contract(pure = true, value = "-> new")
    @Override
    public @NotNull ListIterator<@NotNull BNode<?>> listIterator() {
        return getValue().listIterator();
    }

    @Contract(pure = true, value = "_, -> new")
    @Override
    public @NotNull ListIterator<@NotNull BNode<?>> listIterator(int index) {
        return getValue().listIterator(index);
    }

    @Contract(pure = true, value = "_, _ -> new")
    @Override
    public @NotNull List<@NotNull BNode<?>> subList(int fromIndex, int toIndex) {
        return getValue().subList(fromIndex, toIndex);
    }

    @Contract(pure = true)
    @Override
    public int indexOf(@NotNull Object o) {
        return getValue().indexOf(o);
    }

    @Contract(pure = true)
    @Override
    public int lastIndexOf(@NotNull Object o) {
        return getValue().lastIndexOf(o);
    }

    /* this class is immutable */

    @Contract(pure = true, value = "_ -> fail")
    @Override
    public BNode<?> remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Contract(pure = true, value = "_ -> fail")
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true, value = "_ -> fail")
    @Override
    public boolean add(BNode<?> bNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Contract(pure = true, value = "_ -> fail")
    public boolean addAll(@NotNull Collection<? extends BNode<?>> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Contract(pure = true, value = "_, _ -> fail")
    public boolean addAll(int index, @NotNull Collection<? extends BNode<?>> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Contract(pure = true, value = "_ -> fail")
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Contract(pure = true, value = "_ -> fail")
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Contract(pure = true, value = "-> fail")
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    @Contract(pure = true, value = "_, _ -> fail")
    public BNode<?> set(int index, @NotNull BNode<?> element) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Contract(pure = true, value = "_, _ -> fail")
    public void add(int index, @NotNull BNode<?> element) {
        throw new UnsupportedOperationException();
    }
}
