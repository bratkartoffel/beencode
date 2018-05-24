/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;
import java.util.Optional;

public final class NodeFactory {
    private NodeFactory() {
        // this util class should not be instantiated
    }

    @NotNull
    public static BNode<?> decode(@NotNull InputStream is, byte prefix) throws IOException {
        Objects.requireNonNull(is, "is may not be null");
        if (BDict.canParsePrefix(prefix)) {
            return BDict.of(is, prefix);
        } else if (BInteger.canParsePrefix(prefix)) {
            return BInteger.of(is, prefix);
        } else if (BString.canParsePrefix(prefix)) {
            return BString.of(is, prefix);
        } else if (BList.canParsePrefix(prefix)) {
            return BList.of(is, prefix);
        } else {
            throw new BencodeException("No parser found for prefix '" + prefix + "'");
        }
    }

    @NotNull
    public static BNode<?> decode(@NotNull InputStream is) throws IOException {
        return decode(is, (byte) is.read());
    }

    @NotNull
    public static <T extends BNode<?>> Optional<T> decode(@NotNull InputStream is, @NotNull Class<T> expected) throws IOException {
        Objects.requireNonNull(is, "is may not be null");
        Objects.requireNonNull(expected, "expected may not be null");
        BNode<?> result = decode(is, (byte) is.read());
        if (expected.isAssignableFrom(result.getClass())) {
            return Optional.of(expected.cast(result));
        } else {
            return Optional.empty();
        }
    }

    @NotNull
    public static BNode<?> decode(byte[] data) {
        try (InputStream is = new ByteArrayInputStream(data)) {
            return decode(is);
        } catch (IOException e) {
            // cannot happen as we work on a virtual bytestream and it never throws an IOE
            throw new BencodeException(e);
        }
    }

    @NotNull
    public static <T extends BNode<?>> Optional<T> decode(@NotNull byte[] data, @NotNull Class<T> expected) {
        Objects.requireNonNull(data, "data may not be null");
        Objects.requireNonNull(expected, "expected may not be null");
        BNode<?> result = decode(data);
        if (expected.isAssignableFrom(result.getClass())) {
            return Optional.of(expected.cast(result));
        } else {
            return Optional.empty();
        }
    }

    @NotNull
    public static byte[] encode(@NotNull BNode<?> node) {
        Objects.requireNonNull(node, "node may not be null");
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            node.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            // should never happen as we work on a virtual bytestream
            throw new BencodeException(e);
        }
    }

    public static void encode(@NotNull BNode<?> node, @NotNull OutputStream os)
            throws IOException {
        Objects.requireNonNull(node, "node may not be null");
        Objects.requireNonNull(os, "os may not be null");
        node.write(os);
    }
}
