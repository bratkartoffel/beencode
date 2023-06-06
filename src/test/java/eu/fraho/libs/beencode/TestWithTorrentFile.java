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

public class TestWithTorrentFile {
    @Test
    public void tryParse() throws IOException {
        byte[] file = Files.readAllBytes(Paths.get("src", "test", "resources", "data", "debian-9.4.0-amd64-DVD-1.iso.torrent.dat"));

        BDict data;
        try (InputStream fstream = new ByteArrayInputStream(file)) {
            data = BDict.of(fstream);
        }

        // check a string node
        BString announce = data.get("announce");
        Assertions.assertNotNull(announce);
        Assertions.assertEquals(BString.of("http://bttracker.debian.org:6969/announce"), announce);

        // check a list node
        BList httpseeds = data.get("httpseeds");
        Assertions.assertNotNull(httpseeds);
        Assertions.assertEquals(2, httpseeds.size());
        Assertions.assertEquals(BList.of(
            BString.of("https://cdimage.debian.org/cdimage/release/9.4.0//srv/cdbuilder.debian.org/dst/deb-cd/weekly-builds/amd64/iso-dvd/debian-9.4.0-amd64-DVD-1.iso"),
            BString.of("https://cdimage.debian.org/cdimage/archive/9.4.0//srv/cdbuilder.debian.org/dst/deb-cd/weekly-builds/amd64/iso-dvd/debian-9.4.0-amd64-DVD-1.iso")
        ), httpseeds);

        // check a dict node
        BDict info = data.get("info");
        Assertions.assertNotNull(info);

        // check a integer node
        BInteger length = info.get("length");
        Assertions.assertNotNull(length);
        Assertions.assertEquals(BInteger.of(3977379840L), length);

        // check serialization
        Assertions.assertArrayEquals(file, NodeFactory.encode(data));
    }
}
