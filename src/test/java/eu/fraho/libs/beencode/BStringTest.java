package eu.fraho.libs.beencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
        Assertions.assertEquals("我", str.toString(StandardCharsets.UTF_16BE));
    }

    @Test
    public void testEmptyString() {
        BString str = BString.of("");
        byte[] encoded = NodeFactory.encode(str);
        Assertions.assertEquals("0:", new String(encoded));
        Assertions.assertEquals(str, NodeFactory.decode(encoded));
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
        Assertions.assertEquals(toWrite, hasRead);
        Assertions.assertEquals(rawIn, rawOut);
    }

    @Test
    public void testStreamExtraData() throws IOException {
        testStreamSuccess("bstring_extra_data", getSampleB());
    }

    @Test
    public void testStreamInvalidEmpty() {
        Assertions.assertThrows(BencodeException.class, () -> {
            testStreamFail("bstring_invalid_empty");
        });
    }

    @Test
    public void testStreamLeadingZero() {
        Assertions.assertThrows(BencodeException.class, () -> {
            testStreamFail("bstring_length_leading_zero");
        });
    }

    @Test
    public void testStreamTooLarge() throws IOException {
        try (FileInputStream fstream = new FileInputStream(new File("src/test/resources/data/",
            "bstring_too_long.dat"))) {
            Assertions.assertThrows(BencodeException.class, () -> {
                BString.of(fstream, (byte) fstream.read(), 10);
            });
        }
    }

    @Test
    public void testStreamLengthZero() throws IOException {
        testStreamSuccess("bstring_length_zero", BString.of(""));
    }

    @Test
    public void testStreamInvalidLength() {
        Assertions.assertThrows(BencodeException.class, () -> {
            testStreamFail("bstring_invalid_length");
        });
    }

    @Test
    public void testStreamLengthChars() {
        Assertions.assertThrows(BencodeException.class, () -> {
            testStreamFail("bstring_length_chars");
        });
    }

    @Test
    public void testStreamTooLong() {
        Assertions.assertThrows(BencodeException.class, () -> {
            testStreamFail("bstring_too_long");
        });
    }

    @Test
    public void testStreamLengthNegative() {
        Assertions.assertThrows(BencodeException.class, () -> {
            testStreamFail("bstring_length_negative");
        });
    }

    @Test
    public void testStream() throws IOException {
        testStreamSuccess("bstring_simple", getSampleB());
    }

    @Test
    public void testToString() {
        BString a = BString.of("lorem");
        Assertions.assertEquals("lorem", a.toString());
    }

    @Test
    public void testImmutable() {
        byte[] data = "test".getBytes();
        BString a = BString.of(data);
        data[0] = 'X';
        BString b = BString.of(data);
        Assertions.assertNotEquals(a, b);
    }

    @Test
    public void testClone() {
        BString orig = BString.of("foobar");
        BString clone = orig.clone();

        Assertions.assertEquals(orig, clone);
        Assertions.assertNotSame(orig, clone);
    }
}
