/*
 * MIT Licence
 * Copyright (c) 2023 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface BNode<T> extends Cloneable, Serializable {
    Charset DEFAULT_CHARSET = StandardCharsets.US_ASCII;

    /**
     * Write this element to the given stream.
     *
     * @param os the stream to write to
     * @throws IOException if the write operation fails
     */
    void write(@NotNull OutputStream os) throws IOException;

    @VisibleForTesting
    @NotNull
    @Contract(pure = true)
    T getValue();
}
