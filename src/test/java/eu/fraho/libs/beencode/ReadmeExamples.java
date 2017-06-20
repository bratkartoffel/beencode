/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@SuppressWarnings("unused")
public class ReadmeExamples {
    public void read() throws IOException {
        // If you are entirely sure that the file conains a dictionary
        try (InputStream is = new FileInputStream(new File("test.dat"))) {
            BDict dict = BDict.of(is);
        }

        // If you are pretty sure that the file will contain a single string.
        // When the parsing successfully created a node, but it has the wrong type, then this Optional is empty.
        try (InputStream is = new FileInputStream(new File("test.dat"))) {
            Optional<BString> maybeString = NodeFactory.decode(is, BString.class);
        }

        // If you you don't exactly know which element you will read.
        try (InputStream is = new FileInputStream(new File("test.dat"))) {
            BNode<?> node = NodeFactory.decode(is);
        }
    }

    public void write() throws IOException {
        BList node = BList.of(
                BString.of("Hello"),
                BString.of("world!"),
                BInteger.of(42)
        );

        // Write directly to a file
        try (OutputStream os = new FileOutputStream(new File("test.dat"))) {
            // one way
            node.write(os);

            // another way
            NodeFactory.encode(node, os);
        }
    }

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

    public void types() {
        // String:
        BString lorem = BString.of("lorem");
        BString ipsum = BString.of("ipsum", StandardCharsets.UTF_8);

        // Integer / Long:
        BInteger _42 = BInteger.of(42);
        BInteger largeNumber = BInteger.of(2147483648L);

        // Lists:
        BList firstlist = BList.of(lorem, largeNumber, BString.of("foobar"));
        BList otherlist = BList.of(BInteger.of(13));

        // manipulate list:
        BList listExt = firstlist.add(ipsum);          // [lorem, 2147483648, foobar, ipsum]
        BList listRed = firstlist.remove(largeNumber); // [lorem, foobar]
        BList listJoined = firstlist.join(otherlist);  // [lorem, 2147483648, foobar, 13]

        // Dictionaries: (some kind of map):
        BDict firstdict = BDict.of(lorem, largeNumber);
        BDict otherdict = BDict.of(
                lorem, _42,
                ipsum, BString.of("!")
        );

        // manipulate dict:
        BDict dictExt = firstdict.put(ipsum, _42);    // {lorem=2147483648, ipsum=42}
        BNode<?> dictEntry = firstdict.get(lorem);    // 2147483648
        BDict dictJoined = firstdict.join(otherdict); // {lorem=42, ipsum=!}
    }
}
