package de.wfhosting.beencode.junit.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import de.wfhosting.beencode.data.BInteger;
import de.wfhosting.common.Config;
import de.wfhosting.common.R;

public class BIntegerTest {
	public BIntegerTest() throws IOException {
		Logger.getRootLogger().setLevel(Level.OFF);

		Config.load("src/test/resources/junit.conf");
		R.load("english");
	}

	@Test
	public void testNewByLong() {
		BInteger bi = new BInteger(1337);

		assertEquals("1337", bi.toString());
	}

	@Test
	public void testNewByNegativeLong() {
		BInteger bi = new BInteger(-1337);

		assertEquals("-1337", bi.toString());
	}

	@Test
	public void testNewNegativeOne() {
		BInteger bi = new BInteger(-1);

		assertEquals("-1", bi.toString());
	}

	@Test
	public void testNewByStream() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bint_simple.dat"))) {
			BInteger bi = new BInteger(fstream, (byte) fstream.read());

			assertEquals(Long.valueOf(1337), bi.getValue());
		}
	}

	@Test(expected = IOException.class)
	public void testNewByStreamNegativeZero() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bint_neg_0.dat"))) {
			new BInteger(fstream, (byte) fstream.read());

			fail("This method should not complete!");
		}
	}

	@Test(expected = IOException.class)
	public void testNewByStreamLeadingZero() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bint_leading_zero.dat"))) {
			new BInteger(fstream, (byte) fstream.read());

			fail("This method should not complete!");
		}
	}

	@Test
	public void testNewByStreamNegativeOne() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bint_neg_1.dat"))) {
			BInteger bi = new BInteger(fstream, (byte) fstream.read());

			assertEquals(Long.valueOf(-1), bi.getValue());
		}
	}

	@Test
	public void testNewByStreamExtraData() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bint_extra_data.dat"))) {
			BInteger bi = new BInteger(fstream, (byte) fstream.read());

			assertEquals(Long.valueOf(1337), bi.getValue());
		}
	}

	@Test
	public void testNewNegativeByStream() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bint_simple_neg.dat"))) {
			BInteger bi = new BInteger(fstream, (byte) fstream.read());

			assertEquals(Long.valueOf(-1337), bi.getValue());
		}
	}

	@Test
	public void testNewMaxByStream() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bint_max.dat"))) {
			BInteger bi = new BInteger(fstream, (byte) fstream.read());

			assertEquals(Long.valueOf(Long.MAX_VALUE), bi.getValue());
		}
	}

	@Test
	public void testNewMinByStream() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bint_min.dat"))) {
			BInteger bi = new BInteger(fstream, (byte) fstream.read());

			assertEquals(Long.valueOf(Long.MIN_VALUE), bi.getValue());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testInvalidStream() throws IOException {
		new BInteger(null, (byte) 'i');
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidPrefix() throws IllegalArgumentException, IOException {
		new BInteger(null, (byte) 's');
	}

	@Test(expected = IOException.class)
	public void testInvalidStreamData() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bint_invalid_data.dat"))) {
			new BInteger(fstream, (byte) fstream.read());

			fail("This method should not complete!");
		}
	}

	@Test(expected = IOException.class)
	public void testInvalidStreamEmpty() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bint_invalid_empty.dat"))) {
			new BInteger(fstream, (byte) 'i');

			fail("This method should not complete!");
		}
	}

	@Test(expected = IOException.class)
	public void testInvalidStreamOnlySuffix() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "bint_invalid_nodata.dat"))) {
			new BInteger(fstream, (byte) fstream.read());

			fail("This method should not complete!");
		}
	}
}
