/*
 * MIT Licence
 * Copyright (c) 2023 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;
import java.util.Optional;

public final class NodeFactory {
    private NodeFactory() {
        // this util class should not be instantiated
    }

    @Contract(value = "_, _ -> new")
    public static @NotNull BNode<?> decode(@NotNull InputStream stream, byte prefix) throws IOException {
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

    @Contract(value = "_ -> new")
    public static @NotNull BNode<?> decode(@NotNull InputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream may not be null");
        return decode(stream, (byte) stream.read());
    }

    public static <T extends BNode<?>> Optional<T> decode(@NotNull InputStream stream, @NotNull Class<T> expected) throws IOException {
        BNode<?> result = decode(stream, (byte) stream.read());
        if (expected.isAssignableFrom(result.getClass())) {
            return Optional.of(expected.cast(result));
        } else {
            throw new BencodeException("Parsed the data as " + result.getClass().getSimpleName() + ", but expected " + expected.getSimpleName());
        }
    }

    @Contract(pure = true, value = "_ -> new")
    public static @NotNull BNode<?> decode(byte @NotNull [] data) {
        Objects.requireNonNull(data, "data may not be null");
        try (InputStream is = new ByteArrayInputStream(data)) {
            return decode(is);
        } catch (IOException e) {
            // cannot happen as we work on a virtual bytestream and it never throws an IOE
            throw new BencodeException(e);
        }
    }

    @Contract(pure = true, value = "_, _ -> new")
    public static <T extends BNode<?>> Optional<T> decode(byte @NotNull [] data, @NotNull Class<T> expected) {
        try (InputStream stream = new ByteArrayInputStream(data)) {
            return decode(stream, expected);
        } catch (IOException e) {
            // cannot happen as we work on a virtual bytestream and it never throws an IOE
            throw new BencodeException(e);
        }
    }

    @Contract(pure = true, value = "_ -> new")
    public static byte @NotNull [] encode(@NotNull BNode<?> node) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            node.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            // should never happen as we work on a virtual bytestream
            throw new BencodeException(e);
        }
    }

    public static void encode(@NotNull BNode<?> node, @NotNull OutputStream os) throws IOException {
        node.write(os);
    }
}
