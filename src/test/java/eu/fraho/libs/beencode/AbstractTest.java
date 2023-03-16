package eu.fraho.libs.beencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public abstract class AbstractTest<T extends BNode<?>> {
    protected abstract T getSampleA();

    protected abstract T getSampleB();

    protected abstract String getSampleAEncoded();

    protected abstract String getSampleAToString();

    protected void testStreamFail(String testname) throws IOException {
        try (FileInputStream fstream = new FileInputStream(new File("src/test/resources/data/",
            testname + ".dat"))) {
            NodeFactory.decode(fstream);
        }
    }

    protected void testStreamSuccess(String testname, BNode<?> expected)
        throws IOException {
        try (FileInputStream fstream = new FileInputStream(new File("src/test/resources/data/", testname + ".dat"))) {
            Assertions.assertEquals(expected, NodeFactory.decode(fstream), "Received unexpected result");
        }
    }

    @Test
    public final void testEquals() {
        T a = getSampleA();
        T b = getSampleA();
        T c = getSampleB();
        Assertions.assertEquals(a, b);
        Assertions.assertNotEquals(a, c);

        BString x = BString.of("x123");
        BInteger y = BInteger.of(987_654_321);
        Assertions.assertNotEquals(a, x);
        Assertions.assertNotEquals(a, y);
        //noinspection ConstantValue,SimplifiableAssertion
        Assertions.assertFalse(a.equals(null));
    }

    @Test
    public final void testHashCode() {
        T a = getSampleA();
        T b = getSampleA();
        T c = getSampleB();
        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(a.hashCode(), c.hashCode());
    }

    @Test
    public final void testJavaSerialization() throws IOException, ClassNotFoundException {
        BNode<?> toWrite = getSampleA();
        BNode<?> hasRead;
        byte[] written;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(toWrite);
            }
            written = bos.toByteArray();
        }

        try (ByteArrayInputStream bis = new ByteArrayInputStream(written)) {
            ObjectInputStream ois = new ObjectInputStream(bis);
            hasRead = (BNodeBase<?>) ois.readObject();
        }

        Assertions.assertEquals(toWrite, hasRead, "Object written and read match");
    }


    @Test
    public void testToString() {
        Assertions.assertEquals(getSampleAToString(), getSampleA().toString());
    }

    @Test
    public void testOfStream() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        T expected = getSampleA();
        try (InputStream is = new ByteArrayInputStream(getSampleAEncoded().getBytes())) {
            Method of = expected.getClass().getDeclaredMethod("of", InputStream.class);
            Assertions.assertEquals(expected, of.invoke(null, is));
        }
    }

    @Test
    public void testWrite() throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            getSampleA().write(os);
            Assertions.assertEquals(getSampleAEncoded(), new String(os.toByteArray(), StandardCharsets.US_ASCII));
        }
    }
}
