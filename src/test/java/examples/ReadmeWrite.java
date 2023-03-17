/*
 * MIT Licence
 * Copyright (c) 2023 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package examples;

import eu.fraho.libs.beencode.BInteger;
import eu.fraho.libs.beencode.BList;
import eu.fraho.libs.beencode.BString;
import eu.fraho.libs.beencode.NodeFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class ReadmeWrite {
    public void write() throws IOException {
        BList node = BList.of(
            BString.of("Hello"),
            BString.of("world!"),
            BInteger.of(42)
        );

        // Write directly to a file
        try (OutputStream os = Files.newOutputStream(new File("test.dat").toPath())) {
            // one way
            node.write(os);

            // another way
            NodeFactory.encode(node, os);
        }
    }
}
