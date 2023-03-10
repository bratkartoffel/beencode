/*
 * MIT Licence
 * Copyright (c) 2023 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package examples;

import eu.fraho.libs.beencode.BInteger;
import eu.fraho.libs.beencode.BList;
import eu.fraho.libs.beencode.BNode;
import eu.fraho.libs.beencode.BString;
import eu.fraho.libs.beencode.NodeFactory;

public class ReadmeConvert {
    public void convert() {
        BList node = BList.of(
                BString.of("Hello"),
                BString.of("world!"),
                BInteger.of(42)
        );

        // Get a human readable representation
        System.out.println(node.toString()); // [Hello, world!, 42]

        // Get the beencoded representation
        byte[] encoded = NodeFactory.encode(node); // l5:Hello6:world!i42ee

        // And back again to a node
        BNode<?> back = NodeFactory.decode(encoded);
    }
}
