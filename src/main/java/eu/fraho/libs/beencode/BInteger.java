/*
 * MIT Licence
 * Copyright (c) 2023 Simon Frankenberger
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

public final class BInteger extends BNodeBase<BigInteger> implements Comparable<BInteger> {
    private static final long serialVersionUID = 100L;
    // maximum length of 64 bit signed integer is 20 chars + suffix
    private static final int MAX_READ_LEN = 21;
    private static final byte PREFIX = 'i';
    private static final byte SUFFIX = 'e';

    private BInteger(@NotNull BigInteger value) {
        super(value);
    }

    /**
     * Create a new instance of the given value
     *
     * @param value value
     * @return new instance
     */
    @Contract(pure = true, value = "_ -> new")
    public static @NotNull BInteger of(@NotNull Integer value) {
        return of(BigInteger.valueOf(value));
    }

    /**
     * Create a new instance of the given value
     *
     * @param value value
     * @return new instance
     */
    @Contract(pure = true, value = "_ -> new")
    public static @NotNull BInteger of(@NotNull Long value) {
        return of(BigInteger.valueOf(value));
    }

    /**
     * Create a new instance of the given value
     *
     * @param value value
     * @return new instance
     */
    @Contract(pure = true, value = "_ -> new")
    public static @NotNull BInteger of(@NotNull BigInteger value) {
        return new BInteger(value);
    }

    /**
     * Parse the given stream for a BInteger
     *
     * @param is stream of data
     * @return new instance
     * @throws BencodeException if the given prefix is not {@link #PREFIX} or the parsed stream is invalid
     */
    @Contract(pure = true, value = "_ -> new")
    public static @NotNull BInteger of(@NotNull InputStream is) throws IOException {
        return of(is, (byte) is.read());
    }

    /**
     * Parse the given stream for a BInteger
     *
     * @param is     stream of data
     * @param prefix first read byte, has to be {@link #PREFIX}
     * @return new instance
     * @throws BencodeException if the given prefix is not {@link #PREFIX} or the parsed stream is invalid
     */
    @Contract(value = "_, _ -> new")
    public static @NotNull BInteger of(@NotNull InputStream is, byte prefix) throws IOException {
        if (!canParsePrefix(prefix)) {
            throw new BencodeException("Unknown prefix, cannot parse: " + prefix);
        }

        StringBuilder str = new StringBuilder(MAX_READ_LEN);
        byte read = 0;
        for (int i = 0; i < MAX_READ_LEN; i++) {
            read = (byte) is.read();
            if (read == SUFFIX) {
                break;
            }
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
        if (input.startsWith("0") && length > 1 || input.startsWith("-0")) {
            throw new BencodeException("Invalid data, leading zeros are not allowed");
        }

        try {
            return of(new BigInteger(input));
        } catch (NumberFormatException nfe) {
            throw new BencodeException(nfe);
        }
    }

    /**
     * @param prefix the prefix to check
     * @return is the given byte the expected prefix for this element?
     * @see #PREFIX
     */
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

    @Override
    @Contract(pure = true)
    public int compareTo(@NotNull BInteger o) {
        return getValue().compareTo(o.getValue());
    }

    /**
     * Clone this object
     *
     * @return copy of this object
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    @Contract(pure = true, value = "-> new")
    public @NotNull BInteger clone() {
        return BInteger.of(getValue());
    }
}
