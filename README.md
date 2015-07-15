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

list.add(new BInteger(42));
list.add(new BString("bar"));

BDict bd = new BDict();
BList bl = new BList(list);

bd.put(new BString("foo"), new BInteger(13));
bd.put(new BString("mylist"), bl);

try (FileOutputStream fos = new FileOutputStream(new File("test.dat"))) {
	bd.write(fos);
	// or
	fos.write(bd.getEncoded());
}
```

Valid data types and usage:
-------------
```java
// Strings:
BString mystring = new BString("this is an example");

// Integer:
BInteger myint = new BInteger(1337);

// Lists:
BList mylist = new BList();
BList mylist2 = new BList(new ArrayList<BNode<?>>());

// manipulate list:
mylist.add(mystring);
mylist2.remove(myint);

// Dictionaries: (comparable to java Map):
BDict mydict = new BDict();
BDict mydict2 = new BDict(new HashMap<BString, BNode<?>>);

// manipulate dict:
mydict.put(mystring, myint);
mydict.get(mylist);
```

To handle encoding properly:
-------------
```java
BString output = new BString("我", StandardCharsets.UTF_16);

try (FileOutputStream fos = new FileOutputStream(new File("test.dat"))) {
	fos.write(output.getEncoded());
}

BString input = null;
try (FileInputStream fis = new FileInputStream(new File("test.dat"))) {
	input = BNode.of(fis, BString.class);
}

System.out.println("Written: " + output);
System.out.println("Read:    " + input);
System.out.println("Equal?   " + Objects.equals(input, output));
```

Usage:
-------------
As usual, the JUnit Testcases act as examples.
