/*
 * MIT Licence
 * Copyright (c) 2023 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.libs.beencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertTrue(announce.isPresent());
        Assertions.assertEquals(BString.of("http://bttracker.debian.org:6969/announce"), announce.get());

        // check a list node
        Optional<BList> httpseeds = data.get("httpseeds");
        Assertions.assertTrue(httpseeds.isPresent());
        Assertions.assertEquals(2, httpseeds.get().size());
        Assertions.assertEquals(BList.of(
                BString.of("https://cdimage.debian.org/cdimage/release/9.4.0//srv/cdbuilder.debian.org/dst/deb-cd/weekly-builds/amd64/iso-dvd/debian-9.4.0-amd64-DVD-1.iso"),
                BString.of("https://cdimage.debian.org/cdimage/archive/9.4.0//srv/cdbuilder.debian.org/dst/deb-cd/weekly-builds/amd64/iso-dvd/debian-9.4.0-amd64-DVD-1.iso")
        ), httpseeds.get());

        // check a dict node
        Optional<BDict> info = data.get("info");
        Assertions.assertTrue(info.isPresent());

        // check a integer node
        Optional<BInteger> length = ((BDict) info.get()).get("length");
        Assertions.assertTrue(length.isPresent());
        Assertions.assertEquals(BInteger.of(3977379840L), length.get());

        // check serialization
        Assertions.assertArrayEquals(file, NodeFactory.encode(data));
    }
}
