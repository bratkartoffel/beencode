package eu.fraho.libs.beencode;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class BStringTest extends AbstractTest<BString> {
    @Override
    protected String getSampleAEncoded() {
        return "3:foo";
    }

    @Override
    protected String getSampleAToString() {
        return "foo";
    }

    @Override
    protected BString getSampleA() {
        return BString.of("foo");
    }

    @Override
    protected BString getSampleB() {
        return BString.of("test");
    }

    @Test
    public void testGetValueStringEncoding() {
        BString str = BString.of("我", StandardCharsets.UTF_16BE);
        Assert.assertEquals("我", str.toString(StandardCharsets.UTF_16BE));
    }

    @Test
    public void testEmptyString() {
        BString str = BString.of("");
        byte[] encoded = NodeFactory.encode(str);
        Assert.assertEquals("0:", new String(encoded));
        Assert.assertEquals(str, NodeFactory.decode(encoded));
    }

    @Test
    public void testEncoding() throws IOException {
        byte[] written;
        String rawIn = "我";
        String rawOut;
        BString toWrite;
        BString hasRead;

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            toWrite = BString.of(rawIn.getBytes(StandardCharsets.UTF_16BE));
            NodeFactory.encode(toWrite, bos);
            written = bos.toByteArray();
        }

        try (ByteArrayInputStream bis = new ByteArrayInputStream(written)) {
            hasRead = BString.of(bis);
        }

        rawOut = hasRead.toString(StandardCharsets.UTF_16BE);
        Assert.assertEquals(toWrite, hasRead);
        Assert.assertEquals(rawIn, rawOut);
    }

    @Test
    public void testStreamExtraData() throws IOException {
        testStreamSuccess("bstring_extra_data", getSampleB());
    }

    @Test(expected = BencodeException.class)
    public void testStreamInvalidEmpty() throws IOException {
        testStreamFail("bstring_invalid_empty");
    }

    @Test(expected = BencodeException.class)
    public void testStreamLeadingZero() throws IOException {
        testStreamFail("bstring_length_leading_zero");
    }

    @Test(expected = BencodeException.class)
    public void testStreamTooLarge() throws IOException {
        try (FileInputStream fstream = new FileInputStream(new File("src/test/resources/data/",
                "bstring_too_long.dat"))) {
            BString.of(fstream, (byte) fstream.read(), 10);
        }
    }

    @Test
    public void testStreamLengthZero() throws IOException {
        testStreamSuccess("bstring_length_zero", BString.of(""));
    }

    @Test(expected = BencodeException.class)
    public void testStreamInvalidLength() throws IOException {
        testStreamFail("bstring_invalid_length");
    }

    @Test(expected = BencodeException.class)
    public void testStreamLengthChars() throws IOException {
        testStreamFail("bstring_length_chars");
    }

    @Test(expected = BencodeException.class)
    public void testStreamTooLong() throws IOException {
        testStreamFail("bstring_too_long");
    }

    @Test(expected = BencodeException.class)
    public void testStreamLengthNegative() throws IOException {
        testStreamFail("bstring_length_negative");
    }

    @Test
    public void testStream() throws IOException {
        testStreamSuccess("bstring_simple", getSampleB());
    }

    @Test
    public void testToString() {
        BString a = BString.of("lorem");
        Assert.assertEquals("lorem", a.toString());
    }

    @Test
    public void testImmutable() {
        byte[] data = "test".getBytes();
        BString a = BString.of(data);
        data[0] = 'X';
        BString b = BString.of(data);
        Assert.assertNotEquals(a, b);
    }

    @Test
    public void testClone() {
        BString orig = BString.of("foobar");
        BString clone = orig.clone();

        Assert.assertEquals("Equals", orig, clone);
        Assert.assertNotSame("NotSame", orig, clone);
    }
}
