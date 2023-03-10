/*
 * MIT Licence
 * Copyright (c) 2023 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import java.util.Objects;

abstract class BNodeBase<T> implements BNode<T> {
    private final T value;

    public BNodeBase(T value) {
        this.value = Objects.requireNonNull(value, "value may not be null");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        BNode<?> that = (BNode<?>) obj;
        return Objects.equals(this.getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + value.hashCode();
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public T getValue() {
        return value;
    }

    // only for unit testing
    @SuppressWarnings("RedundantThrows")
    protected void preCloneForUnitTesting() throws CloneNotSupportedException {
    }

    @SuppressWarnings("unchecked")
    @Override
    public BNode<T> clone() {
        try {
            preCloneForUnitTesting();
            return (BNode<T>) super.clone();
        } catch (CloneNotSupportedException cnse) {
            // we know that this class is cloneable
            throw new BencodeException("Clone failed", cnse);
        }
    }
}
