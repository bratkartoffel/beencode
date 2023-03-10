package eu.fraho.libs.beencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

    @Test
    public void testOfBIntegerKey() {
        Assertions.assertThrows(BencodeException.class, () -> {
            BDict.of(BInteger.of(13), BInteger.of(2));
        });
    }

    @Test
    public void testOfNullKey() {
        Assertions.assertThrows(BencodeException.class, () -> {
            BDict.of(null, BInteger.of(2));
        });
    }

    @Test
    public void testOfNullValue() {
        Assertions.assertThrows(BencodeException.class, () -> {
            BDict.of(BString.of("foo"), null);
        });
    }

    @Test
    public void testOfMissingValue() {
        Assertions.assertThrows(BencodeException.class, () -> {
            BDict.of(BString.of("foo"), BInteger.of(42), BString.of("bar"));
        });
    }

    @Test
    public void testStreamExtraData() throws IOException {
        testStreamSuccess("bdict_extra_data", getSampleA());
    }

    @Test
    public void testStream() throws IOException {
        testStreamSuccess("bdict_simple", getSampleA());
    }

    @Test
    public void testStreamInvalidEmpty() {
        Assertions.assertThrows(BencodeException.class, () -> {
            testStreamFail("bdict_invalid_empty");
        });
    }

    @Test
    public void testStreamNoValue() {
        Assertions.assertThrows(BencodeException.class, () -> {
            testStreamFail("bdict_no_value");
        });
    }

    @Test
    public void testStreamInvalidEnd() {
        Assertions.assertThrows(BencodeException.class, () -> {
            testStreamFail("bdict_invalid_end");
        });
    }

    @Test
    public void testStreamInvalidIntKey() {
        Assertions.assertThrows(BencodeException.class, () -> {
            testStreamFail("bdict_invalid_int_key");
        });
    }

    @Test
    public void testDelegateMethods() {
        BDict testee = getSampleB();
        BString key = BString.of("Foo");
        BInteger value = BInteger.of(13);

        Assertions.assertEquals(testee.size(), testee.getValue().size());
        Assertions.assertEquals(testee.isEmpty(), testee.getValue().isEmpty());
        Assertions.assertEquals(testee.containsKey(key), testee.getValue().containsKey(key));
        Assertions.assertEquals(testee.containsValue(key), testee.getValue().containsValue(key));
        Assertions.assertEquals(testee.get(key), Optional.of(value));
        Assertions.assertEquals(testee.get(key.toString()), Optional.of(value));
        Assertions.assertEquals(testee.get("xxxxx"), Optional.empty());
        Assertions.assertEquals(testee.keySet(), testee.getValue().keySet());
        Assertions.assertEquals(testee.values(), testee.getValue().values());
        Assertions.assertEquals(testee.entrySet(), testee.getValue().entrySet());
    }

    @Test
    public void testRemove() {
        BDict testee = getSampleB();
        Assertions.assertEquals(1, testee.remove("Foo").size());
        Assertions.assertEquals(1, testee.remove("baz").size());
        Assertions.assertEquals(1, testee.remove(BString.of("baz")).size());
        Assertions.assertSame(testee, testee.remove(BString.of("xxx")));
        Assertions.assertEquals(2, testee.size());
    }

    @Test
    public void tesPut() {
        BDict testee = getSampleB();
        Assertions.assertEquals(3, testee.put(BString.of("xxx"), BInteger.of(1)).size());
        Assertions.assertEquals(2, testee.size());
    }

    @Test
    public void testJoin() {
        BDict a = getSampleA();
        BDict b = getSampleB();
        Assertions.assertEquals(4, a.join(b).size());
        Assertions.assertEquals(2, a.size());
    }

    @Test
    public void testImmutable() {
        Map<BString, BNode<?>> data = new TreeMap<>();
        data.put(BString.of("foo"), BInteger.of(13));
        BDict a = BDict.of(data);
        data.put(BString.of("bar"), BInteger.of(42));
        BDict b = BDict.of(data);
        Assertions.assertNotEquals(a, b);
    }

    @Test
    public void testOfInvalidPrefix() throws IOException {
        try (InputStream is = new ByteArrayInputStream(new byte[0])) {
            Assertions.assertThrows(BencodeException.class, () -> {
                BDict.of(is, (byte) 'x');
            });
        }
    }

    @Test
    public void testClone() {
        BDict orig = BDict.of(BString.of("foo"), BString.of("bar"));
        BDict clone = orig.clone();

        Assertions.assertEquals(orig, clone);
        Assertions.assertNotSame(orig, clone);
    }
}
