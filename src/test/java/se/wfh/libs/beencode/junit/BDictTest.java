package se.wfh.libs.beencode.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;

import se.wfh.libs.beencode.data.BDict;
import se.wfh.libs.beencode.data.BInteger;
import se.wfh.libs.beencode.data.BString;
import se.wfh.libs.common.utils.Config;
import se.wfh.libs.common.utils.R;

public class BDictTest {
	public BDictTest() throws IOException {
		Config.load("src/test/resources/junit.conf");
		R.load("english");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidPrefix() throws IllegalArgumentException,
			IOException {
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

			fail("This method should not complete!");
		}
	}

	@Test(expected = IOException.class)
	public void testInvalidStreamEnd() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bdict_invalid_end.dat"))) {
			fstream.skip(1);
			new BDict(fstream, (byte) 'd');

			fail("This method should not complete!");
		}
	}

	@Test(expected = IOException.class)
	public void testInvalidStreamIntegerKey() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bdict_invalid_int_key.dat"))) {
			fstream.skip(1);
			new BDict(fstream, (byte) 'd');

			fail("This method should not complete!");
		}
	}

	@Test
	public void testNewByStream() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bdict_simple.dat"))) {
			fstream.skip(1);
			BDict bi = new BDict(fstream, (byte) 'd');

			assertNotNull(bi.get("foo"));
			assertNotNull(bi.get("bar"));
		}
	}

	@Test
	public void testNewByStreamExtraData() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bdict_extra_data.dat"))) {
			fstream.skip(1);
			BDict bi = new BDict(fstream, (byte) 'd');

			assertNotNull(bi.get("foo"));
			assertNotNull(bi.get("bar"));
		}
	}

	@Test
	public void testNewByString() {
		BDict bi = new BDict();

		bi.put(new BString("foo"), new BInteger(13));

		assertEquals("{\n  \"foo\" => 13\n}", bi.toString());
	}

	@Test
	public void testNewEmptyString() {
		BDict bi = new BDict();

		assertEquals("{\n}", bi.toString());
	}
}
