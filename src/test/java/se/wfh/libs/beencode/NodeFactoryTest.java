package se.wfh.libs.beencode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import se.wfh.libs.beencode.helpers.MyNode;

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
		ByteArrayInputStream bis = null;
		try {
			bis = new ByteArrayInputStream(data);
			BInteger result = BInteger.of(null);
			NodeFactory.decode(bis, result);

			Assert.fail("Invalid data was successfully parsed: " + result);
		} finally {
			Java6Helper.close(bis);
		}
	}

	@Test(expected = BencodeException.class)
	public void testDecodeStreamWithIOException() throws IOException {
		InputStream bis = Mockito.mock(InputStream.class);
		try {
			Mockito.when(bis.read()).thenThrow(new IOException());

			BString result = NodeFactory.decode(bis, BString.class);

			Assert.fail("Invalid data was successfully parsed: " + result);
		} finally {
			Java6Helper.close(bis);
		}
	}

	@Test(expected = BencodeException.class)
	public void testEncodeStreamWithIOExceptionOnWrite() throws IOException {
		OutputStream bos = Mockito.mock(OutputStream.class);
		try {
			Mockito.doThrow(new IOException()).when(bos).write(Mockito.anyInt());

			BString str = BString.of("foobar");
			NodeFactory.encode(str, bos);

			Assert.fail("Invalid data was successfully encoded: " + str);
		} finally {
			Java6Helper.close(bos);
		}
	}

	@Test
	public void testDecodeStreamToInstanceOk() throws IOException {
		byte[] data = "i3e".getBytes();
		ByteArrayInputStream bis = null;
		try {
			bis = new ByteArrayInputStream(data);
			BInteger result = BInteger.of(null);
			NodeFactory.decode(bis, result);

			Assert.assertEquals(Long.valueOf(3), result.getValue());
		} finally {
			Java6Helper.close(bis);
		}
	}

	@Test(expected = IOException.class)
	public void testEncodeIOException() throws IOException {
		BNode<?> str = Mockito.mock(BNode.class);
		Mockito.doThrow(new IOException()).when(str)
				.writeTo(Mockito.any(OutputStream.class));

		byte[] data = NodeFactory.encode(str);

		Assert.fail("Successfully encoded node to: " + new String(data));
	}
}
