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
import java.math.BigInteger;
import java.util.Objects;

public final class BInteger extends BNode<BigInteger> implements Comparable<BInteger> {
    private static final int MAX_READ_LEN = 21;
    private static final long serialVersionUID = 1L;
    private static final byte PREFIX = 'i';
    private static final byte SUFFIX = 'e';

    private BInteger(BigInteger value) {
        super(value);
    }

    @Contract(pure = true, value = "_ -> new")
    @NotNull
    public static BInteger of(long value) {
        return of(BigInteger.valueOf(value));
    }

    @Contract(pure = true, value = "_ -> new")
    @NotNull
    public static BInteger of(int value) {
        return of(BigInteger.valueOf(value));
    }

    @Contract(pure = true, value = "_ -> new")
    @NotNull
    public static BInteger of(Integer value) {
        Objects.requireNonNull(value, "value may not be null");
        return of(BigInteger.valueOf(value));
    }

    @Contract(pure = true, value = "_ -> new")
    @NotNull
    public static BInteger of(Long value) {
        Objects.requireNonNull(value, "value may not be null");
        return of(BigInteger.valueOf(value));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static BInteger of(BigInteger value) {
        Objects.requireNonNull(value, "value may not be null");
        return new BInteger(value);
    }

    @Contract("_ -> new")
    @NotNull
    public static BInteger of(InputStream is) throws IOException {
        return of(is, (byte) is.read());
    }

    @Contract("_, _ -> new")
    @NotNull
    public static BInteger of(InputStream is, byte prefix) throws IOException {
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
            return of(new BigInteger(input));
        } catch (NumberFormatException nfe) {
            throw new BencodeException(nfe);
        }
    }

    @Contract(pure = true)
    public static boolean canParsePrefix(byte prefix) {
        return prefix == PREFIX;
    }

    @Override
    public void write(@NotNull OutputStream os) throws IOException {
        os.write(PREFIX);
        os.write(getValue().toString().getBytes(DEFAULT_CHARSET));
        os.write(SUFFIX);
    }

    @Contract(pure = true)
    @Override
    public int compareTo(@NotNull BInteger o) {
        Objects.requireNonNull(o, "other is null");
        return getValue().compareTo(o.getValue());
    }
}
