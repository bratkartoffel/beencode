package eu.fraho.libs.beencode;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class BDictTest extends AbstractTest<BDict> {
    protected String getSampleAEncoded() {
        return "d3:fooi13e3:bar4:teste";
    }

    protected String getSampleAToString() {
        return "{foo=13, bar=test}";
    }

    @Override
    protected BDict getSampleA() {
        return BDict.of(
                BString.of("foo"), BInteger.of(13),
                BString.of("bar"), BString.of("test")
        );
    }

    @Override
    protected BDict getSampleB() {
        return BDict.of(
                BString.of("Foo"), BInteger.of(13),
                BString.of("baz"), BInteger.of(42)
        );
    }

    @Test(expected = BencodeException.class)
    public void testOfBIntegerKey() {
        BDict.of(BInteger.of(13), BInteger.of(2));
    }

    @Test(expected = BencodeException.class)
    public void testOfNullKey() {
        BDict.of(null, BInteger.of(2));
    }

    @Test(expected = BencodeException.class)
    public void testOfNullValue() {
        BDict.of(BString.of("foo"), null);
    }

    @Test(expected = BencodeException.class)
    public void testOfMissingValue() {
        BDict.of(BString.of("foo"), BInteger.of(42), BString.of("bar"));
    }

    @Test
    public void testStreamExtraData() throws IOException {
        testStreamSuccess("bdict_extra_data", getSampleA());
    }

    @Test
    public void testStream() throws IOException {
        testStreamSuccess("bdict_simple", getSampleA());
    }

    @Test(expected = BencodeException.class)
    public void testStreamInvalidEmpty() throws IOException {
        testStreamFail("bdict_invalid_empty");
    }

    @Test(expected = BencodeException.class)
    public void testStreamNoValue() throws IOException {
        testStreamFail("bdict_no_value");
    }

    @Test(expected = BencodeException.class)
    public void testStreamInvalidEnd() throws IOException {
        testStreamFail("bdict_invalid_end");
    }

    @Test(expected = BencodeException.class)
    public void testStreamInvalidIntKey() throws IOException {
        testStreamFail("bdict_invalid_int_key");
    }

    @Test
    public void testDelegateMethods() {
        BDict testee = getSampleB();
        BString key = BString.of("Foo");
        BInteger value = BInteger.of(13);

        Assert.assertEquals(testee.size(), testee.getValue().size());
        Assert.assertEquals(testee.isEmpty(), testee.getValue().isEmpty());
        Assert.assertEquals(testee.containsKey(key), testee.getValue().containsKey(key));
        Assert.assertEquals(testee.containsValue(key), testee.getValue().containsValue(key));
        Assert.assertEquals(testee.get(key), Optional.of(value));
        Assert.assertEquals(testee.get(key.toString()), Optional.of(value));
        Assert.assertEquals(testee.get("xxxxx"), Optional.empty());
        Assert.assertEquals(testee.keySet(), testee.getValue().keySet());
        Assert.assertEquals(testee.values(), testee.getValue().values());
        Assert.assertEquals(testee.entrySet(), testee.getValue().entrySet());
    }

    @Test
    public void testRemove() {
        BDict testee = getSampleB();
        Assert.assertEquals(1, testee.remove("Foo").size());
        Assert.assertEquals(1, testee.remove("baz").size());
        Assert.assertEquals(1, testee.remove(BString.of("baz")).size());
        Assert.assertSame(testee, testee.remove(BString.of("xxx")));
        Assert.assertEquals(2, testee.size());
    }

    @Test
    public void tesPut() {
        BDict testee = getSampleB();
        Assert.assertEquals(3, testee.put(BString.of("xxx"), BInteger.of(1)).size());
        Assert.assertEquals(2, testee.size());
    }

    @Test
    public void testJoin() {
        BDict a = getSampleA();
        BDict b = getSampleB();
        Assert.assertEquals(4, a.join(b).size());
        Assert.assertEquals(2, a.size());
    }

    @Test
    public void testImmutable() {
        Map<BString, BNode<?>> data = new TreeMap<>();
        data.put(BString.of("foo"), BInteger.of(13));
        BDict a = BDict.of(data);
        data.put(BString.of("bar"), BInteger.of(42));
        BDict b = BDict.of(data);
        Assert.assertNotEquals(a, b);
    }

    @Test(expected = BencodeException.class)
    public void testOfInvalidPrefix() throws IOException {
        try (InputStream is = new ByteArrayInputStream(new byte[0])) {
            BDict.of(is, (byte) 'x');
        }
    }

    @Test
    public void testClone() {
        BDict orig = BDict.of(BString.of("foo"), BString.of("bar"));
        BDict clone = orig.clone();

        Assert.assertEquals("Equals", orig, clone);
        Assert.assertNotSame("NotSame", orig, clone);
    }
}
