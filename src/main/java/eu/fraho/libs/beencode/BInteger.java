/*
 * MIT Licence
 * Copyright (c) 2023 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Objects;

public final class BInteger extends BNodeBase<BigInteger> implements Comparable<BInteger> {
    private static final long serialVersionUID = 100L;
    private static final int MAX_READ_LEN = 31;
    private static final byte PREFIX = 'i';
    private static final byte SUFFIX = 'e';

    private BInteger(BigInteger value) {
        super(value);
    }

    public static BInteger of(Integer value) {
        Objects.requireNonNull(value, "value may not be null");
        return of(BigInteger.valueOf(value));
    }

    public static BInteger of(Long value) {
        Objects.requireNonNull(value, "value may not be null");
        return of(BigInteger.valueOf(value));
    }

    public static BInteger of(BigInteger value) {
        Objects.requireNonNull(value, "value may not be null");
        return new BInteger(value);
    }

    public static BInteger of(InputStream is) throws IOException {
        return of(is, (byte) is.read());
    }

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

    public static boolean canParsePrefix(byte prefix) {
        return prefix == PREFIX;
    }

    @Override
    public void write(OutputStream os) throws IOException {
        os.write(PREFIX);
        os.write(getValue().toString().getBytes(DEFAULT_CHARSET));
        os.write(SUFFIX);
    }

    @Override
    public int compareTo(BInteger o) {
        Objects.requireNonNull(o, "other is null");
        return getValue().compareTo(o.getValue());
    }

    @Override
    public BInteger clone() {
        try {
            return (BInteger) super.clone();
        } catch (BencodeException be) {
            return BInteger.of(getValue());
        }
    }
}
