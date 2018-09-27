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

public final class BDict extends BNodeBase<Map<BString, BNode<?>>> {
    private static final long serialVersionUID = 100L;
    private static final byte PREFIX = 'd';
    private static final byte SUFFIX = 'e';

    private BDict(Map<BString, BNode<?>> nodes) {
        super(Collections.unmodifiableMap(new LinkedHashMap<>(nodes)));
    }

    public static BDict of(BNode<?>... nodes) {
        Objects.requireNonNull(nodes, "nodes may not be null");
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

    public static BDict of(InputStream is) throws IOException {
        return of(is, (byte) is.read());
    }

    public static BDict of(InputStream is, byte prefix) throws IOException {
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

    public static BDict of(Map<BString, BNode<?>> value) {
        Objects.requireNonNull(value, "value may not be null");
        return new BDict(value);
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

    public boolean containsKey(BString key) {
        return getValue().containsKey(key);
    }

    public boolean containsValue(BNode<?> value) {
        return getValue().containsValue(value);
    }

    @SuppressWarnings("unchecked")
    public <T extends BNode<?>> Optional<T> get(BString key) {
        return Optional.ofNullable((T) getValue().get(key));
    }

    @SuppressWarnings("unchecked")
    public <T extends BNode<?>> Optional<T> get(String key) {
        return Optional.ofNullable((T) getValue().get(BString.of(key)));
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

    @SuppressWarnings("unchecked")
    @Override
    public BDict clone() {
        try {
            return (BDict) super.clone();
        } catch (BencodeException be) {
            return BDict.of(getValue());
        }
    }
}
