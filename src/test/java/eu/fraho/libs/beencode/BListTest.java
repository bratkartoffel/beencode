package eu.fraho.libs.beencode;

import eu.fraho.libs.beencode.helpers.TestcaseHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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
        TestcaseHelper.testStreamSuccess("blist_extra_data", getSampleB());
    }

    @Test
    public void testStream() throws IOException {
        TestcaseHelper.testStreamSuccess("blist_simple", getSampleB());
    }

    @Test(expected = BencodeException.class)
    public void testStreamInvalidEmpty() throws IOException {
        TestcaseHelper.testStreamFail("blist_invalid_empty");
    }

    @Test(expected = BencodeException.class)
    public void testStreamInvalidEnd() throws IOException {
        TestcaseHelper.testStreamFail("blist_invalid_end");
    }

    @Test
    public void testDelegateMethods() {
        BList testee = getSampleA();
        BInteger eNonExistant = BInteger.of(12345);
        BString e0 = BString.of("Foo");
        BNode<?>[] array = new BNode[3];

        Assert.assertEquals(testee.size(), testee.getValue().size());
        Assert.assertEquals(testee.isEmpty(), testee.getValue().isEmpty());
        Assert.assertEquals(testee.contains(eNonExistant), testee.getValue().contains(eNonExistant));
        Assert.assertArrayEquals(testee.toArray(), testee.getValue().toArray());
        Assert.assertArrayEquals(testee.toArray(array), testee.getValue().toArray(array));
        Assert.assertEquals(testee.containsAll(Collections.singletonList(eNonExistant)), testee.getValue().contains(eNonExistant));
        Assert.assertEquals(testee.get(0), Optional.of(e0));
        Assert.assertEquals(testee.get(42), Optional.empty());
        Assert.assertEquals(testee.indexOf(eNonExistant), testee.getValue().indexOf(eNonExistant));
        Assert.assertEquals(testee.lastIndexOf(eNonExistant), testee.getValue().lastIndexOf(eNonExistant));
        Assert.assertEquals(testee.subList(0, 1), testee.getValue().subList(0, 1));
        Assert.assertEquals(testee.subList(1, 2), testee.getValue().subList(1, 2));

        /* test iterator */
        {
            Iterator a = testee.iterator();
            Iterator b = testee.getValue().iterator();
            while (a.hasNext()) {
                Assert.assertEquals(a.next(), b.next());
            }
            Assert.assertFalse(a.hasNext());
            Assert.assertFalse(b.hasNext());
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
            Assert.assertEquals(sizeA.get(), sizeB.get());
        }
        /* end test spliterator */

        /* test list iterator without arg */
        {
            Iterator a = testee.listIterator();
            Iterator b = testee.getValue().listIterator();
            while (a.hasNext()) {
                Assert.assertEquals(a.next(), b.next());
            }
            Assert.assertFalse(a.hasNext());
            Assert.assertFalse(b.hasNext());
        }
        /* end test list iterator without arg */

        /* test list iterator with arg */
        for (int i = 0; i < 2; i++) {
            Iterator a = testee.listIterator(i);
            Iterator b = testee.getValue().listIterator(i);
            while (a.hasNext()) {
                Assert.assertEquals(a.next(), b.next());
            }
            Assert.assertFalse(a.hasNext());
            Assert.assertFalse(b.hasNext());
        }
        /* end test list iterator with arg */

        /* test stream */
        {
            Assert.assertEquals(testee.stream().mapToLong(BNode::hashCode).sum(), testee.getValue().stream().mapToLong(BNode::hashCode).sum());
        }
        /* end test stream */

        /* test forEach */
        {
            AtomicInteger sizeA = new AtomicInteger();
            AtomicInteger sizeB = new AtomicInteger();

            testee.forEach(e -> sizeA.addAndGet(e.hashCode()));
            testee.getValue().forEach(e -> sizeB.addAndGet(e.hashCode()));
            Assert.assertEquals(sizeA.get(), sizeB.get());
        }
        /* end test forEach */
    }

    @Test
    public void testRemove() {
        BList testee = getSampleB();
        Assert.assertEquals(1, testee.remove(0).size());
        Assert.assertEquals(1, testee.remove(1).size());
        Assert.assertEquals(1, testee.remove(BInteger.of(13)).size());
        Assert.assertSame(testee, testee.remove(BInteger.of(99)));
        Assert.assertEquals(2, testee.size());
    }

    @Test
    public void testAdd() {
        BList testee = getSampleB();
        Assert.assertEquals(3, testee.add(BInteger.of(42)).size());
        Assert.assertEquals(4, testee.add(BInteger.of(42), BInteger.of(1)).size());
        Assert.assertEquals(2, testee.size());
    }

    @Test
    public void testJoin() {
        BList a = getSampleA();
        BList b = getSampleB();
        Assert.assertEquals(5, a.join(b).size());
        Assert.assertEquals(3, a.size());
    }

    @Test
    public void testImmutable() {
        List<BNode<?>> data = new ArrayList<>();
        data.add(BString.of("foo"));
        BList a = BList.of(data);
        data.add(BString.of("bar"));
        BList b = BList.of(data);
        Assert.assertNotEquals(a, b);
    }

    @Test(expected = BencodeException.class)
    public void testOfInvalidPrefix() throws IOException {
        try (InputStream is = new ByteArrayInputStream(new byte[0])) {
            BList.of(is, (byte) 'x');
        }
    }
}
