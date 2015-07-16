beencode
=========

Some java helper classes to deal with binary encoded data strings.
This class collection can be used to parse, alter and generate these bencoded strings.
The binary-encoding is primary used by the torrent protocol, but is free to use elsewhere.
For details see [the bencoding specification](https://wiki.theory.org/BitTorrentSpecification#Bencoding).

Usage (from pom.xml):
```xml
<dependency>
	<groupId>se.wfh.libs</groupId>
	<artifactId>beencode</artifactId>
	<version>0.7</version>
</dependency>
```
		
To read a beencoded file:
-------------
```java
BDict dict = null;
BNode<?> node = null;

try (FileInputStream fstream = new FileInputStream(new File("test.dat"))) {
	// If you know that the file will contain a dictinary and force so
	dict = BNode<?>.of(fstream, BDict.class);
	// If you you don't exactly know which element you will read
	node = BNode<?>.of(fstream);
}
```

To write a beencoded file:
-------------
```java
List<BNode> list = new ArrayList<>();

list.add(BInteger.of(42));
list.add(BString.of("bar"));

BDict bd = BDict.empty();
BList bl = BList.of(list);

bd.put(BString.of("foo"), BInteger.of(13));
bd.put(BString.of("mylist"), bl);

try (FileOutputStream fos = new FileOutputStream(new File("test.dat"))) {
	NodeFactory.encode(bd, fos);
}
```

Valid data types and usage:
-------------
```java
// Strings:
BString mystring = BString.of("this is an example");
BString mystring2 = BString.of("Lörem", StandardCharsets.UTF_8);

// Integer:
BInteger myint = BInteger.of(42);

// Lists:
BList mylist = BList.empty();
BList mylist2 = BList.of(BInteger.of(2), BString.of("foobar"));

// manipulate list:
mylist.add(mystring);
mylist2.remove(myint);

// Dictionaries: (comparable to java Map):
BDict mydict = BDict.empty();
BDict mydict2 = BDict.of(new HashMap<BString, BNode<?>>);

// manipulate dict:
mydict.put(mystring, myint);
mydict.get(mylist);
```

To handle encoding properly:
-------------
```java
BString output = BString.of("我", StandardCharsets.UTF_16);

try (FileOutputStream fos = new FileOutputStream(new File("test.dat"))) {
	NodeFactory.encode(output, fos);
}

BString input = null;
try (FileInputStream fis = new FileInputStream(new File("test.dat"))) {
	input = NodeFactory.decode(fis, BString.class);
}

System.out.println("Written: " + output);
System.out.println("Read:    " + input);
System.out.println("Equal?   " + Objects.equals(input, output));
```

Usage:
-------------
As usual, the JUnit Testcases act as examples.
