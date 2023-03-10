package eu.fraho.libs.beencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
    @Test
    public void testDecodeByteArrayError() {
        Assertions.assertThrows(BencodeException.class, () -> {
            NodeFactory.decode("i3".getBytes());
        });
    }

    @Test
    public void testDecodeByteArrayWrongExpected() {
        Assertions.assertThrows(BencodeException.class, () -> {
            NodeFactory.decode("i3e".getBytes(), BString.class);
        });
    }

    @Test
    public void testDecodeStreamWithBencodeException() throws IOException {
        try (InputStream bis = Mockito.mock(InputStream.class)) {
            Mockito.when(bis.read()).thenThrow(new BencodeException());
            Assertions.assertThrows(BencodeException.class, () -> {
                NodeFactory.decode(bis);
            });
        }
    }

    @Test
    public void testDecodeStreamWithIOException() throws IOException {
        try (InputStream bis = Mockito.mock(InputStream.class)) {
            Mockito.when(bis.read()).thenThrow(new IOException());
            Assertions.assertThrows(IOException.class, () -> {
                NodeFactory.decode(bis);
            });
        }
    }

    @Test
    public void testEncodeStreamWithBencodeExceptionOnWrite() throws IOException {
        OutputStream bos = Mockito.mock(OutputStream.class);
        Mockito.doThrow(new BencodeException()).when(bos).write(Mockito.anyInt());
        Assertions.assertThrows(BencodeException.class, () -> {
            NodeFactory.encode(BString.of("foobar"), bos);
        });
    }

    @Test
    public void testEncodeBencodeException() throws IOException {
        BNodeBase<?> str = Mockito.mock(BNodeBase.class);
        Mockito.doThrow(new BencodeException()).when(str).write(Mockito.any(OutputStream.class));
        Assertions.assertThrows(BencodeException.class, () -> {
            NodeFactory.encode(str);
        });
    }

    @Test
    public void testDecodeByteArray() {
        Assertions.assertEquals(BInteger.of(13), NodeFactory.decode("i13e".getBytes()));
    }

    @Test
    public void testDecodeWithWrongType() {
        Assertions.assertThrows(BencodeException.class, () -> {
            NodeFactory.decode("i13e".getBytes(), BList.class);
        });
    }

    @Test
    public void testDecodeWithType() {
        Assertions.assertEquals(Optional.of(BInteger.of(13)), NodeFactory.decode("i13e".getBytes(), BInteger.class));
    }

    @Test
    public void testDecodeWithTypeStreamWrongType() throws IOException {
        try (InputStream stream = new ByteArrayInputStream("i13e".getBytes())) {
            Assertions.assertThrows(BencodeException.class, () -> {
                NodeFactory.decode(stream, BList.class);
            });
        }
    }

    @Test
    public void testDecodeWithTypeStream() throws IOException {
        try (InputStream stream = new ByteArrayInputStream("i13e".getBytes())) {
            Assertions.assertEquals(Optional.of(BInteger.of(13)), NodeFactory.decode(stream, BInteger.class));
        }
    }

    @Test
    public void testEncode() {
        Assertions.assertArrayEquals("i13e".getBytes(), NodeFactory.encode(BInteger.of(13)));
    }

    @Test
    public void testDecodeStreamWithTypeOk() throws IOException {
        try (InputStream is = new ByteArrayInputStream("i0e".getBytes())) {
            Optional<BInteger> result = NodeFactory.decode(is, BInteger.class);
            Assertions.assertTrue(result.isPresent());
        }
    }

    @Test
    public void testDecodeStreamWithTypeWrong() throws IOException {
        try (InputStream is = new ByteArrayInputStream("i0e".getBytes())) {
            Assertions.assertThrows(BencodeException.class, () -> {
                NodeFactory.decode(is, BString.class);
            });
        }
    }

    @Test
    public void testInstance() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?>[] constructors = NodeFactory.class.getDeclaredConstructors();
        Assertions.assertEquals(1, constructors.length);
        Assertions.assertEquals(Modifier.PRIVATE, constructors[0].getModifiers() & Modifier.PRIVATE);

        constructors[0].setAccessible(true);
        constructors[0].newInstance();
    }
}
