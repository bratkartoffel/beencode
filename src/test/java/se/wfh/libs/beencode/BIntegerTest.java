package se.wfh.libs.beencode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class BIntegerTest {
	private static final int FUZZING_RUNS = 1000;

	@Test
	public void testClone() {
		BInteger a = BInteger.of(42);
		BInteger b = NodeFactory.clone(a);

		Assert.assertEquals(a, b);
		Assert.assertNotSame(a, b);
	}

	@Test
	public void testEquals() {
		BInteger a = BInteger.of(1);
		BInteger b = BInteger.of(1);
		BInteger c = BInteger.of(2);

		Assert.assertEquals(a, b);
		Assert.assertNotEquals(b, c);
	}

	@Test
	public void testHashCode() {
		BInteger a = BInteger.of(1);
		BInteger b = BInteger.of(1);
		BInteger c = BInteger.of(2);

		Assert.assertEquals(a.hashCode(), b.hashCode());
		Assert.assertNotEquals(b.hashCode(), c.hashCode());
	}

	@Test
	public void testPositiveLong() {
		BInteger bi = BInteger.of(1337);

		Assert.assertEquals(Long.valueOf(1337), bi.getValue());
	}

	@Test
	public void testNegativeLong() {
		BInteger bi = BInteger.of(-1337);

		Assert.assertEquals(Long.valueOf(-1337), bi.getValue());
	}

	@Test
	public void testZero() {
		BInteger bi = BInteger.of(0);

		Assert.assertEquals(Long.valueOf(0), bi.getValue());
	}

	@Test
	public void testStream() throws IOException {
		TestcaseHelper.testStreamSuccess("bint_simple", Long.valueOf(1337));
	}

	@Test
	public void testStreamExtraData() throws IOException {
		TestcaseHelper.testStreamSuccess("bint_extra_data", Long.valueOf(1337));
	}

	@Test
	public void testStreamZero() throws IOException {
		TestcaseHelper.testStreamSuccess("bint_0", Long.valueOf(0));
	}

	@Test
	public void testStreamNegativeOne() throws IOException {
		TestcaseHelper.testStreamSuccess("bint_neg_1", Long.valueOf(-1));
	}

	@Test
	public void testStreamNegative() throws IOException {
		TestcaseHelper.testStreamSuccess("bint_simple_neg", Long.valueOf(-1337));
	}

	@Test
	public void testStreamMax() throws IOException {
		TestcaseHelper.testStreamSuccess("bint_max", Long.valueOf(Long.MAX_VALUE));
	}

	@Test
	public void testStreamMin() throws IOException {
		TestcaseHelper.testStreamSuccess("bint_min", Long.valueOf(Long.MIN_VALUE));
	}

	@Test(expected = BencodeException.class)
	public void testStreamInvalidData() throws IOException {
		TestcaseHelper.testStreamFail("bint_invalid_data");
	}

	@Test(expected = BencodeException.class)
	public void testStreamOnlyDash() throws IOException {
		TestcaseHelper.testStreamFail("bint_only_dash");
	}

	@Test
	public void testStream9() throws IOException {
		TestcaseHelper.testStreamSuccess("bint_9", Long.valueOf(9));
	}

	@Test(expected = BencodeException.class)
	public void testStreamInvalidEmpty() throws IOException {
		TestcaseHelper.testStreamFail("bint_invalid_empty");
	}

	@Test(expected = BencodeException.class)
	public void testStreamInvalidNoData() throws IOException {
		TestcaseHelper.testStreamFail("bint_invalid_nodata");
	}

	@Test(expected = BencodeException.class)
	public void testStreamInvalidTwoDashes1() throws IOException {
		TestcaseHelper.testStreamFail("bint_invalid_two_dashes_1");
	}

	@Test(expected = BencodeException.class)
	public void testStreamInvalidTwoDashes2() throws IOException {
		TestcaseHelper.testStreamFail("bint_invalid_two_dashes_2");
	}

	@Test(expected = BencodeException.class)
	public void testStreamInvalidLeadingZero() throws IOException {
		TestcaseHelper.testStreamFail("bint_leading_zero");
	}

	@Test(expected = BencodeException.class)
	public void testStreamInvalidLeadingZeroNeg() throws IOException {
		TestcaseHelper.testStreamFail("bint_leading_zero_neg");
	}

	@Test(expected = BencodeException.class)
	public void testStreamInvalidNegativeZero() throws IOException {
		TestcaseHelper.testStreamFail("bint_neg_0");
	}

	@Test(expected = BencodeException.class)
	public void testStreamTooLong() throws IOException {
		TestcaseHelper.testStreamFail("bint_too_long");
	}

	@Test
	public void testEncode() throws BencodeException {
		String result = new String(NodeFactory.encode(BInteger.of(42)));

		Assert.assertEquals("i42e", result);
	}

	@Test
	public void testNegativeOne() {
		BInteger bi = BInteger.of(-1);

		Assert.assertEquals(Long.valueOf(-1), bi.getValue());
	}

	@Test
	public void testNegative42() throws IOException {
		TestcaseHelper.testStreamSuccess("bint_neg_42", Long.valueOf(-42));
	}

	@Test
	public void testToString() {
		BInteger bi = BInteger.of(-1);
		Assert.assertEquals("-1", bi.toString());
	}

	@Test
	public void testFuzzingCreate() throws IOException {
		Random rand = new Random();
		for (int i = 0; i < FUZZING_RUNS; i++) {
			Long value = rand.nextLong();
			BInteger node = BInteger.of(value);

			Assert.assertEquals(value, node.getValue());
		}
	}

	@Test
	public void testFuzzingRead() throws IOException {
		Random rand = new Random();
		for (int i = 0; i < FUZZING_RUNS; i++) {
			Long value = rand.nextLong();
			String random = "i" + String.valueOf(value) + "e";

			ByteArrayInputStream bis = null;
			try {
				bis = new ByteArrayInputStream(random.getBytes());
				BInteger node = NodeFactory.decode(bis, BInteger.class);

				Assert.assertEquals(value, node.getValue());
			} finally {
				Java6Helper.close(bis);
			}
		}
	}

	@Test
	public void testFuzzingReadGarbage() throws IOException {
		byte[] data = new byte[100];
		Random rand = new Random();

		for (int i = 0; i < FUZZING_RUNS; i++) {
			rand.nextBytes(data);
			ByteArrayInputStream bis = null;
			try {
				bis = new ByteArrayInputStream(data);
				BInteger node = NodeFactory.decode(bis, BInteger.class);
				System.out.println("Succeeded in creating a fuzzed node '" + node
						+ "' with data: " + Arrays.toString(data));
			} catch (IOException ioe) {
				// expected
			} finally {
				bis.close();
			}
		}
	}

	@Test
	public void testSetValue() {
		BInteger a = BInteger.of(42);

		a.setValue(13l);
		Assert.assertEquals(Long.valueOf(13), a.getValue());
	}
}
