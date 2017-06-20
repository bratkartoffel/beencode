beencode
=========
[![Build Status](https://travis-ci.org/bratkartoffel/beencode.svg?branch=develop)](https://travis-ci.org/bratkartoffel/beencode)
[![Code Coverage](https://img.shields.io/codecov/c/github/bratkartoffel/beencode/develop.svg)](https://codecov.io/github/bratkartoffel/beencode?branch=develop)
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat)](http://doge.mit-license.org)

Some java helper classes to deal with binary encoded data strings.
This class collection can be used to parse, alter and generate these bencoded strings.
The binary-encoding is primary used by the torrent protocol, but is free to use elsewhere.
For details see [the bencoding specification](https://wiki.theory.org/BitTorrentSpecification#Bencoding).

All classes are immutable and thus the whole library is threadsafe.

# Dependencies
```xml
<dependency>
	<groupId>eu.fraho.libs</groupId>
	<artifactId>beencode</artifactId>
	<version>1.0.0</version>
</dependency>
```
		
# Building
```bash
# on linux:
./gradlew assemble
# on windows:
gradlew.bat assemble
```

# Usage
* All instances from this library are immutable, each change creates a new instance
* Use the various ```of``` methods on the datatypes to create instances
* The ```write(OutputStream)``` methods can be used to write the data beencoded to a stream
* The ```toString()``` methods return a humand readable presentation of the data

# Code examples
## Valid data types and usage:
```java
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
```

## To read a beencoded file:
```java
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
```

## To write a beencoded file:
```java
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
```

## Convert from and to beencoded data:
```java
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
```

# Hacking
* This repository uses the git flow layout
* Changes are welcome, but please use pull requests with separate branches
* TravisCI has to pass before merging
* Code coverage should stay about the same level (please write tests for new features!)

# Releasing
```bash
# to local repository:
./gradlew install
# to central:
./gradlew -Prelease check uploadArchives
```
