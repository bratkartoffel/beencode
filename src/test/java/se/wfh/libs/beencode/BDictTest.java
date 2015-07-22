package se.wfh.libs.beencode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import se.wfh.libs.beencode.BDict;
import se.wfh.libs.beencode.BInteger;
import se.wfh.libs.beencode.BList;
import se.wfh.libs.beencode.BNode;
import se.wfh.libs.beencode.BString;
import se.wfh.libs.beencode.BencodeException;
import se.wfh.libs.beencode.NodeFactory;
import se.wfh.libs.common.utils.Config;

public class BDictTest {
	private static final int FUZZING_RUNS = 1000;

	public BDictTest() throws IOException {
		Config.load("src/test/resources/junit.conf");
	}

	@Test
	public void testClone() {
		BDict dict1 = BDict.empty();
		dict1.put(BString.of("Test"), BInteger.of(13));
		BDict dict2 = NodeFactory.clone(dict1);

		Assert.assertEquals(dict1, dict2);
		Assert.assertNotSame(dict1, dict2);

		dict1.put(BString.of("Test"), BInteger.of(13));
		Assert.assertEquals(dict1, dict2);

		dict2.put(BString.of("asd"), BString.of("xxx"));
		Assert.assertNotEquals(dict1, dict2);

		dict1.put(BString.of("asd"), BString.of("xxx"));
		Assert.assertEquals(dict1, dict2);
	}

	@Test
	public void testEquals() {
		BString a = BString.of("foo");
		BString b = BString.of("foo");
		BString c = BString.of("bar");

		BDict d1 = BDict.empty();
		d1.put(a, b);
		d1.put(b, c);

		BDict d2 = BDict.empty();
		d2.put(a, b);
		d2.put(b, c);

		BDict d3 = BDict.empty();
		d3.put(a, b);

		Assert.assertEquals(d1, d2);
		Assert.assertNotEquals(d2, d3);
	}

	@Test
	public void testHashCode() {
		BString a = BString.of("foo");
		BString b = BString.of("foo");
		BString c = BString.of("bar");

		BDict d1 = BDict.empty();
		d1.put(a, b);
		d1.put(b, c);

		BDict d2 = BDict.empty();
		d2.put(a, b);
		d2.put(b, c);

		BDict d3 = BDict.empty();
		d3.put(a, b);

		Assert.assertEquals(d1.hashCode(), d2.hashCode());
		Assert.assertNotEquals(d2.hashCode(), d3.hashCode());
	}

	@Test
	public void testJavaSerialization() throws IOException,
			ClassNotFoundException {
		BDict toWrite = BDict.empty();
		BDict hasRead = null;

		toWrite.put(BString.of("foo"), BInteger.of(13));
		toWrite.put(BString.of("bar"),
				BList.of(BInteger.of(42), BInteger.of(13), BInteger.of(-1)));

		byte[] written;
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			ObjectOutputStream oos = new ObjectOutputStream(bos);

			oos.writeObject(toWrite);
			written = bos.toByteArray();
		}

		try (ByteArrayInputStream bis = new ByteArrayInputStream(written)) {
			ObjectInputStream ois = new ObjectInputStream(bis);

			hasRead = (BDict) ois.readObject();
		}

