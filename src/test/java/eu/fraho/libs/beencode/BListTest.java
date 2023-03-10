package eu.fraho.libs.beencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;

public class BListTest extends AbstractTest<BList> {
    @Override
    protected String getSampleAEncoded() {
        return "l3:Fooi13eli42eee";
    }

    @Override
    protected String getSampleAToString() {
        return "[Foo, 13, [42]]";
    }

    @Override
    protected BList getSampleA() {
        return BList.of(
                BString.of("Foo"),
                BInteger.of(13),
                BList.of(
                        BInteger.of(42)
                )
        );
    }

    @Override
    protected BList getSampleB() {
        return BList.of(
                BInteger.of(13),
                BString.of("test")
        );
    }

    @Test
    public void testStreamExtraData() throws IOException {
        testStreamSuccess("blist_extra_data", getSampleB());
    }

    @Test
    public void testStream() throws IOException {
        testStreamSuccess("blist_simple", getSampleB());
    }

    @Test
    public void testStreamInvalidEmpty() throws IOException {
        Assertions.assertThrows(BencodeException.class, () -> {
            testStreamFail("blist_invalid_empty");
        });
    }

    @Test
    public void testStreamInvalidEnd() throws IOException {
        Assertions.assertThrows(BencodeException.class, () -> {
            testStreamFail("blist_invalid_end");
        });
    }

    @Test
    public void testDelegateMethods() {
        BList testee = getSampleA();
        BInteger eNonExistant = BInteger.of(12345);
        BString e0 = BString.of("Foo");
        BNode<?>[] array = new BNode[3];

        Assertions.assertEquals(testee.size(), testee.getValue().size());
        Assertions.assertEquals(testee.isEmpty(), testee.getValue().isEmpty());
        Assertions.assertEquals(testee.contains(eNonExistant), testee.getValue().contains(eNonExistant));
        Assertions.assertArrayEquals(testee.toArray(), testee.getValue().toArray());
        Assertions.assertArrayEquals(testee.toArray(array), testee.getValue().toArray(array));
        Assertions.assertEquals(testee.containsAll(Collections.singletonList(eNonExistant)), testee.getValue().contains(eNonExistant));
        Assertions.assertEquals(testee.get(0), Optional.of(e0));
        Assertions.assertEquals(testee.get(42), Optional.empty());
        Assertions.assertEquals(testee.indexOf(eNonExistant), testee.getValue().indexOf(eNonExistant));
        Assertions.assertEquals(testee.lastIndexOf(eNonExistant), testee.getValue().lastIndexOf(eNonExistant));
        Assertions.assertEquals(testee.subList(0, 1), testee.getValue().subList(0, 1));
        Assertions.assertEquals(testee.subList(1, 2), testee.getValue().subList(1, 2));

        /* test iterator */
        {
            Iterator<BNode<?>> a = testee.iterator();
            Iterator<BNode<?>> b = testee.getValue().iterator();
            while (a.hasNext()) {
                Assertions.assertEquals(a.next(), b.next());
            }
            Assertions.assertFalse(b.hasNext());
        }
        /* end test iterator */

        /* test spliterator */
        {
            Spliterator<BNode<?>> a = testee.spliterator();
            Spliterator<BNode<?>> b = testee.getValue().spliterator();

            AtomicInteger sizeA = new AtomicInteger();
            AtomicInteger sizeB = new AtomicInteger();

            a.forEachRemaining(e -> sizeA.addAndGet(e.hashCode()));
            b.forEachRemaining(e -> sizeB.addAndGet(e.hashCode()));
            Assertions.assertEquals(sizeA.get(), sizeB.get());
        }
        /* end test spliterator */

        /* test list iterator without arg */
        {
            Iterator<BNode<?>> a = testee.listIterator();
            Iterator<BNode<?>> b = testee.getValue().listIterator();
            while (a.hasNext()) {
                Assertions.assertEquals(a.next(), b.next());
            }
            Assertions.assertFalse(b.hasNext());
        }
        /* end test list iterator without arg */

        /* test list iterator with arg */
        for (int i = 0; i < 2; i++) {
            Iterator<BNode<?>> a = testee.listIterator(i);
            Iterator<BNode<?>> b = testee.getValue().listIterator(i);
            while (a.hasNext()) {
                Assertions.assertEquals(a.next(), b.next());
            }
            Assertions.assertFalse(b.hasNext());
        }
        /* end test list iterator with arg */

        /* test stream */
        {
            Assertions.assertEquals(testee.stream().mapToLong(BNode::hashCode).sum(), testee.getValue().stream().mapToLong(BNode::hashCode).sum());
        }
        /* end test stream */

        /* test forEach */
        {
            AtomicInteger sizeA = new AtomicInteger();
            AtomicInteger sizeB = new AtomicInteger();

            testee.forEach(e -> sizeA.addAndGet(e.hashCode()));
            testee.getValue().forEach(e -> sizeB.addAndGet(e.hashCode()));
            Assertions.assertEquals(sizeA.get(), sizeB.get());
        }
        /* end test forEach */
    }

    @Test
    public void testRemove() {
        BList testee = getSampleB();
        Assertions.assertEquals(1, testee.remove(0).size());
        Assertions.assertEquals(1, testee.remove(1).size());
        Assertions.assertEquals(1, testee.remove(BInteger.of(13)).size());
        Assertions.assertSame(testee, testee.remove(BInteger.of(99)));
        Assertions.assertEquals(2, testee.size());
    }

    @Test
    public void testAdd() {
        BList testee = getSampleB();
        Assertions.assertEquals(3, testee.add(BInteger.of(42)).size());
        Assertions.assertEquals(4, testee.add(BInteger.of(42), BInteger.of(1)).size());
        Assertions.assertEquals(2, testee.size());
    }

    @Test
    public void testJoin() {
        BList a = getSampleA();
        BList b = getSampleB();
        Assertions.assertEquals(5, a.join(b).size());
        Assertions.assertEquals(3, a.size());
    }

    @Test
    public void testImmutable() {
        List<BNode<?>> data = new ArrayList<>();
        data.add(BString.of("foo"));
        BList a = BList.of(data);
        data.add(BString.of("bar"));
        BList b = BList.of(data);
        Assertions.assertNotEquals(a, b);
    }

    @Test
    public void testOfInvalidPrefix() throws IOException {
        try (InputStream is = new ByteArrayInputStream(new byte[0])) {
            Assertions.assertThrows(BencodeException.class, () -> {
                BList.of(is, (byte) 'x');
            });
        }
    }

    @Test
    public void testClone() {
        BList orig = BList.of(BString.of("foobar"), BInteger.of(42));
        BList clone = orig.clone();

        Assertions.assertEquals(orig, clone);
        Assertions.assertNotSame(orig, clone);
    }
}
