beencode
=========

Some java helper classes to deal with binary encoded data strings.
This class collection can be used to parse, alter and generate these beencoded strings.

To read a beencoded file:
=====
```java
BNode<?> bi = null;
try (FileInputStream fstream = new FileInputStream(new File("test.dat"))) {
	bi = NodeFactory.parseByPrefix(fstream.read(), fstream);
}

// You can now work with the BNode Object
```

To write a beencoded file:
=====
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
}
```

Valid data types and usage:
=====
```java
// Strings:
BString mystring = new BString("this is an example");

// Integer:
BInteger myint = new BInteger(1337);

// Lists:
BList mylist = new BList();
BList mylist2 = new BList(new List<BNode<?>>);

// manipulate list:
mylist.getList().add(mystring);
mylist2.getList().remove(myint);

// Dictionaries: (comparable to java Map):
BDict mydict = new BDict();
BDict mydict2 = new BDict(new HashMap<BString, BNode<?>>);

// manipulate dict:
mydict.put(mystring, myint);
mydict.get(mylist);
```

Usage:
=====
As usual, the JUnit Testcases act as examples.
