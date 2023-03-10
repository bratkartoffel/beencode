/*
 * MIT Licence
 * Copyright (c) 2023 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package examples;

import eu.fraho.libs.beencode.BDict;
import eu.fraho.libs.beencode.BNode;
import eu.fraho.libs.beencode.BString;
import eu.fraho.libs.beencode.NodeFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

public class ReadmeRead {
    public void read() throws IOException {
        // If you are entirely sure that the file conains a dictionary
        try (InputStream is = Files.newInputStream(new File("test.dat").toPath())) {
            BDict dict = BDict.of(is);
        }

        // If you are pretty sure that the file will contain a single string.
        // When the parsing successfully created a node, but it has the wrong type, then this Optional is empty.
        try (InputStream is = Files.newInputStream(new File("test.dat").toPath())) {
            Optional<BString> maybeString = NodeFactory.decode(is, BString.class);
        }

        // If you you don't exactly know which element you will read.
        try (InputStream is = Files.newInputStream(new File("test.dat").toPath())) {
            BNode<?> node = NodeFactory.decode(is);
        }
    }
}
