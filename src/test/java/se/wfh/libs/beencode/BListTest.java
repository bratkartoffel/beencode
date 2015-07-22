package se.wfh.libs.beencode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import se.wfh.libs.beencode.BInteger;
import se.wfh.libs.beencode.BList;
import se.wfh.libs.beencode.BNode;
import se.wfh.libs.beencode.BString;
import se.wfh.libs.beencode.BencodeException;
import se.wfh.libs.beencode.NodeFactory;
import se.wfh.libs.common.utils.Config;

public class BListTest {
	private static final int FUZZING_RUNS = 1000;

	public BListTest() throws IOException {
		Config.load("src/test/resources/junit.conf");
	}

	@Test
	public void testClone() {
		BList list1 = BList.empty();
		list1.add(BInteger.of(13));
		BList list2 = NodeFactory.clone(list1);

		Assert.assertEquals(list1, list2);
		Assert.assertNotSame(list1, list2);

		list1.add(BString.of("asd"));
		Assert.assertNotEquals(list1, list2);

		list2.add(BString.of("asd"));
		Assert.assertEquals(list1, list2);
	}

	@Test
	public void testEquals() {
		BString a = BString.of("foo");
		BString b = BString.of("foo");
		BString c = BString.of("bar");

		BList l1 = BList.empty();
		l1.add(a);
		l1.add(b);

		BList l2 = BList.empty();
		l2.add(a);
		l2.add(b);

		BList l3 = BList.empty();
		l3.add(a);
		l3.add(c);

		Assert.assertEquals(l1, l2);
		Assert.assertNotEquals(l2, l3);
	}

	@Test
	public void testHashCode() {
		BString a = BString.of("foo");
		BString b = BString.of("foo");
		BString c = BString.of("bar");

		BList l1 = BList.empty();
		l1.add(a);
		l1.add(b);

		BList l2 = BList.empty();
		l2.add(a);
		l2.add(b);

		BList l3 = BList.empty();
		l3.add(a);
		l3.add(c);

		Assert.assertEquals(l1.hashCode(), l2.hashCode());
		Assert.assertNotEquals(l2.hashCode(), l3.hashCode());
	}

	@Test
	public void testToString() {
		BList a = BList.empty();
		BList b = BList.empty();

		b.add(BInteger.of(13));
		b.add(BString.of("test"));

		Assert.assertEquals("[]", a.toString());
		Assert.assertEquals("[13, test]", b.toString());
	}

	/*
	 * blist_invalid_empty.dat
	 * blist_invalid_end.dat
	 */
	@Test
	public void testStreamExtraData() throws IOException {
		BList result = BList.empty();
		result.add(BInteger.of(13));
		result.add(BString.of("test"));

		TestcaseHelper.testStreamSuccess("blist_extra_data", result.getValue());
	}

	@Test
	public void testStream() throws IOException {
		BList result = BList.empty();
		result.add(BInteger.of(13));
		result.add(BString.of("test"));

		TestcaseHelper.testStreamSuccess("blist_simple", result.getValue());
	}

	@Test(expected = BencodeException.class)
	public void testStreamInvalidEmpty() throws IOException {
		TestcaseHelper.testStreamFail("blist_invalid_empty");
	}

	@Test(expected = BencodeException.class)
	public void testStreamInvalidEnd() throws IOException {
		TestcaseHelper.testStreamFail("blist_invalid_end");
	}

	@Test
	public void testListMethods() {
		BString a = BString.of("a");
		BInteger _13 = BInteger.of(13);
		BInteger _42 = BInteger.of(42);
		BList list = BList.of(a, _13, _42);

		Assert.assertFalse(list.isEmpty());
		Assert.assertFalse(list.contains(null));
		Assert.assertFalse(list.contains("a"));
		Assert.assertTrue(list.contains(_42));
		Assert.assertEquals(3, list.size());
		Assert.assertEquals(a, list.get(0));
		Assert.assertEquals(a, list.iterator().next());
		Assert.assertEquals(a, list.listIterator().next());
		Assert.assertEquals(a, list.listIterator(0).next());
		Assert.assertEquals(BList.of(a).getValue(), list.subList(0, 1));

		Assert.assertArrayEquals(new BNode<?>[] { a, _13, _42 }, list.toArray());
		BNode<?>[] temp = new BNode<?>[3];
		list.toArray(temp);
		Assert.assertArrayEquals(temp, list.toArray());

		Assert.assertFalse(list.remove(null));
		Assert.assertFalse(list.remove("a"));
		Assert.assertEquals(a, list.remove(0));
		Assert.assertTrue(list.remove(_13));

		list.clear();
		Assert.assertTrue(list.isEmpty());
		Assert.assertFalse(list.containsAll(Arrays.asList(a, _13)));

		Assert.assertTrue(list.addAll(Arrays.asList(_13, _42)));
		list.add(0, a);
		Assert.assertEquals(3, list.size());

		List<BNode<?>> ex = Arrays.asList(BInteger.of(1337));
		Assert.assertTrue(list.addAll(0, ex));
		Assert.assertTrue(list.removeAll(ex));
		Assert.assertFalse(list.retainAll(Arrays.asList(temp)));

		BInteger _1337 = BInteger.of(1337);
		Assert.assertEquals(a, list.set(0, _1337));
		Assert.assertTrue(list.add(_1337));
		Assert.assertEquals(0, list.indexOf(_1337));
		Assert.assertEquals(-1, list.indexOf(null));
		Assert.assertEquals(-1, list.indexOf("abc"));
		Assert.assertEquals(-1, list.indexOf(BString.of("foobar")));
		Assert.assertEquals(3, list.lastIndexOf(_1337));
		Assert.assertEquals(-1, list.lastIndexOf(null));
		Assert.assertEquals(-1, list.lastIndexOf("abc"));
		Assert.assertEquals(-1, list.lastIndexOf(BString.of("foobar")));
	}

	@Test
	public void testEncode() throws BencodeException {
		String result = new String(NodeFactory.encode(BList.of(BInteger.of(42))));

		Assert.assertEquals("li42ee", result);
	}

	@Test
	public void testFuzzingReadGarbage() throws IOException {
		byte[] data = new byte[100];

		for (int i = 0; i < FUZZING_RUNS; i++) {
			ThreadLocalRandom.current().nextBytes(data);

			if (ThreadLocalRandom.current().nextBoolean()) {
				data[0] = 'l';

				while (data[1] == 'e') {
					data[1] = (byte) (ThreadLocalRandom.current().nextInt() & 0xFF);
				}
			}
			try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
				try {
					BList node = NodeFactory.decode(bis, BList.class);
					System.out.println("Succeeded in creating a fuzzed node '" + node
							+ "' with data: " + Arrays.toString(data));
				} catch (IOException ioe) {
					// expected
				}
			}
		}
	}
}
