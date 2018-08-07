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

public final class BDict extends BNode<Map<BString, BNode<?>>> {
    private static final long serialVersionUID = 1L;
    private static final byte PREFIX = 'd';
    private static final byte SUFFIX = 'e';

    private BDict(Map<BString, BNode<?>> nodes) {
        super(nodes);
    }

    @Contract(pure = true)
    @NotNull
    public static BDict of(BNode<?>... nodes) {
        Objects.requireNonNull(nodes, "nodes may not be null");

        TreeMap<BString, BNode<?>> temp = new TreeMap<>();
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

    @Contract("_ -> new")
    @NotNull
    public static BDict of(InputStream is) throws IOException {
        return of(is, (byte) is.read());
    }

    @Contract("_, _ -> new")
    @NotNull
    public static BDict of(InputStream is, byte prefix) throws IOException {
        if (!canParsePrefix(prefix)) {
            throw new BencodeException("Unknown prefix, cannot parse: " + prefix);
        }

        Map<BString, BNode<?>> result = new TreeMap<>();
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

    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static BDict of(Map<BString, BNode<?>> value) {
        Objects.requireNonNull(value, "value may not be null");

        TreeMap<BString, BNode<?>> temp = new TreeMap<>(value);
        return new BDict(Collections.unmodifiableMap(temp));
    }

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

    @Contract(pure = true)
    public int size() {
        return getValue().size();
    }

    @Contract(pure = true)
    public boolean isEmpty() {
        return getValue().isEmpty();
    }

    @Contract(pure = true)
    public boolean containsKey(BString key) {
        return getValue().containsKey(key);
    }

    @Contract(pure = true)
    public boolean containsValue(BNode<?> value) {
        return getValue().containsValue(value);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Contract(pure = true)
    public <T extends BNode<?>> Optional<T> get(BString key) {
        BNode<?> value = getValue().get(key);
        return Optional.ofNullable((T) value);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Contract(pure = true)
    public <T extends BNode<?>> Optional<T> get(String key) {
        BNode<?> value = getValue().get(BString.of(key));
        return Optional.ofNullable((T) value);
    }

    @NotNull
    @Contract(pure = true)
    public Set<BString> keySet() {
        return getValue().keySet();
    }

    @NotNull
    @Contract(pure = true)
    public Collection<BNode<?>> values() {
        return getValue().values();
    }

    @NotNull
    @Contract(pure = true)
    public Set<Map.Entry<BString, BNode<?>>> entrySet() {
        return getValue().entrySet();
    }

    @NotNull
    @Contract(pure = true, value = "_, _ -> new")
    public BDict put(final BString key, final BNode<?> value) {
        Objects.requireNonNull(key, "key may not be null");
        Objects.requireNonNull(value, "value may not be null");
        TreeMap<BString, BNode<?>> temp = new TreeMap<>(getValue());
        temp.put(key, value);
        return of(temp);
    }

    @NotNull
    @Contract(pure = true)
    public BDict remove(String key) {
        return remove(BString.of(key));
    }

    @NotNull
    @Contract(pure = true)
    public BDict remove(BString key) {
        Objects.requireNonNull(key, "key may not be null");
        TreeMap<BString, BNode<?>> temp = new TreeMap<>(getValue());
        if (temp.remove(key) != null) return of(temp);
        else return this;
    }

    @NotNull
    @Contract(pure = true, value = "_ -> new")
    public BDict join(BDict... others) {
        Objects.requireNonNull(others, "others may not be null");
        TreeMap<BString, BNode<?>> temp = new TreeMap<>(getValue());
        for (BDict other : others) temp.putAll(other.getValue());
        return of(temp);
    }
}
