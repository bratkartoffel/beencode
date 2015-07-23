package se.wfh.libs.beencode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class BStringTest {
	private static final int FUZZING_RUNS = 1000;

	public BStringTest() throws IOException {
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
		Charset charset = Charset.forName("UTF-16");
		BString str = BString.of("我".getBytes(charset));

		Assert.assertEquals("我", str.getString(charset));
	}

	@Test
	public void testSetValueStringCharset() {
		Charset charset = Charset.forName("UTF-16");
		BString str = BString.of("foo");
		str.setValue("我", charset);

		Assert.assertArrayEquals("我".getBytes(charset), str.getValue());
	}

	@Test
	public void testEncoding() throws IOException {
		byte[] written;
		String rawIn = "我";
		String rawOut;

		BString toWrite;
		BString hasRead;

		ByteArrayOutputStream bos = null;
		Charset charset = Charset.forName("UTF-16");
		try {
			bos = new ByteArrayOutputStream();
			toWrite = BString.of(rawIn.getBytes(charset));

			NodeFactory.encode(toWrite, bos);
			written = bos.toByteArray();
		} finally {
			Java6Helper.close(bos);
		}

		ByteArrayInputStream bis = null;
		try {
			bis = new ByteArrayInputStream(written);
			hasRead = NodeFactory.decode(bis, BString.class);
		} finally {
			Java6Helper.close(bis);
		}

		rawOut = hasRead.getString(charset);
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
		Charset charset = Charset.forName("UTF-8");

		BString result = BString.of(input.getBytes(charset));
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream(32);
			NodeFactory.encode(result, bos);

			Assert.assertEquals("String length did not match expected.", 18,
					bos.toByteArray().length);
			Assert.assertEquals("Result did not match.", "15:" + input, new String(
					bos.toByteArray(), charset));

			ByteArrayInputStream bis = null;
			try {
				bis = new ByteArrayInputStream(bos.toByteArray());
				BString reverse = NodeFactory.decode(bis, BString.class);
				Assert.assertEquals("Input and output differ", result, reverse);
			} finally {
				Java6Helper.close(bis);
			}
		} finally {
			Java6Helper.close(bos);
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
		Random rand = new Random();

		for (int i = 0; i < FUZZING_RUNS; i++) {
			rand.nextBytes(data);
			BString node = BString.of(data);

			Assert.assertArrayEquals(data, node.getValue());
		}
	}

	@Test
	public void testFuzzingRead() throws IOException {
		byte[] data = new byte[104];
		Random rand = new Random();
		System.arraycopy("100:".getBytes(), 0, data, 0, 4);
		byte[] random = new byte[100];

		for (int i = 0; i < FUZZING_RUNS; i++) {
			rand.nextBytes(random);
			System.arraycopy(random, 0, data, 4, random.length);
			ByteArrayInputStream bis = null;
			try {
				bis = new ByteArrayInputStream(data);
				BString node = NodeFactory.decode(bis, BString.class);
				Assert.assertArrayEquals(random, node.getValue());
			} finally {
				Java6Helper.close(bis);
			}
		}
	}

	@Test
	public void testFuzzingReadGarbage() throws IOException {
		byte[] data = new byte[100];
		Random rand = new Random();

		for (int i = 0; i < FUZZING_RUNS; i++) {
			rand.nextBytes(data);
			ByteArrayInputStream bis = null;
			try {
				bis = new ByteArrayInputStream(data);
				BString node = NodeFactory.decode(bis, BString.class);
				System.out.println("Succeeded in creating a fuzzed node '" + node
						+ "' with data: " + Arrays.toString(data));
			} catch (IOException ioe) {
				// expected
			} finally {
				Java6Helper.close(bis);
			}
		}
	}
}
