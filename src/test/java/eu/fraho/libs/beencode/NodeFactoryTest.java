package eu.fraho.libs.beencode;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Optional;

public class NodeFactoryTest {
    @Test(expected = BencodeException.class)
    public void testDecodeByteArrayError() {
        NodeFactory.decode("i3".getBytes());
    }

    @Test(expected = BencodeException.class)
    public void testDecodeByteArrayWrongExpected() {
        NodeFactory.decode("i3e".getBytes(), BString.class);
    }

    @Test(expected = BencodeException.class)
    public void testDecodeStreamWithBencodeException() throws IOException {
        InputStream bis = Mockito.mock(InputStream.class);
        Mockito.when(bis.read()).thenThrow(new BencodeException());
        NodeFactory.decode(bis);
    }

    @Test(expected = IOException.class)
    public void testDecodeStreamWithIOException() throws IOException {
        InputStream bis = Mockito.mock(InputStream.class);
        Mockito.when(bis.read()).thenThrow(new IOException());
        NodeFactory.decode(bis);
    }

    @Test(expected = BencodeException.class)
    public void testEncodeStreamWithBencodeExceptionOnWrite() throws IOException {
        OutputStream bos = Mockito.mock(OutputStream.class);
        Mockito.doThrow(new BencodeException()).when(bos).write(Mockito.anyInt());
        NodeFactory.encode(BString.of("foobar"), bos);
    }

    @Test(expected = BencodeException.class)
    public void testEncodeBencodeException() throws IOException {
        BNodeBase<?> str = Mockito.mock(BNodeBase.class);
        Mockito.doThrow(new BencodeException()).when(str).write(Mockito.any(OutputStream.class));
        NodeFactory.encode(str);
    }

    @Test
    public void testDecodeByteArray() {
        Assert.assertEquals(BInteger.of(13), NodeFactory.decode("i13e".getBytes()));
    }

    @Test(expected = BencodeException.class)
    public void testDecodeWithWrongType() {
        NodeFactory.decode("i13e".getBytes(), BList.class);
    }

    @Test
    public void testDecodeWithType() {
        Assert.assertEquals(Optional.of(BInteger.of(13)), NodeFactory.decode("i13e".getBytes(), BInteger.class));
    }

    @Test(expected = BencodeException.class)
    public void testDecodeWithTypeStreamWrongType() throws IOException {
        try (InputStream stream = new ByteArrayInputStream("i13e".getBytes())) {
            NodeFactory.decode(stream, BList.class);
        }
    }

    @Test
    public void testDecodeWithTypeStream() throws IOException {
        try (InputStream stream = new ByteArrayInputStream("i13e".getBytes())) {
            Assert.assertEquals(Optional.of(BInteger.of(13)), NodeFactory.decode(stream, BInteger.class));
        }
    }

    @Test
    public void testEncode() {
        Assert.assertArrayEquals("i13e".getBytes(), NodeFactory.encode(BInteger.of(13)));
    }

    @Test
    public void testDecodeStreamWithTypeOk() throws IOException {
        try (InputStream is = new ByteArrayInputStream("i0e".getBytes())) {
            Optional<BInteger> result = NodeFactory.decode(is, BInteger.class);
            Assert.assertTrue(result.isPresent());
        }
    }

    @Test(expected = BencodeException.class)
    public void testDecodeStreamWithTypeWrong() throws IOException {
        try (InputStream is = new ByteArrayInputStream("i0e".getBytes())) {
            NodeFactory.decode(is, BString.class);
        }
    }

    @Test
    public void testInstance() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?>[] constructors = NodeFactory.class.getDeclaredConstructors();
        Assert.assertEquals("Only one constructor defined", 1, constructors.length);
        Assert.assertEquals("Constructor is private", Modifier.PRIVATE, constructors[0].getModifiers() & Modifier.PRIVATE);

        constructors[0].setAccessible(true);
        constructors[0].newInstance();
    }
}
