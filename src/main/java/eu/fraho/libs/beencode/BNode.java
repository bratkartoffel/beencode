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
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public abstract class BNode<T> implements Cloneable, Serializable {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.US_ASCII;
    private static final long serialVersionUID = 1L;

    @NotNull
    private final T value;

    public BNode(@NotNull T value) {
        this.value = Objects.requireNonNull(value, "value may not be null");
    }

    public abstract void write(OutputStream os) throws IOException;

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        BNode<?> that = (BNode<?>) obj;
        return Objects.equals(this.getValue(), that.getValue());
    }

    @Override
    @Contract(pure = true)
    public int hashCode() {
        return getClass().hashCode() + value.hashCode();
    }

    @Override
    @Contract(pure = true)
    public String toString() {
        return String.valueOf(value);
    }

    @NotNull
    public T getValue() {
        return value;
    }
}
