/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import net.jcip.annotations.Immutable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

@Immutable
public final class BDict extends BNode<Map<BString, BNode<?>>> {
    private static final long serialVersionUID = 1L;
    private static final byte PREFIX = 'd';
    private static final byte SUFFIX = 'e';

    private BDict(Map<BString, BNode<?>> nodes) {
        super(nodes);
    }

    public static BDict of(BNode<?>... nodes) {
        Objects.requireNonNull(nodes, "nodes may not be null");

        TreeMap<BString, BNode<?>> temp = new TreeMap<>();
        for (int i = 0; i < nodes.length; i += 2) {
            if (!BString.class.isInstance(nodes[i])) {
                Class<?> clz = nodes[i] == null ? null : nodes[i].getClass();
                throw new BencodeException("key as argument #" + i + " has to be a BString (is " + clz + ")");
            }
            temp.put((BString) nodes[i], nodes[i + 1]);
        }
        return of(temp);
    }

    public static BDict of(Map<BString, BNode<?>> value) {
        Objects.requireNonNull(value, "value may not be null");

        TreeMap<BString, BNode<?>> temp = new TreeMap<>();
        temp.putAll(value);
        return new BDict(Collections.unmodifiableMap(temp));
    }

    public static BDict of(InputStream is) throws IOException {
        return of(is, (byte) is.read());
    }

    public static BDict of(InputStream is, byte prefix) throws IOException {
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
    public void write(OutputStream os) throws IOException {
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

    public boolean containsKey(Object key) {
        return BString.class.isInstance(key) && getValue().containsKey(key);
    }

    public boolean containsValue(Object value) {
        return getValue().containsValue(value);
    }

    public BNode<?> get(Object key) {
        if (key instanceof BString) {
            return getValue().get(key);
        } else {
            return getValue().get(BString.of(String.valueOf(key)));
        }
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

    public BDict put(final BString key, final BNode<?> value) {
        Objects.requireNonNull(key, "key may not be null");
        Objects.requireNonNull(value, "value may not be null");
        TreeMap<BString, BNode<?>> temp = new TreeMap<>(getValue());
        temp.put(key, value);
        return of(temp);
    }

    public BDict remove(String key) {
        return remove(BString.of(key));
    }

    public BDict remove(BString key) {
        Objects.requireNonNull(key, "key may not be null");
        TreeMap<BString, BNode<?>> temp = new TreeMap<>(getValue());
        if (temp.remove(key) != null) return of(temp);
        else return this;
    }

    public BDict join(BDict... others) {
        Objects.requireNonNull(others, "others may not be null");
        TreeMap<BString, BNode<?>> temp = new TreeMap<>(getValue());
        for (BDict other : others) temp.putAll(other.getValue());
        return of(temp);
    }
}
