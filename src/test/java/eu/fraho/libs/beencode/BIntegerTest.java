package eu.fraho.libs.beencode;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class BIntegerTest extends AbstractTest<BInteger> {
    @Override
    protected String getSampleAEncoded() {
        return "i13e";
    }

    @Override
    protected String getSampleAToString() {
        return "13";
    }

    @Override
    protected BInteger getSampleA() {
        return BInteger.of(13L);
    }

    @Override
    protected BInteger getSampleB() {
        return BInteger.of(42);
    }

    @Test
    public void testCompare() {
        BInteger a = getSampleA();
        BInteger b = getSampleB();

        Assert.assertEquals(-1, a.compareTo(b));
    }

    @Test
    public void testWithBigInteger() {
        BInteger testee = BInteger.of(new BigInteger("1598574885652145874569988813"));
        Assert.assertNotNull(testee);
    }

    @Test
    public void testWithObjectLong() {
        BInteger testee = BInteger.of(77L);
        Assert.assertNotNull(testee);
    }

    @Test
    public void testStream() throws IOException {
        testStreamSuccess("bint_simple", BInteger.of(1337));
    }

    @Test
    public void testStreamExtraData() throws IOException {
        testStreamSuccess("bint_extra_data", BInteger.of(1337));
    }

    @Test
    public void testStreamZero() throws IOException {
        testStreamSuccess("bint_0", BInteger.of(0));
    }

    @Test
    public void testStreamNegativeOne() throws IOException {
        testStreamSuccess("bint_neg_1", BInteger.of(-1));
    }

    @Test
    public void testStreamNegative() throws IOException {
        testStreamSuccess("bint_simple_neg", BInteger.of(-1337));
    }

    @Test
    public void testStreamMax() throws IOException {
        testStreamSuccess("bint_max", BInteger.of(Long.MAX_VALUE));
    }

    @Test
    public void testStreamMin() throws IOException {
        testStreamSuccess("bint_min", BInteger.of(Long.MIN_VALUE));
    }

    @Test(expected = BencodeException.class)
    public void testStreamInvalidData() throws IOException {
        testStreamFail("bint_invalid_data");
    }

    @Test(expected = BencodeException.class)
    public void testStreamOnlyDash() throws IOException {
        testStreamFail("bint_only_dash");
    }

    @Test
    public void testStream9() throws IOException {
        testStreamSuccess("bint_9", BInteger.of(9));
    }

    @Test(expected = BencodeException.class)
    public void testStreamInvalidEmpty() throws IOException {
        testStreamFail("bint_invalid_empty");
    }

    @Test(expected = BencodeException.class)
    public void testStreamInvalidNoData() throws IOException {
        testStreamFail("bint_invalid_nodata");
    }

    @Test(expected = BencodeException.class)
    public void testStreamInvalidTwoDashes1() throws IOException {
        testStreamFail("bint_invalid_two_dashes_1");
    }

    @Test(expected = BencodeException.class)
    public void testStreamInvalidTwoDashes2() throws IOException {
        testStreamFail("bint_invalid_two_dashes_2");
    }

    @Test(expected = BencodeException.class)
    public void testStreamInvalidLeadingZero() throws IOException {
        testStreamFail("bint_leading_zero");
    }

    @Test(expected = BencodeException.class)
    public void testStreamInvalidLeadingZeroNeg() throws IOException {
        testStreamFail("bint_leading_zero_neg");
    }

    @Test(expected = BencodeException.class)
    public void testStreamInvalidNegativeZero() throws IOException {
        testStreamFail("bint_neg_0");
    }

    @Test(expected = BencodeException.class)
    public void testStreamTooLong() throws IOException {
        testStreamFail("bint_too_long");
    }

    @Test(expected = BencodeException.class)
    public void testOfInvalidPrefix() throws IOException {
        try (InputStream is = new ByteArrayInputStream(new byte[0])) {
            BInteger.of(is, (byte) 'x');
        }
    }

    @Test
    public void testClone() {
        BInteger orig = BInteger.of(42);
        BInteger clone = orig.clone();

        Assert.assertEquals("Equals", orig, clone);
        Assert.assertNotSame("NotSame", orig, clone);
    }
}
