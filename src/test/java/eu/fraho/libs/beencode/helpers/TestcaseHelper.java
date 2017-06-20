package eu.fraho.libs.beencode.helpers;

import eu.fraho.libs.beencode.BNode;
import eu.fraho.libs.beencode.NodeFactory;
import org.junit.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TestcaseHelper {

    public static void testStreamFail(String testname) throws IOException {
        try (FileInputStream fstream = new FileInputStream(new File("src/test/resources/data/",
                testname + ".dat"))) {
            BNode<?> bi = NodeFactory.decode(fstream);

            Assert.fail("Should not succeed, data read: " + bi);
        }
    }

    public static void testStreamSuccess(String testname, BNode<?> expected)
            throws IOException {
        try (FileInputStream fstream = new FileInputStream(new File("src/test/resources/data/",
                testname + ".dat"))) {
            BNode<?> bi = NodeFactory.decode(fstream);

            Assert.assertEquals("Received unexpected result", expected, bi);
        }
    }

}
