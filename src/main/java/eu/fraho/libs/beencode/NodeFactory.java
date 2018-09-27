/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import java.io.*;
import java.util.Objects;
import java.util.Optional;

public final class NodeFactory {
    private NodeFactory() {
        // this util class should not be instantiated
    }

    public static BNode<?> decode(InputStream stream, byte prefix) throws IOException {
        Objects.requireNonNull(stream, "stream may not be null");
        if (BDict.canParsePrefix(prefix)) {
            return BDict.of(stream, prefix);
        } else if (BInteger.canParsePrefix(prefix)) {
            return BInteger.of(stream, prefix);
        } else if (BString.canParsePrefix(prefix)) {
            return BString.of(stream, prefix);
        } else if (BList.canParsePrefix(prefix)) {
            return BList.of(stream, prefix);
        } else {
            throw new BencodeException("No parser found for prefix '" + prefix + "'");
        }
    }

    public static BNode<?> decode(InputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream may not be null");
        return decode(stream, (byte) stream.read());
    }

    public static <T extends BNode<?>> Optional<T> decode(InputStream stream, Class<T> expected) throws IOException {
        Objects.requireNonNull(stream, "stream may not be null");
        Objects.requireNonNull(expected, "expected may not be null");
        BNode<?> result = decode(stream, (byte) stream.read());
        if (expected.isAssignableFrom(result.getClass())) {
            return Optional.of(expected.cast(result));
        } else {
            throw new BencodeException("Parsed the data as " + result.getClass().getSimpleName() + ", but expected " + expected.getSimpleName());
        }
    }

    public static BNode<?> decode(byte[] data) {
        Objects.requireNonNull(data, "data may not be null");
        try (InputStream is = new ByteArrayInputStream(data)) {
            return decode(is);
        } catch (IOException e) {
            // cannot happen as we work on a virtual bytestream and it never throws an IOE
            throw new BencodeException(e);
        }
    }

    public static <T extends BNode<?>> Optional<T> decode(byte[] data, Class<T> expected) {
        Objects.requireNonNull(data, "data may not be null");
        Objects.requireNonNull(expected, "expected may not be null");
        try (InputStream stream = new ByteArrayInputStream(data)) {
            return decode(stream, expected);
        } catch (IOException e) {
            // cannot happen as we work on a virtual bytestream and it never throws an IOE
            throw new BencodeException(e);
        }
    }

    public static byte[] encode(BNode<?> node) {
        Objects.requireNonNull(node, "node may not be null");
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            node.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            // should never happen as we work on a virtual bytestream
            throw new BencodeException(e);
        }
    }

    public static void encode(BNode<?> node, OutputStream os) throws IOException {
        Objects.requireNonNull(node, "node may not be null");
        Objects.requireNonNull(os, "os may not be null");
        node.write(os);
    }
}
