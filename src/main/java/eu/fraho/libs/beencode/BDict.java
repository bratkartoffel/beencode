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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class BDict extends BNodeBase<Map<BString, BNode<?>>> implements Map<BString, BNode<?>> {
    private static final long serialVersionUID = 100L;
    private static final byte PREFIX = 'd';
    private static final byte SUFFIX = 'e';

    private BDict(Map<@NotNull BString, @NotNull BNode<?>> nodes) {
        super(Collections.unmodifiableMap(new LinkedHashMap<>(nodes)));
    }

    /**
     * Create a new map containing the given elements. It is asserted, that each even element is a BString.
     * The count of elements provided to this method has to be a multiple of two, where each even element is used as
     * an entries key and the next odd element is used as the corresponding value.
     *
     * @param nodes map of elements
     * @return a new map
     * @throws BencodeException if an even element is not a BString or one element is null
     */
    @Contract(pure = true, value = "_ -> new")
    public static @NotNull BDict of(@NotNull BNode<?> @NotNull ... nodes) {
        LinkedHashMap<BString, BNode<?>> temp = new LinkedHashMap<>();
        for (int i = 0; i < nodes.length; i += 2) {
            if (!(nodes[i] instanceof BString)) {
                Class<?> clz = nodes[i] == null ? null : nodes[i].getClass();
                throw new BencodeException("key as argument #" + i + " has to be a BString (is " + clz + ")");
            }
            if (i + 1 == nodes.length || nodes[i + 1] == null) {
                throw new BencodeException("value argument #" + i + " has to be not null");
            }
            temp.put((BString) nodes[i], nodes[i + 1]);
        }
        return of(temp);
    }

    /**
     * Parse the given stream for a map
     *
     * @param is stream of data
     * @return a new map
     * @throws BencodeException if the given prefix is not {@link #PREFIX} or the parsed stream is invalid
     */
    @Contract(value = "_ -> new")
    public static @NotNull BDict of(@NotNull InputStream is) throws IOException {
        return of(is, (byte) is.read());
    }

    /**
     * Parse the given stream for a map
     *
     * @param is     stream of data
     * @param prefix first read byte, has to be {@link #PREFIX}
     * @return a new map
     * @throws BencodeException if the given prefix is not {@link #PREFIX} or the parsed stream is invalid
     */
    @Contract(value = "_, _ -> new")
    public static @NotNull BDict of(@NotNull InputStream is, byte prefix) throws IOException {
        if (!canParsePrefix(prefix)) {
            throw new BencodeException("Unknown prefix, cannot parse: " + prefix);
        }
        Map<BString, BNode<?>> result = new LinkedHashMap<>();
        byte read;
        while ((read = (byte) is.read()) != SUFFIX) {
            if (!BString.canParsePrefix(read)) {
                throw new BencodeException("Expected a dictionary key (BString), but it"
                                           + " cannot parse with prefix '" + read + "'.");
            }
            BString key = BString.of(is, read);
            read = (byte) is.read();
            if (read == SUFFIX) {
                throw new BencodeException(
                    "Expected dictionary value, but suffix was found.");
            }
            result.put(key, NodeFactory.decode(is, read));
        }
        return of(result);
    }

    /**
     * Create a new map containing the given elements
     *
     * @param value map of elements
     * @return a new map
     */
    @Contract(pure = true, value = "_ -> new")
    public static @NotNull BDict of(@NotNull Map<BString, BNode<?>> value) {
        return new BDict(value);
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
        for (Map.Entry<BString, BNode<?>> entry : getValue().entrySet()) {
            entry.getKey().write(os);
            entry.getValue().write(os);
        }
        os.write(SUFFIX);
    }

    /**
     * Create a copy of this map with the given key and value added.
     *
     * @param key   new key
     * @param value new value
     * @return new map
     */
    @Contract(pure = true, value = "_, _ -> new")
    public @NotNull BDict with(@NotNull BString key, @NotNull BNode<?> value) {
        TreeMap<BString, BNode<?>> temp = new TreeMap<>(getValue());
        temp.put(key, value);
        return of(temp);
    }

    /**
     * Create a copy of this map without the given key. If the given element is not present in this Map,
     * no new instance is created.
     *
     * @param key key to remove
     * @return map without the given key, possibly a new instance
     */
    @Contract(pure = true)
    public @NotNull BDict without(@NotNull String key) {
        return without(BString.of(key));
    }

    /**
     * Create a copy of this map without the given key. If the given element is not present in this Map,
     * no new instance is created.
     *
     * @param key key to remove
     * @return map without the given key, possibly a new instance
     */
    @Contract(pure = true)
    public @NotNull BDict without(@NotNull BString key) {
        TreeMap<BString, BNode<?>> temp = new TreeMap<>(getValue());
        if (temp.remove(key) != null) {
            return of(temp);
        } else {
            return this;
        }
    }

    /**
     * Create a new map with all the given maps joined together.
     *
     * @param others other maps
     * @return new map
     */
    @Contract(pure = true, value = "_ -> new")
    public @NotNull BDict join(@NotNull BDict @NotNull ... others) {
        TreeMap<BString, BNode<?>> temp = new TreeMap<>(getValue());
        for (BDict other : others) {
            temp.putAll(other.getValue());
        }
        return of(temp);
    }

    /**
     * Get an element of this map.
     *
     * @param key key
     * @param <T> type of returned element
     * @return element at the specified key, possibly null
     * @see #get(Object)
     */
    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    public <T extends BNode<?>> @Nullable T get(@NotNull String key) {
        return (T) getValue().get(BString.of(key));
    }

    /**
     * Clone this object
     *
     * @return copy of this object
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    @NotNull
    @Contract(pure = true, value = ", -> new")
    public BDict clone() {
        return BDict.of(getValue());
    }

    /* implement Map API */

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
    public boolean containsKey(@NotNull Object o) {
        return getValue().containsKey(o);
    }

    @Contract(pure = true)
    @Override
    public boolean containsValue(@NotNull Object o) {
        return getValue().containsValue(o);
    }

    @Contract(pure = true)
    @Override
    public @Nullable BNode<?> get(@NotNull Object o) {
        return getValue().get(o);
    }

    @Contract(pure = true)
    @Override
    public @NotNull Set<@NotNull BString> keySet() {
        return getValue().keySet();
    }

    @Contract(pure = true)
    @Override
    public @NotNull Collection<@NotNull BNode<?>> values() {
        return getValue().values();
    }

    @Contract(pure = true)
    @Override
    public @NotNull Set<Map.Entry<@NotNull BString, @NotNull BNode<?>>> entrySet() {
        return getValue().entrySet();
    }

    /* this class is immutable */

    @Contract(pure = true, value = "_, _ -> fail")
    @Override
    public @Nullable BNode<?> put(BString key, BNode<?> value) {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true, value = "_ -> fail")
    @Override
    public @Nullable BNode<?> remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true, value = "_ -> fail")
    @Override
    public void putAll(@NotNull Map<? extends BString, ? extends BNode<?>> map) {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true, value = "-> fail")
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
