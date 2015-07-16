package se.wfh.libs.beencode.junit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import se.wfh.libs.beencode.BInteger;
import se.wfh.libs.beencode.BNode;
import se.wfh.libs.beencode.BString;
import se.wfh.libs.beencode.BencodeException;
import se.wfh.libs.beencode.NodeFactory;
import se.wfh.libs.beencode.junit.helpers.MyNode;

public class NodeFactoryTest {
	@Test(expected = BencodeException.class)
	public void testDecodeByteArrayError() throws BencodeException {
		byte[] data = "i3".getBytes();
		BNode<?> node = NodeFactory.decode(data);

		Assert.fail("Invalid data was successfully parsed: " + node);
	}

	@Test(expected = BencodeException.class)
	public void testDecodeByteArrayWrongExpected() throws BencodeException {
		byte[] data = "i3".getBytes();
		BString node = NodeFactory.decode(data, BString.class);

		Assert.fail("Invalid data was successfully parsed: " + node);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDecodeByteArrayUnknownClass() throws BencodeException {
		byte[] data = "i3".getBytes();
		MyNode node = NodeFactory.decode(data, MyNode.class);

		Assert.fail("Invalid data was successfully parsed: " + node);
	}

	@Test(expected = BencodeException.class)
	public void testDecodeStreamToInstanceError() throws IOException {
		byte[] data = "i3".getBytes();
		try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
			BInteger result = BInteger.of(null);
			NodeFactory.decode(bis, result);

			Assert.fail("Invalid data was successfully parsed: " + result);
		}
	}

	@Test(expected = BencodeException.class)
	public void testDecodeStreamWithIOException() throws IOException {
		try (InputStream bis = Mockito.mock(InputStream.class)) {
			Mockito.when(bis.read()).thenThrow(new IOException());

			BString result = NodeFactory.decode(bis, BString.class);

			Assert.fail("Invalid data was successfully parsed: " + result);
		}
	}

	@Test(expected = BencodeException.class)
	public void testEncodeStreamWithIOExceptionOnWrite() throws IOException {
		try (OutputStream bos = Mockito.mock(OutputStream.class)) {
			Mockito.doThrow(new IOException()).when(bos).write(Mockito.anyInt());

			BString str = BString.of("foobar");
			NodeFactory.encode(str, bos);

			Assert.fail("Invalid data was successfully encoded: " + str);
		}
	}

	@Test
	public void testDecodeStreamToInstanceOk() throws IOException {
		byte[] data = "i3e".getBytes();
		try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
			BInteger result = BInteger.of(null);
			NodeFactory.decode(bis, result);

			Assert.assertEquals(Long.valueOf(3), result.getValue());
		}
	}
}