		Assert.assertEquals(toWrite, hasRead);
	}

	@Test
	public void testToString() {
		BDict a = BDict.empty();

		BDict b = BDict.empty();
		b.put(BString.of("foo"), BString.of("bar"));

		Assert.assertEquals("{}", a.toString());
		Assert.assertEquals("{foo=bar}", b.toString());
	}

	@Test
	public void testFuzzingReadGarbage() throws IOException {
		byte[] data = new byte[100];

		for (int i = 0; i < FUZZING_RUNS; i++) {
			ThreadLocalRandom.current().nextBytes(data);
			try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
				try {
					BDict node = NodeFactory.decode(bis, BDict.class);
					System.out.println("Succeeded in creating a fuzzed node '" + node
							+ "' with data: " + Arrays.toString(data));
				} catch (IOException ioe) {
					// expected
				}
			}
		}
	}

	@Test
	public void testHugeDict() throws IOException {
		BDict dict = BDict.empty();
		for (int i = 0; i < FUZZING_RUNS; i++) {
			BString key = BString.of(String.valueOf(ThreadLocalRandom.current()
					.nextLong()));
			BString value = BString.of(String.valueOf(ThreadLocalRandom.current()
					.nextLong()));

			dict.put(key, value);
		}

		byte[] data = NodeFactory.encode(dict);
		BDict dict2 = NodeFactory.decode(data, BDict.class);

		Assert.assertEquals(dict, dict2);
	}

	@Test
	public void testStreamExtraData() throws IOException {
		BDict result = BDict.empty();
		result.put(BString.of("foo"), BInteger.of(13));
		result.put(BString.of("bar"), BString.of("test"));

		TestcaseHelper.testStreamSuccess("bdict_extra_data", result.getValue());
	}

	@Test
	public void testStream() throws IOException {
		BDict result = BDict.empty();
		result.put(BString.of("foo"), BInteger.of(13));
		result.put(BString.of("bar"), BString.of("test"));

		TestcaseHelper.testStreamSuccess("bdict_simple", result.getValue());
	}

	@Test(expected = BencodeException.class)
	public void testStreamInvalidEmpty() throws IOException {
		TestcaseHelper.testStreamFail("bdict_invalid_empty");
	}

	@Test(expected = BencodeException.class)
	public void testStreamNoValue() throws IOException {
		TestcaseHelper.testStreamFail("bdict_no_value");
	}

	@Test(expected = BencodeException.class)
	public void testStreamInvalidEnd() throws IOException {
		TestcaseHelper.testStreamFail("bdict_invalid_end");
	}

	@Test(expected = BencodeException.class)
	public void testStreamInvalidIntKey() throws IOException {
		TestcaseHelper.testStreamFail("bdict_invalid_int_key");
	}

	@Test
	public void testMapMethods() {
		BString a = BString.of("a");
		BString b = BString.of("b");
		BInteger _13 = BInteger.of(13);
		BInteger _42 = BInteger.of(42);
		BDict dict = BDict.empty();

		dict.put(a, _13);
		dict.put(b, _42);

		Assert.assertEquals(2, dict.size());
		Assert.assertFalse(dict.isEmpty());

		Assert.assertTrue(dict.containsKey(a));
		Assert.assertFalse(dict.containsKey(_13));
		Assert.assertFalse(dict.containsKey(BString.of("c")));
		Assert.assertFalse(dict.containsKey("a"));
		Assert.assertFalse(dict.containsKey(null));

		Assert.assertTrue(dict.containsValue(_13));
		Assert.assertFalse(dict.containsValue(a));
		Assert.assertFalse(dict.containsValue(BString.of("c")));
		Assert.assertFalse(dict.containsValue("a"));
		Assert.assertFalse(dict.containsValue(null));

		Assert.assertNull(dict.get(null));
		Assert.assertNull(dict.get("c"));
		Assert.assertEquals(_13, dict.get("a"));
		Assert.assertEquals(_13, dict.get(a));

		Assert.assertNull(dict.remove(null));
		Assert.assertNull(dict.remove("a"));
		Assert.assertEquals(_13, dict.remove(a));
		Assert.assertTrue(dict.remove(b, _42));

		Assert.assertTrue(dict.isEmpty());

		Map<BString, BNode<?>> myMap = new TreeMap<>();
		myMap.put(a, _13);
		myMap.put(b, _42);

		dict.putAll(myMap);
		Assert.assertEquals(myMap.size(), dict.size());

		Assert.assertEquals(a, dict.keySet().iterator().next());
		Assert.assertEquals(_13, dict.values().iterator().next());
		Assert.assertEquals(myMap.entrySet().iterator().next(), dict.entrySet()
				.iterator().next());

		dict.clear();
		Assert.assertTrue(dict.isEmpty());
	}
}
