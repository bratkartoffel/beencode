/*
 * MIT Licence
 * Copyright (c) 2023 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import org.jetbrains.annotations.*;

import java.util.Objects;

abstract class BNodeBase<T> implements BNode<T> {
    @NotNull
    private final T value;

    public BNodeBase(@NotNull T value) {
        this.value = Objects.requireNonNull(value, "value may not be null");
    }

    @Override
    @Contract(pure = true)
    public boolean equals(@Nullable Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        BNode<?> that = (BNode<?>) obj;
        return Objects.equals(this.getValue(), that.getValue());
    }

    @Override
    @Contract(pure = true)
    public int hashCode() {
        return Objects.hash(getClass(), value);
    }

    @Override
    @Contract(pure = true, value = "-> new")
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    @Contract(pure = true)
    public @NotNull T getValue() {
        return value;
    }
}
