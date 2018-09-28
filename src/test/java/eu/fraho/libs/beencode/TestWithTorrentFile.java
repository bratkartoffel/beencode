/*
 * MIT Licence
 * Copyright (c) 2018 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class TestWithTorrentFile {
    @Test
    public void tryParse() throws IOException {
        byte[] file = Files.readAllBytes(Paths.get("src", "test", "resources", "data", "debian-9.4.0-amd64-DVD-1.iso.torrent.dat"));

        BDict data;
        try (InputStream fstream = new ByteArrayInputStream(file)) {
            data = BDict.of(fstream);
        }

        // check a string node
        Optional<BString> announce = data.get("announce");
        Assert.assertTrue(announce.isPresent());
        Assert.assertEquals(BString.of("http://bttracker.debian.org:6969/announce"), announce.get());

        // check a list node
        Optional<BList> httpseeds = data.get("httpseeds");
        Assert.assertTrue(httpseeds.isPresent());
        Assert.assertEquals(2, httpseeds.get().size());
        Assert.assertEquals(BList.of(
                BString.of("https://cdimage.debian.org/cdimage/release/9.4.0//srv/cdbuilder.debian.org/dst/deb-cd/weekly-builds/amd64/iso-dvd/debian-9.4.0-amd64-DVD-1.iso"),
                BString.of("https://cdimage.debian.org/cdimage/archive/9.4.0//srv/cdbuilder.debian.org/dst/deb-cd/weekly-builds/amd64/iso-dvd/debian-9.4.0-amd64-DVD-1.iso")
        ), httpseeds.get());

        // check a dict node
        Optional<BDict> info = data.get("info");
        Assert.assertTrue(info.isPresent());

        // check a integer node
        Optional<BInteger> length = ((BDict) info.get()).get("length");
        Assert.assertTrue(length.isPresent());
        Assert.assertEquals(BInteger.of(3977379840L), length.get());

        // check serialization
        Assert.assertArrayEquals(file, NodeFactory.encode(data));
    }
}
