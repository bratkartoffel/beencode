package se.wfh.libs.beencode.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import se.wfh.libs.beencode.data.BNode;
import se.wfh.libs.beencode.data.BString;
import se.wfh.libs.common.utils.Config;

@SuppressWarnings("deprecation")
public class BStringTest {
	public BStringTest() throws IOException {
		Config.load("src/test/resources/junit.conf");
	}

	@Test(expected = IOException.class)
	public void testInvalidStreamEmpty() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bstring_invalid_empty.dat"))) {
			new BString(fstream, (byte) '4');

			Assert.fail("This method should not complete!");
		}
	}

	@Test(expected = IOException.class)
	public void testInvalidStreamLength() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bstring_invalid_length.dat"))) {
			fstream.skip(1);
			new BString(fstream, (byte) '4');

			Assert.fail("This method should not complete!");
		}
	}

	@Test
	public void testNewByStream() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bstring_simple.dat"))) {
			fstream.skip(1);
			BString bi = new BString(fstream, (byte) '4');

			if (!Arrays.equals("test".getBytes(), bi.getValue())) {
				Assert.fail("Result did not match. Got: " + new String(bi.getValue()));
			}
		}
	}

	@Test
	public void testNewByStreamExtraData() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bstring_extra_data.dat"))) {
			fstream.skip(1);
			BString bi = new BString(fstream, (byte) '4');

			if (!Arrays.equals("test".getBytes(), bi.getValue())) {
				Assert.fail("Result did not match. Got: " + new String(bi.getValue()));
			}
		}
	}

	@Test
	public void testNewByString() {
		BString bi = new BString("test");

		Assert.assertEquals("\"test\"", bi.toString());
	}

	@Test
	public void testNewEmptyString() {
		BString bi = new BString("");

		Assert.assertEquals("\"\"", bi.toString());
	}

	@Test
	public void testNewUtf8String() throws IOException {
		final String input = "Lörëm Ïpsüm";

		BString result = new BString(input.getBytes(StandardCharsets.UTF_8));
		ByteArrayOutputStream bos = new ByteArrayOutputStream(32);
		result.write(bos);

		Assert.assertEquals("String length did not match expected.", 18,
				bos.toByteArray().length);
		Assert.assertEquals("Result did not match.", "15:" + input,
				new String(bos.toByteArray(), StandardCharsets.UTF_8));

		BString reverse = BNode.of(new ByteArrayInputStream(bos.toByteArray()),
				BString.class);
		Assert.assertEquals("Input and output differ", result, reverse);
	}

	@Test
	public void testEquals() {
		BString a = new BString("foo");
		BString b = new BString("foo");
		BString c = new BString("bar");

		Assert.assertEquals(a, b);
		Assert.assertNotEquals(b, c);
	}

	@Test
	public void testHashCode() {
		BString a = new BString("foo");
		BString b = new BString("foo");
		BString c = new BString("bar");

		Assert.assertEquals(a.hashCode(), b.hashCode());
		Assert.assertNotEquals(b.hashCode(), c.hashCode());
	}

	@Test
	public void testEncoding() throws IOException {
		byte[] written;
		String rawIn = "我";
		String rawOut;

		BString toWrite;
		BString hasRead;

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			toWrite = new BString(rawIn, StandardCharsets.UTF_16);

			bos.write(toWrite.getEncoded());
			written = bos.toByteArray();
		}

		try (ByteArrayInputStream bis = new ByteArrayInputStream(written)) {
			hasRead = BNode.of(bis, BString.class);
		}

		rawOut = hasRead.asString(StandardCharsets.UTF_16);
		Assert.assertEquals(toWrite, hasRead);
		Assert.assertEquals(rawIn, rawOut);
	}
}
