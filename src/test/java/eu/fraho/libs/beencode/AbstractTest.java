package eu.fraho.libs.beencode;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
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
        try (FileInputStream fstream = new FileInputStream(new File("src/test/resources/data/",
                testname + ".dat"))) {
            Assert.assertEquals("Received unexpected result", expected, NodeFactory.decode(fstream));
        }
    }

    @Test
    public final void testEquals() {
        T a = getSampleA();
        T b = getSampleA();
        T c = getSampleB();
        Assert.assertEquals(a, b);
        Assert.assertNotEquals(a, c);
    }

    @Test
    public final void testHashCode() {
        T a = getSampleA();
        T b = getSampleA();
        T c = getSampleB();
        Assert.assertEquals(a.hashCode(), b.hashCode());
        Assert.assertNotEquals(a.hashCode(), c.hashCode());
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

        Assert.assertEquals(toWrite, hasRead);
    }


    @Test
    public void testToString() {
        Assert.assertEquals(getSampleAToString(), getSampleA().toString());
    }

    @Test
    public void testOfStream() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        T expected = getSampleA();
        try (InputStream is = new ByteArrayInputStream(getSampleAEncoded().getBytes())) {
            Method of = expected.getClass().getDeclaredMethod("of", InputStream.class);
            Assert.assertEquals(expected, of.invoke(null, is));
        }
    }

    @Test
    public void testWrite() throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            getSampleA().write(os);
            Assert.assertEquals(getSampleAEncoded(), new String(os.toByteArray(), StandardCharsets.US_ASCII));
        }
    }
}
