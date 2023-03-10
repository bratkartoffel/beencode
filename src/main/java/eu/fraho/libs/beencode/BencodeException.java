/*
 * MIT Licence
 * Copyright (c) 2023 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

public class BencodeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BencodeException(String message) {
        super(message);
    }

    public BencodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BencodeException() {
        super();
    }

    public BencodeException(Throwable cause) {
        super(cause);
    }
}
