/*
 * MIT Licence
 * Copyright (c) 2023 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

public final class BString extends BNodeBase<byte[]> implements Comparable<BString> {
    public static final int DEFAULT_MAX_READ_LEN = 33_554_432; // 32 MiB
    private static final long serialVersionUID = 100L;
    private static final byte SEPARATOR = ':';

    private BString(byte @NotNull [] data) {
        super(new byte[data.length]);
        byte[] value = getValue();
        System.arraycopy(data, 0, value, 0, data.length);
    }

    @Contract(pure = true, value = "_ -> new")
    public static @NotNull BString of(byte @NotNull [] data) {
        Objects.requireNonNull(data, "data may not be null");
        return new BString(data);
    }

    @Contract(pure = true, value = "_ -> new")
    public static @NotNull BString of(@NotNull CharSequence data) {
        return of(data, Charset.defaultCharset());
    }

    @Contract(pure = true, value = "_, _ -> new")
    public static @NotNull BString of(@NotNull CharSequence data, @NotNull Charset charset) {
        return of(data.toString().getBytes(charset));
    }

    @Contract(value = "_ -> new")
    public static @NotNull BString of(@NotNull InputStream is) throws IOException {
        return of(is, (byte) is.read());
    }

    @Contract(value = "_, _ -> new")
    public static @NotNull BString of(@NotNull InputStream is, @Range(from = '0', to = '9') byte prefix) throws IOException {
        return of(is, prefix, DEFAULT_MAX_READ_LEN);
    }

    @Contract(value = "_, _, _ -> new")
    public static @NotNull BString of(@NotNull InputStream is, @Range(from = '0', to = '9') byte prefix, @Range(from = 0, to = Integer.MAX_VALUE) int maxReadLen) throws IOException {
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

    @Override
    @Contract(pure = true)
    public @NotNull String toString() {
        return toString(Charset.defaultCharset());
    }

    @Contract(pure = true)
    public @NotNull String toString(@NotNull Charset encoding) {
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

    @Override
    @Contract(pure = true)
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !BString.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        BString that = (BString) obj;
        return Arrays.equals(this.getValue(), that.getValue());
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    @Contract(pure = true, value = "-> new")
    public @NotNull BString clone() {
        return BString.of(getValue());
    }
}
