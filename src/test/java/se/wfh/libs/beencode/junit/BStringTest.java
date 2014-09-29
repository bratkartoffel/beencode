package se.wfh.libs.beencode.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import se.wfh.libs.beencode.data.BString;
import se.wfh.libs.common.utils.Config;
import se.wfh.libs.common.utils.R;

public class BStringTest {
	public BStringTest() throws IOException {
		Config.load("src/test/resources/junit.conf");
		R.load("english");
	}

	@Test(expected = IOException.class)
	public void testInvalidStreamEmpty() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bstring_invalid_empty.dat"))) {
			new BString(fstream, (byte) '4');

			fail("This method should not complete!");
		}
	}

	@Test(expected = IOException.class)
	public void testInvalidStreamLength() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bstring_invalid_length.dat"))) {
			fstream.skip(1);
			new BString(fstream, (byte) '4');

			fail("This method should not complete!");
		}
	}

	@Test
	public void testNewByStream() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bstring_simple.dat"))) {
			fstream.skip(1);
			BString bi = new BString(fstream, (byte) '4');

			if (!Arrays.equals("test".getBytes(), bi.getValue())) {
				fail("Result did not match. Got: " + new String(bi.getValue()));
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
				fail("Result did not match. Got: " + new String(bi.getValue()));
			}
		}
	}

	@Test
	public void testNewByString() {
		BString bi = new BString("test");

		assertEquals("\"test\"", bi.toString());
	}

	@Test
	public void testNewEmptyString() {
		BString bi = new BString("");

		assertEquals("\"\"", bi.toString());
	}
}
