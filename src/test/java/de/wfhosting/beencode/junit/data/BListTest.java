package de.wfhosting.beencode.junit.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import de.wfhosting.beencode.data.BInteger;
import de.wfhosting.beencode.data.BList;
import de.wfhosting.beencode.data.BString;
import de.wfhosting.common.Config;
import de.wfhosting.common.R;

public class BListTest {
	public BListTest() throws IOException {
		Config.load("src/test/resources/junit.conf");
		R.load("english");
	}

	@Test(expected = IOException.class)
	public void testInvalidStreamEmpty() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "blist_invalid_empty.dat"))) {
			new BList(fstream, (byte) 'l');

			fail("This method should not complete!");
		}
	}

	@Test(expected = IOException.class)
	public void testInvalidStreamEnd() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "blist_invalid_end.dat"))) {
			fstream.skip(1);
			new BList(fstream, (byte) 'l');

			fail("This method should not complete!");
		}
	}

	@Test
	public void testNewByStream() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "blist_simple.dat"))) {
			fstream.skip(1);
			BList bi = new BList(fstream, (byte) 'l');

			bi.getValue()
					.forEach(
							node -> {
								if (node instanceof BInteger) {
									assertEquals(Long.valueOf(13),
											((BInteger) node).getValue());
								}

								if (node instanceof BString) {
									if (!Arrays.equals("test".getBytes(),
											(byte[]) node.getValue())) {
										fail("Result did not match. Got: "
												+ new String((byte[]) node
														.getValue()));
									}
								}
							});
		}
	}

	@Test
	public void testNewByStreamExtraData() throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + "blist_extra_data.dat"))) {
			fstream.skip(1);
			BList bi = new BList(fstream, (byte) 'l');

			bi.getValue()
					.forEach(
							node -> {
								if (node instanceof BInteger) {
									assertEquals(Long.valueOf(13),
											((BInteger) node).getValue());
								}

								if (node instanceof BString) {
									if (!Arrays.equals("test".getBytes(),
											(byte[]) node.getValue())) {
										fail("Result did not match. Got: "
												+ new String((byte[]) node
														.getValue()));
									}
								}
							});
		}
	}

	@Test
	public void testNewByString() {
		BList bi = new BList();

		bi.getList().add(new BInteger(13));
		bi.getList().add(new BString("test"));

		assertEquals("[\n  13\n  \"test\"\n]", bi.toString());
	}

	@Test
	public void testNewEmptyString() {
		BList bi = new BList();

		assertEquals("[\n]", bi.toString());
	}
}
