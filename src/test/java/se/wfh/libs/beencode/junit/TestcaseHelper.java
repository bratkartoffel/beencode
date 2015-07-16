package se.wfh.libs.beencode.junit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;

import se.wfh.libs.beencode.BNode;
import se.wfh.libs.beencode.NodeFactory;
import se.wfh.libs.common.utils.Config;

public class TestcaseHelper {

	public static void testStreamFail(String testname) throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + testname + ".dat"))) {
			BNode<?> bi = NodeFactory.decode(fstream);

			Assert.fail("Should not succeed, data read: " + bi);
		}
	}

	public static void testStreamSuccess(String testname, Object expected)
			throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + testname + ".dat"))) {
			BNode<?> bi = NodeFactory.decode(fstream);

			Assert
					.assertEquals("Received unexpected result", expected, bi.getValue());
		}
	}

	public static void testStreamSuccess(String testname, byte[] expected)
			throws IOException {
		try (FileInputStream fstream = new FileInputStream(new File(
				Config.getString("junit.tests") + testname + ".dat"))) {
			BNode<?> bi = NodeFactory.decode(fstream);

			Assert.assertArrayEquals("Received unexpected result", expected,
					(byte[]) bi.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Throwable> void sneakyThrow(Throwable t) throws T {
		throw (T) t;
	}
}
