package se.wfh.libs.beencode.junit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import se.wfh.libs.beencode.data.BDict;
import se.wfh.libs.beencode.data.BInteger;
import se.wfh.libs.beencode.data.BString;
import se.wfh.libs.common.utils.Config;

public class BDictTest {
	public BDictTest() throws IOException {
		Config.load("src/test/resources/junit.conf");
	}

	@Test
	public void testClone() {
		BDict dict1 = new BDict();
		BDict dict2 = new BDict();

		dict1.put(new BString("Test"), new BInteger(13));
		dict2.put(new BString("Test"), new BInteger(13));

		Assert.assertEquals(dict1, dict2);

		dict1.put(new BString("Test"), new BInteger(13));
		Assert.assertEquals(dict1, dict2);

		dict2.put(new BString("asd"), new BString("xxx"));
		Assert.assertNotEquals(dict1, dict2);

		dict1.put(new BString("asd"), new BString("xxx"));
		Assert.assertEquals(dict1, dict2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidPrefix() throws IllegalArgumentException, IOException {
		new BDict(null, (byte) 's');
	}

	@Test(expected = NullPointerException.class)
	public void testInvalidStream() throws IOException {
		new BDict(null, (byte) 'd');
	}

	@Test(expected = IOException.class)
	public void testInvalidStreamEmpty() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bdict_invalid_empty.dat"))) {
			new BDict(fstream, (byte) 'd');

			Assert.fail("This method should not complete!");
		}
	}

	@Test(expected = IOException.class)
	public void testInvalidStreamEnd() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bdict_invalid_end.dat"))) {
			fstream.skip(1);
			new BDict(fstream, (byte) 'd');

			Assert.fail("This method should not complete!");
		}
	}

	@Test(expected = IOException.class)
	public void testInvalidStreamIntegerKey() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bdict_invalid_int_key.dat"))) {
			fstream.skip(1);
			new BDict(fstream, (byte) 'd');

			Assert.fail("This method should not complete!");
		}
	}

	@Test
	public void testNewByStream() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bdict_simple.dat"))) {
			fstream.skip(1);
			BDict bi = new BDict(fstream, (byte) 'd');

			Assert.assertNotNull(bi.get("foo"));
			Assert.assertNotNull(bi.get("bar"));
		}
	}

	@Test
	public void testNewByStreamExtraData() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bdict_extra_data.dat"))) {
			fstream.skip(1);
			BDict bi = new BDict(fstream, (byte) 'd');

			Assert.assertNotNull(bi.get("foo"));
			Assert.assertNotNull(bi.get("bar"));
		}
	}

	@Test
	public void testNewByString() {
		BDict bi = new BDict();

		bi.put(new BString("foo"), new BInteger(13));

		Assert.assertEquals("{\n  \"foo\" => 13\n}", bi.toString());
	}

	@Test
	public void testNewEmptyString() {
		BDict bi = new BDict();

		Assert.assertEquals("{\n}", bi.toString());
	}
}
