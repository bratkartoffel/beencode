# Bencoded data reader and writer for java

[![Java CI](https://github.com/bratkartoffel/beencode/actions/workflows/build.yaml/badge.svg)](https://github.com/bratkartoffel/beencode/actions/workflows/build.yaml)
[![codecov](https://codecov.io/gh/bratkartoffel/beencode/branch/develop/graph/badge.svg?token=QgUmkgHSMd)](https://codecov.io/gh/bratkartoffel/beencode)
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat)](http://doge.mit-license.org)
[![Central Version](https://img.shields.io/maven-central/v/eu.fraho.libs/beencode)](https://mvnrepository.com/artifact/eu.fraho.libs/beencode)

Some java helper classes to deal with binary encoded data strings.
This class collection can be used to parse, alter and generate bencoded data.
The binary-encoding is primary used by the torrent protocol, but is free to use elsewhere.
For details see [the bencoding specification](https://wiki.theory.org/BitTorrentSpecification#Bencoding).

All classes are immutable and thus the whole library is threadsafe.
This library was designed to be easy to use, lean (no external dependencies), secure and performant.

# Dependencies

```xml
<!-- https://mvnrepository.com/artifact/eu.fraho.libs/beencode -->
<dependency>
    <groupId>eu.fraho.libs</groupId>
    <artifactId>beencode</artifactId>
    <version>2.0.1</version>
</dependency>
```

# Usage

* All instances from this library are immutable, each change creates a new instance
* Use the various ```of``` methods on the datatypes to create instances
* The ```write(OutputStream)``` methods can be used to write the data beencoded to a stream
* The ```toString()``` methods return a humand readable presentation of the data

# Code examples

## Valid data types and usage:

```java
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
```

## To read a beencoded file:

```java
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
```

## To write a beencoded file:

```java
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
```

## Convert from and to beencoded data:

```java
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

```

# Hacking

* This repository uses the git flow layout
* Changes are welcome, but please use pull requests with separate branches
* Github workflow has to pass before merging
* Code coverage should stay about the same level (please write tests for new features!)

# Releasing

Releasing is done with the default gradle tasks:

```bash
# to local repository:
./gradlew publishToMavenLocal
# to central:
./gradlew publish
```
