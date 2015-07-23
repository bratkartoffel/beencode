package se.wfh.libs.beencode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;

public class TestcaseHelper {

	public static void testStreamFail(String testname) throws IOException {
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(new File("src/test/resources/data/",
					testname + ".dat"));
			BNode<?> bi = NodeFactory.decode(fstream);

			Assert.fail("Should not succeed, data read: " + bi);
		} finally {
			Java6Helper.close(fstream);
		}
	}

	public static void testStreamSuccess(String testname, Object expected)
			throws IOException {
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(new File("src/test/resources/data/",
					testname + ".dat"));
			BNode<?> bi = NodeFactory.decode(fstream);

			Assert
					.assertEquals("Received unexpected result", expected, bi.getValue());
		} finally {
			Java6Helper.close(fstream);
		}
	}

	public static void testStreamSuccess(String testname, byte[] expected)
			throws IOException {
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(new File("src/test/resources/data/",
					testname + ".dat"));
			BNode<?> bi = NodeFactory.decode(fstream);

			Assert.assertArrayEquals("Received unexpected result", expected,
					(byte[]) bi.getValue());
		} finally {
			Java6Helper.close(fstream);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Throwable> void sneakyThrow(Throwable t) throws T {
		throw (T) t;
	}
}
