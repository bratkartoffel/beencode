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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

public final class BString extends BNode<byte[]> implements Comparable<BString> {
    public static final int DEFAULT_MAX_READ_LEN = 33_554_432; // 32 MiB
    private static final long serialVersionUID = 1L;
    private static final byte SEPARATOR = ':';

    private BString(@NotNull byte[] data) {
        super(new byte[data.length]);
        System.arraycopy(data, 0, getValue(), 0, data.length);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static BString of(byte[] data) {
        Objects.requireNonNull(data, "data may not be null");
        return new BString(data);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static BString of(CharSequence data) {
        return of(data, Charset.defaultCharset());
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static BString of(CharSequence data, Charset charset) {
        Objects.requireNonNull(data, "data may not be null");
        Objects.requireNonNull(charset, "charset may not be null");
        return of(data.toString().getBytes(charset));
    }

    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static BString of(InputStream is) throws IOException {
        return of(is, (byte) is.read());
    }

    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    public static BString of(InputStream is, byte prefix) throws IOException {
        return of(is, prefix, DEFAULT_MAX_READ_LEN);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    @NotNull
    public static BString of(InputStream is, byte prefix, int maxReadLen) throws IOException {
        long length = prefix - '0';

        byte cur;
        while ((cur = (byte) is.read()) != SEPARATOR) {
            if (!canParsePrefix(cur)) {
                throw new BencodeException("Unexpected data, expected an digit but got a '"
                        + cur + "'");
            }
            length = length * 10 + (cur - '0');
        }

        if (length > 0 && prefix == '0') {
            throw new BencodeException("Leading zeros are not allowed.");
        }

        if (length > maxReadLen) {
            throw new BencodeException("Denied attempt to read " + length + " bytes.");
        }

        int ilength = (int) length;
        byte[] value = new byte[ilength];
        int offset = 0;
        while (offset != ilength) {
            int temp = is.read(value, offset, ilength - offset);
            if (temp >= 0) {
                offset += temp;
            } else {
                throw new BencodeException("Premature end of stream, missing "
                        + (ilength - offset) + " bytes.");
            }
        }
        return of(value);
    }

    @Contract(pure = true)
    public static boolean canParsePrefix(byte prefix) {
        return prefix >= '0' && prefix <= '9';
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public String toString() {
        return toString(Charset.defaultCharset());
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public String toString(Charset encoding) {
        return new String(getValue(), encoding);
    }

    @Override
    public void write(@NotNull OutputStream os) throws IOException {
        os.write(String.valueOf(getValue().length).getBytes(DEFAULT_CHARSET));
        os.write(SEPARATOR);
        os.write(getValue());
    }

    @Override
    @Contract(pure = true)
    public int hashCode() {
        return getClass().hashCode() + Arrays.hashCode(getValue());
    }

    @Override
    @Contract(pure = true)
    public int compareTo(@NotNull BString o) {
        return toString().compareTo(o.toString());
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !BString.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        BString that = (BString) obj;
        return Arrays.equals(this.getValue(), that.getValue());
    }
}
