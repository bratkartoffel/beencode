/*
 * MIT Licence
 * Copyright (c) 2023 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package examples;

import eu.fraho.libs.beencode.BDict;
import eu.fraho.libs.beencode.BInteger;
import eu.fraho.libs.beencode.BList;
import eu.fraho.libs.beencode.BNode;
import eu.fraho.libs.beencode.BString;

import java.nio.charset.StandardCharsets;

public class ReadmeTypes {
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
        BList listExt = firstlist.with(ipsum);          // [lorem, 2147483648, foobar, ipsum]
        BList listRed = firstlist.without(largeNumber); // [lorem, foobar]
        BList listJoined = firstlist.join(otherlist);   // [lorem, 2147483648, foobar, 13]

        // get info from list
        BNode<?> listEntry = firstlist.get(0);   // BString(lorem)
        BNode<?> listEntry2 = firstlist.get(42); // null

        // Dictionaries: (some kind of map):
        BDict firstdict = BDict.of(lorem, largeNumber);
        BDict otherdict = BDict.of(
            lorem, _42,
            ipsum, BString.of("!")
        );

        // manipulate dict:
        BDict dictExt = firstdict.with(ipsum, _42);    // {lorem=2147483648, ipsum=42}
        BDict dictJoined = firstdict.join(otherdict);  // {lorem=42, ipsum=!}

        // get info from dict
        BNode<?> dictEntry = firstdict.get(lorem);    // BInteger(2147483648)
        BNode<?> dictEntry2 = firstdict.get(ipsum);   // null

        // when using a regular string for get(), the type can be inferred (if you know what type the element has)
        BInteger dictEntry3 = firstdict.get("lorem"); // BInteger(2147483648)
    }
}
