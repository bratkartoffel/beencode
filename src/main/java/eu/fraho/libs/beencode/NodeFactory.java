/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import java.io.*;
import java.util.Optional;

public abstract class NodeFactory {
    public static BNode<?> decode(InputStream is, byte prefix) throws IOException {
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

    public static BNode<?> decode(InputStream is) throws IOException {
        return decode(is, (byte) is.read());
    }

    public static <T extends BNode<?>> Optional<T> decode(InputStream is, Class<T> expected) throws IOException {
        BNode<?> result = decode(is, (byte) is.read());
        if (expected.isAssignableFrom(result.getClass())) {
            return Optional.of(expected.cast(result));
        } else {
            return Optional.empty();
        }
    }

    public static BNode<?> decode(byte[] data) {
        try (InputStream is = new ByteArrayInputStream(data)) {
            return decode(is);
        } catch (IOException e) {
            // should never happen as we work on a virtual bytestream
            throw new BencodeException(e);
        }
    }

    public static <T extends BNode<?>> Optional<T> decode(byte[] data, Class<T> expected) {
        BNode<?> result = decode(data);
        if (expected.isAssignableFrom(result.getClass())) {
            return Optional.of(expected.cast(result));
        } else {
            return Optional.empty();
        }
    }


    public static byte[] encode(BNode<?> node) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            node.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            // should never happen as we work on a virtual bytestream
            throw new BencodeException(e);
        }
    }

    public static void encode(BNode<?> node, OutputStream os)
            throws IOException {
        node.write(os);
    }
}
