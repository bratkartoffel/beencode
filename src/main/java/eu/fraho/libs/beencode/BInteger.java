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
import java.util.Objects;

@Immutable
public final class BInteger extends BNode<Long> implements Comparable<BInteger> {
    private static final int MAX_READ_LEN = 21;
    private static final long serialVersionUID = 1L;
    private static final byte PREFIX = 'i';
    private static final byte SUFFIX = 'e';

    private BInteger(@NotNull Long value) {
        super(value);
    }

    @NotNull
    public static BInteger of(long value) {
        return of(Long.valueOf(value));
    }

    @NotNull
    public static BInteger of(int value) {
        return of(Long.valueOf(value));
    }

    @NotNull
    public static BInteger of(@NotNull Integer value) {
        Objects.requireNonNull(value, "value may not be null");
        return of(Long.valueOf(value));
    }

    @NotNull
    public static BInteger of(@NotNull Long value) {
        Objects.requireNonNull(value, "value may not be null");
        return new BInteger(value);
    }

    @NotNull
    public static BInteger of(@NotNull InputStream is) throws IOException {
        return of(is, (byte) is.read());
    }

    @NotNull
    public static BInteger of(@NotNull InputStream is, byte prefix) throws IOException {
        if (!canParsePrefix(prefix)) {
            throw new BencodeException("Unknown prefix, cannot parse: " + prefix);
        }

        StringBuilder str = new StringBuilder(MAX_READ_LEN);
        byte read = 0;
        for (int i = 0; i < MAX_READ_LEN; i++) {
            read = (byte) is.read();
            if (read == SUFFIX) break;
            str.append((char) read);
        }

        if (read != SUFFIX) {
            throw new BencodeException("Invalid data, did not find suffix within " + MAX_READ_LEN + " bytes");
        }

        String input = str.toString();
        int length = input.length();
        if (length == 0) {
            throw new BencodeException("Invalid data, no data read");
        }
        if (input.equals("-")) {
            throw new BencodeException("Invalid data, only a dash was read");
        }
        if (input.equals("-0")) {
            throw new BencodeException("Invalid data, negative zero is not allowed");
        }
        if (input.startsWith("0") && length > 1 || input.startsWith("-0")
                && length > 2) {
            throw new BencodeException("Invalid data, leading zeros are not allowed");
        }

        try {
            return of(Long.parseLong(input));
        } catch (NumberFormatException nfe) {
            throw new BencodeException(nfe);
        }
    }

    public static boolean canParsePrefix(byte prefix) {
        return prefix == PREFIX;
    }

    @Override
    public void write(@NotNull OutputStream os) throws IOException {
        os.write(PREFIX);
        os.write(String.valueOf(getValue()).getBytes(DEFAULT_CHARSET));
        os.write(SUFFIX);
    }

    @Override
    public int compareTo(@NotNull BInteger o) {
        return getValue().compareTo(o.getValue());
    }
}
