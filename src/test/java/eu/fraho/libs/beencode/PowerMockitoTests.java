/*
 * MIT Licence
 * Copyright (c) 2018 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@PrepareForTest({BDict.class, BInteger.class, BList.class, BString.class})
@RunWith(PowerMockRunner.class)
public class PowerMockitoTests {
    @Test
    public void testCloneNotSupportedBDict() {
        BDict orig = BDict.of(BString.of("foo"), BString.of("bar"));
        BDict mock = Mockito.spy(orig);
        Mockito.doThrow(new CloneNotSupportedException()).when(mock).preCloneForUnitTesting();
        PowerMockito.mockStatic(BDict.class, Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS));
        BDict clone = mock.clone();

        Assert.assertEquals("Equals", orig, clone);
        Assert.assertNotSame("NotSame", orig, clone);
    }

    @Test
    public void testCloneNotSupportedBInteger() {
        BInteger orig = BInteger.of(42);
        BInteger mock = Mockito.spy(orig);
        Mockito.doThrow(new CloneNotSupportedException()).when(mock).preCloneForUnitTesting();
        PowerMockito.mockStatic(BDict.class, Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS));
        BInteger clone = mock.clone();

        Assert.assertEquals("Equals", orig, clone);
        Assert.assertNotSame("NotSame", orig, clone);
    }

    @Test
    public void testCloneNotSupportedBList() {
        BList orig = BList.of(BString.of("foobar"), BInteger.of(42));
        BList mock = Mockito.spy(orig);
        Mockito.doThrow(new CloneNotSupportedException()).when(mock).preCloneForUnitTesting();
        PowerMockito.mockStatic(BDict.class, Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS));
        BList clone = mock.clone();

        Assert.assertEquals("Equals", orig, clone);
        Assert.assertNotSame("NotSame", orig, clone);
    }

    @Test
    public void testCloneNotSupportedBString() {
        BString orig = BString.of("foobar");
        BString mock = Mockito.spy(orig);
        Mockito.doThrow(new CloneNotSupportedException()).when(mock).preCloneForUnitTesting();
        PowerMockito.mockStatic(BDict.class, Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS));
        BString clone = mock.clone();

        Assert.assertEquals("Equals", orig, clone);
        Assert.assertNotSame("NotSame", orig, clone);
    }

    @Test(expected = BencodeException.class)
    public void testDecodeArray() throws IOException {
        PowerMockito.mockStatic(BInteger.class);
        PowerMockito.when(BInteger.of(Mockito.any(InputStream.class), Mockito.anyByte())).thenThrow(new IOException());
        PowerMockito.when(BInteger.canParsePrefix(Mockito.anyByte())).thenReturn(true);
        try {
            NodeFactory.decode("i42e".getBytes(StandardCharsets.US_ASCII));
        } catch (BencodeException be) {
            Assert.assertEquals("Cause should be an IOException", IOException.class, be.getCause().getClass());
            throw be;
        }
    }

    @Test(expected = BencodeException.class)
    public void testDecodeArrayWithClass() throws IOException {
        PowerMockito.mockStatic(BInteger.class);
        PowerMockito.when(BInteger.of(Mockito.any(InputStream.class), Mockito.anyByte())).thenThrow(new IOException());
        PowerMockito.when(BInteger.canParsePrefix(Mockito.anyByte())).thenReturn(true);
        try {
            NodeFactory.decode("i42e".getBytes(StandardCharsets.US_ASCII), BInteger.class);
        } catch (BencodeException be) {
            Assert.assertEquals("Cause should be an IOException", IOException.class, be.getCause().getClass());
            throw be;
        }
    }

    @Test(expected = BencodeException.class)
    public void testEncodeWithIoException() throws IOException {
        BInteger testee = Mockito.mock(BInteger.class);
        Mockito.doThrow(new IOException()).when(testee).write(Mockito.any(OutputStream.class));
        try {
            NodeFactory.encode(testee);
        } catch (BencodeException be) {
            Assert.assertEquals("Cause should be an IOException", IOException.class, be.getCause().getClass());
            throw be;
        }
    }
}
