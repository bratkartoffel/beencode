package se.wfh.libs.beencode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import se.wfh.libs.beencode.BString;
import se.wfh.libs.beencode.BencodeException;
import se.wfh.libs.beencode.NodeFactory;
import se.wfh.libs.common.utils.Config;

public class BStringTest {
	private static final int FUZZING_RUNS = 1000;

	public BStringTest() throws IOException {
		Config.load("src/test/resources/junit.conf");

		BString.setMaxReadSize(128);
	}

	@Test
	public void testClone() {
		BString a = BString.of("foobar");
		BString b = NodeFactory.clone(a);

		Assert.assertEquals(a, b);
		Assert.assertNotSame(a, b);
	}

	@Test
	public void testSetValueString() {
		BString str = BString.of("foo");
		str.setValue("bar");

		Assert.assertArrayEquals("bar".getBytes(), str.getValue());
	}

	@Test
	public void testSetValueByteArray() {
		BString str = BString.of("foo");
		str.setValue("bar".getBytes());

		Assert.assertArrayEquals("bar".getBytes(), str.getValue());
	}

	@Test
	public void testGetValueString() {
		BString str = BString.of("foo");

		Assert.assertEquals("foo", str.getString());
	}

	@Test
	public void testGetValueStringEncoding() {
		BString str = BString.of("我".getBytes(StandardCharsets.UTF_16));

		Assert.assertEquals("我", str.getString(StandardCharsets.UTF_16));
	}

	@Test
	public void testSetValueStringCharset() {
		BString str = BString.of("foo");
		str.setValue("我", StandardCharsets.UTF_16);

		Assert.assertArrayEquals("我".getBytes(StandardCharsets.UTF_16),
				str.getValue());
	}

	@Test
	public void testEncoding() throws IOException {
		byte[] written;
		String rawIn = "我";
		String rawOut;

		BString toWrite;
		BString hasRead;

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			toWrite = BString.of(rawIn.getBytes(StandardCharsets.UTF_16));

			NodeFactory.encode(toWrite, bos);
			written = bos.toByteArray();
		}

		try (ByteArrayInputStream bis = new ByteArrayInputStream(written)) {
			hasRead = NodeFactory.decode(bis, BString.class);
		}

		rawOut = hasRead.getString(StandardCharsets.UTF_16);
		Assert.assertEquals(toWrite, hasRead);
		Assert.assertEquals(rawIn, rawOut);
	}

	@Test
	public void testEquals() {
		BString a = BString.of("foo");
		BString b = BString.of("foo");
		BString c = BString.of("bar");

		Assert.assertEquals(a, b);
		Assert.assertNotEquals(b, c);
		Assert.assertFalse(a.equals(null));
		Assert.assertFalse(a.equals("abc"));
	}

	@Test
	public void testHashCode() {
		BString a = BString.of("foo");
		BString b = BString.of("foo");
		BString c = BString.of("bar");

		Assert.assertEquals(a.hashCode(), b.hashCode());
		Assert.assertNotEquals(b.hashCode(), c.hashCode());
	}

	@Test
	public void testString() {
		BString bi = BString.of("test");

		Assert.assertArrayEquals("test".getBytes(), bi.getValue());
	}

	@Test
	public void testByteArray() {
		BString bi = BString.of("test".getBytes());

		Assert.assertArrayEquals("test".getBytes(), bi.getValue());
	}

	@Test
	public void testEmptyString() {
		BString bi = BString.of("");

		Assert.assertTrue(bi.getValue().length == 0);
	}

	@Test
	public void testUtf8String() throws IOException {
		final String input = "Lörëm Ïpsüm";

		BString result = BString.of(input.getBytes(StandardCharsets.UTF_8));
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(32)) {
			NodeFactory.encode(result, bos);

			Assert.assertEquals("String length did not match expected.", 18,
					bos.toByteArray().length);
			Assert.assertEquals("Result did not match.", "15:" + input, new String(
					bos.toByteArray(), StandardCharsets.UTF_8));

			try (ByteArrayInputStream bis = new ByteArrayInputStream(
					bos.toByteArray())) {
				BString reverse = NodeFactory.decode(bis, BString.class);
				Assert.assertEquals("Input and output differ", result, reverse);
			}
		}
	}

	@Test
	public void testStreamExtraData() throws IOException {
		TestcaseHelper.testStreamSuccess("bstring_extra_data", "test".getBytes());
	}

	@Test(expected = BencodeException.class)
	public void testStreamInvalidEmpty() throws IOException {
		TestcaseHelper.testStreamFail("bstring_invalid_empty");
	}

	@Test(expected = BencodeException.class)
	public void testStreamLengthZero() throws IOException {
		TestcaseHelper.testStreamFail("bstring_length_zero");
	}

	@Test(expected = BencodeException.class)
	public void testStreamInvalidLength() throws IOException {
		TestcaseHelper.testStreamFail("bstring_invalid_length");
	}

	@Test(expected = BencodeException.class)
	public void testStreamLengthChars() throws IOException {
		TestcaseHelper.testStreamFail("bstring_length_chars");
	}

	@Test(expected = BencodeException.class)
	public void testStreamTooLong() throws IOException {
		TestcaseHelper.testStreamFail("bstring_too_long");
	}

	@Test(expected = BencodeException.class)
	public void testStreamLengthNegative() throws IOException {
		TestcaseHelper.testStreamFail("bstring_length_negative");
	}

	@Test
	public void testStream() throws IOException {
		TestcaseHelper.testStreamSuccess("bstring_simple", "test".getBytes());
	}

	@Test
	public void testToString() {
		BString a = BString.of("lorem");
		Assert.assertEquals("lorem", a.toString());
	}

	@Test
	public void testFuzzingCreate() throws IOException {
		byte[] data = new byte[100];

		for (int i = 0; i < FUZZING_RUNS; i++) {
			ThreadLocalRandom.current().nextBytes(data);
			BString node = BString.of(data);

			Assert.assertArrayEquals(data, node.getValue());
		}
	}

	@Test
	public void testFuzzingRead() throws IOException {
		byte[] data = new byte[104];
		System.arraycopy("100:".getBytes(), 0, data, 0, 4);
		byte[] random = new byte[100];

		for (int i = 0; i < FUZZING_RUNS; i++) {
			ThreadLocalRandom.current().nextBytes(random);
			System.arraycopy(random, 0, data, 4, random.length);
			try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
				BString node = NodeFactory.decode(bis, BString.class);
				Assert.assertArrayEquals(random, node.getValue());
			}
		}
	}

	@Test
	public void testFuzzingReadGarbage() throws IOException {
		byte[] data = new byte[100];

		for (int i = 0; i < FUZZING_RUNS; i++) {
			ThreadLocalRandom.current().nextBytes(data);
			try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
				try {
					BString node = NodeFactory.decode(bis, BString.class);
					System.out.println("Succeeded in creating a fuzzed node '" + node
							+ "' with data: " + Arrays.toString(data));
				} catch (IOException ioe) {
					// expected
				}
			}
		}
	}
}
