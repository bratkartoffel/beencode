/*
 * MIT Licence
 * Copyright (c) 2018 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface BNode<T> extends Cloneable, Serializable {
    Charset DEFAULT_CHARSET = StandardCharsets.US_ASCII;

    void write(OutputStream os) throws IOException;

    T getValue();

    BNode<T> clone();
}
