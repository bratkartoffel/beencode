/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

@Immutable
public final class BDict extends BNode<Map<BString, BNode<?>>> {
    private static final long serialVersionUID = 1L;
    private static final byte PREFIX = 'd';
    private static final byte SUFFIX = 'e';

    private BDict(@NotNull Map<BString, BNode<?>> nodes) {
        super(nodes);
    }

    @NotNull
    public static BDict of(@NotNull BNode<?>... nodes) {
        Objects.requireNonNull(nodes, "nodes may not be null");

        TreeMap<BString, BNode<?>> temp = new TreeMap<>();
        for (int i = 0; i < nodes.length; i += 2) {
            if (!BString.class.isInstance(nodes[i])) {
                Class<?> clz = nodes[i] == null ? null : nodes[i].getClass();
                throw new BencodeException("key as argument #" + i + " has to be a BString (is " + clz + ")");
            }
            if (nodes[i + 1] == null) {
                throw new BencodeException("value argument #" + i + " has to be not null");
            }
            temp.put((BString) nodes[i], nodes[i + 1]);
        }
        return of(temp);
    }

    @NotNull
    public static BDict of(@NotNull Map<BString, BNode<?>> value) {
        Objects.requireNonNull(value, "value may not be null");

        TreeMap<BString, BNode<?>> temp = new TreeMap<>();
        temp.putAll(value);
        return new BDict(Collections.unmodifiableMap(temp));
    }

    @NotNull
    public static BDict of(@NotNull InputStream is) throws IOException {
        return of(is, (byte) is.read());
    }

    @NotNull
    public static BDict of(@NotNull InputStream is, byte prefix) throws IOException {
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

    public int size() {
        return getValue().size();
    }

    public boolean isEmpty() {
        return getValue().isEmpty();
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean containsKey(BNode<?> key) {
        return getValue().containsKey(key);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean containsValue(BNode<?> value) {
        return getValue().containsValue(value);
    }

    public Optional<BNode<?>> get(BString key) {
        return Optional.ofNullable(getValue().get(key));
    }

    public Optional<BNode<?>> get(String key) {
        return Optional.ofNullable(getValue().get(BString.of(key)));
    }

    public Set<BString> keySet() {
        return getValue().keySet();
    }

    public Collection<BNode<?>> values() {
        return getValue().values();
    }

    public Set<Map.Entry<BString, BNode<?>>> entrySet() {
        return getValue().entrySet();
    }

    @NotNull
    public BDict put(@NotNull final BString key, @NotNull final BNode<?> value) {
        Objects.requireNonNull(key, "key may not be null");
        Objects.requireNonNull(value, "value may not be null");
        TreeMap<BString, BNode<?>> temp = new TreeMap<>(getValue());
        temp.put(key, value);
        return of(temp);
    }

    @NotNull
    public BDict remove(@NotNull String key) {
        return remove(BString.of(key));
    }

    @NotNull
    public BDict remove(@NotNull BString key) {
        Objects.requireNonNull(key, "key may not be null");
        TreeMap<BString, BNode<?>> temp = new TreeMap<>(getValue());
        if (temp.remove(key) != null) return of(temp);
        else return this;
    }

    @NotNull
    public BDict join(@NotNull BDict... others) {
        Objects.requireNonNull(others, "others may not be null");
        TreeMap<BString, BNode<?>> temp = new TreeMap<>(getValue());
        for (BDict other : others) temp.putAll(other.getValue());
        return of(temp);
    }
}
