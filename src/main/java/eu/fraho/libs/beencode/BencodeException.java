/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import org.jetbrains.annotations.NotNull;

public class BencodeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BencodeException(@NotNull String message) {
        super(message);
    }

    public BencodeException() {
        super();
    }

    public BencodeException(@NotNull Throwable cause) {
        super(cause);
    }
}
