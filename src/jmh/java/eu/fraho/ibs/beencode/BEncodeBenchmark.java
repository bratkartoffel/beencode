/*
 * MIT Licence
 * Copyright (c) 2023 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.ibs.beencode;

import eu.fraho.libs.beencode.BDict;
import eu.fraho.libs.beencode.BInteger;
import eu.fraho.libs.beencode.BList;
import eu.fraho.libs.beencode.BNode;
import eu.fraho.libs.beencode.BString;
import eu.fraho.libs.beencode.NodeFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@SuppressWarnings("unused")
public class BEncodeBenchmark {
    @Benchmark
    public void encodeFactory(Blackhole blackhole, Context ctx) {
        blackhole.consume(NodeFactory.encode(ctx.nodeBDict));
    }

    @Benchmark
    public void decodeFactory(Blackhole blackhole, Context ctx) {
        blackhole.consume(NodeFactory.decode(ctx.dataBDict));
    }

    @Benchmark
    public void decodeBInteger(Blackhole blackhole, Context ctx) throws IOException {
        blackhole.consume(BInteger.of(new ByteArrayInputStream(ctx.dataBInteger)));
    }

    @Benchmark
    public void decodeBString(Blackhole blackhole, Context ctx) throws IOException {
        blackhole.consume(BString.of(new ByteArrayInputStream(ctx.dataBString)));
    }

    @Benchmark
    public void decodeBList(Blackhole blackhole, Context ctx) throws IOException {
        blackhole.consume(BList.of(new ByteArrayInputStream(ctx.dataBList)));
    }

    @Benchmark
    public void decodeBDict(Blackhole blackhole, Context ctx) throws IOException {
        blackhole.consume(BDict.of(new ByteArrayInputStream(ctx.dataBDict)));
    }

    @State(Scope.Benchmark)
    public static class Context {
        private BNode<?> nodeBDict;
        private byte[] dataBDict;
        private byte[] dataBInteger;
        private byte[] dataBString;
        private byte[] dataBList;

        @Setup
        public void setup() throws IOException {
            nodeBDict = BDict.of(
                    BString.of("first key"), BInteger.of(1337L),
                    BString.of("second key"), BList.of(
                            BInteger.of(1),
                            BInteger.of(2),
                            BInteger.of(3),
                            BInteger.of(4)
                    ),
                    BString.of("third key"), BDict.of(
                            BString.of("child 1"), BInteger.of(1234)
                    )
            );
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                nodeBDict.write(bos);
                dataBDict = bos.toByteArray();
            }
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                BInteger.of(1868487645L).write(bos);
                dataBInteger = bos.toByteArray();
            }
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                BString.of("this is just a simple example text").write(bos);
                dataBString = bos.toByteArray();
            }
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                BList.of(
                        BInteger.of(Long.MAX_VALUE / 2),
                        BInteger.of(1),
                        BInteger.of(0),
                        BInteger.of(Long.MAX_VALUE),
                        BInteger.of(Long.MIN_VALUE)
                ).write(bos);
                dataBList = bos.toByteArray();
            }
        }
    }
}
