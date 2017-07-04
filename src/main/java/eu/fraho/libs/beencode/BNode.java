/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public abstract class BNode<T> implements Cloneable, Serializable {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.US_ASCII;
    private static final long serialVersionUID = 1L;

    @NotNull
    private final T value;

    public BNode(@NotNull T value) {
        this.value = value;
    }

    public abstract void write(@NotNull OutputStream os) throws IOException;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !BNode.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        boolean result = getClass().equals(obj.getClass());
        if (result) {
            BNode<?> other = (BNode<?>) obj;
            if (this.getClass().isAssignableFrom(BString.class)) {
                result = Arrays.equals((byte[]) value, (byte[]) other.getValue());
            } else {
                result = value.equals(other.getValue());
            }
        }

        return result;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + value.hashCode();
    }

    @Override
    @NotNull
    public String toString() {
        return String.valueOf(value);
    }

    @NotNull
    public T getValue() {
        return value;
    }
}
