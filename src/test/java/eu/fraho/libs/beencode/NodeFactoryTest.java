package eu.fraho.libs.beencode;

import eu.fraho.libs.beencode.helpers.MyNode;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

public class NodeFactoryTest {
    @Test(expected = BencodeException.class)
    public void testDecodeByteArrayError() {
        byte[] data = "i3".getBytes();
        BNode<?> node = NodeFactory.decode(data);
        Assert.fail("Invalid data was successfully parsed: " + node);
    }

    @Test(expected = BencodeException.class)
    public void testDecodeByteArrayWrongExpected() {
        byte[] data = "i3".getBytes();
        Optional<BString> node = NodeFactory.decode(data, BString.class);
        Assert.assertFalse("Invalid data was successfully parsed: " + node, node.isPresent());
    }

    @Test(expected = BencodeException.class)
    public void testDecodeByteArrayUnknownClass(){
        byte[] data = "i3".getBytes();
        Optional<MyNode> node = NodeFactory.decode(data, MyNode.class);
        Assert.assertFalse("Invalid data was successfully parsed: ", node.isPresent());
    }

    @Test(expected = BencodeException.class)
    public void testDecodeStreamWithBencodeException() throws IOException {
        try (InputStream bis = Mockito.mock(InputStream.class)) {
            Mockito.when(bis.read()).thenThrow(new BencodeException());
            NodeFactory.decode(bis);
            Assert.fail("Invalid data was successfully parsed");
        }
    }

    @Test(expected = IOException.class)
    public void testDecodeStreamWithIOException() throws IOException {
        try (InputStream bis = Mockito.mock(InputStream.class)) {
            Mockito.when(bis.read()).thenThrow(new IOException());
            NodeFactory.decode(bis);
            Assert.fail("Invalid data was successfully parsed");
        }
    }

    @Test(expected = BencodeException.class)
    public void testEncodeStreamWithBencodeExceptionOnWrite() throws IOException {
        try (OutputStream bos = Mockito.mock(OutputStream.class)) {
            Mockito.doThrow(new BencodeException()).when(bos).write(Mockito.anyInt());
            BString str = BString.of("foobar");
            NodeFactory.encode(str, bos);
            Assert.fail("Invalid data was successfully encoded: " + str);
        }
    }

    @Test(expected = BencodeException.class)
    public void testEncodeBencodeException() throws IOException {
        BNode<?> str = Mockito.mock(BNode.class);
        Mockito.doThrow(new BencodeException()).when(str).write(Mockito.any(OutputStream.class));

        byte[] data = NodeFactory.encode(str);
        Assert.fail("Successfully encoded node to: " + new String(data));
    }

    @Test
    public void testDecodeByteArray() {
        byte[] data = "i13e".getBytes();
        Assert.assertEquals(BInteger.of(13), NodeFactory.decode(data));
    }

    @Test
    public void testDecodeWithType() {
        byte[] data = "i13e".getBytes();
        Assert.assertEquals(Optional.empty(), NodeFactory.decode(data, BList.class));
        Assert.assertEquals(Optional.of(BInteger.of(13)), NodeFactory.decode(data, BInteger.class));
    }

    @Test
    public void testEncode() {
        byte[] expected = "i13e".getBytes();
        Assert.assertArrayEquals(expected, NodeFactory.encode(BInteger.of(13)));
    }

    @Test
    public void testDecodeStreamWithTypeOk() throws IOException {
        try (InputStream is = new ByteArrayInputStream("i0e".getBytes())) {
            Optional<BInteger> result = NodeFactory.decode(is, BInteger.class);
            Assert.assertTrue(result.isPresent());
        }
    }

    @Test
    public void testDecodeStreamWithTypeWrong() throws IOException {
        try (InputStream is = new ByteArrayInputStream("i0e".getBytes())) {
            Optional<BString> result = NodeFactory.decode(is, BString.class);
            Assert.assertFalse(result.isPresent());
        }
    }
}
