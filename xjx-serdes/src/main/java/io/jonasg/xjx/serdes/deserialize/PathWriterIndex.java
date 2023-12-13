package io.jonasg.xjx.serdes.deserialize;

import java.util.Map;

public class PathWriterIndex {

    public final Map<Path, PathWriter> index;

    public PathWriterIndex(Map<Path, PathWriter> index) {
        this.index = index;
    }

//    public void write(Path path, String value) {
//        List<PathValueWriter> pathValueWriters = index.get(path);
//        if (pathValueWriters != null) {
//            pathValueWriters.forEach(p -> p.write(value));
//        }
//    }
//
//    public void initialize(Path path) {
//        List<PathValueWriter> pathValueWriters = index.get(path);
//        if (pathValueWriters != null) {
//            pathValueWriters.forEach(PathValueWriter::initialize);
//        }
//    }
//
//    public void finalizeWrite(Path path) {
//        List<PathValueWriter> pathValueWriters = index.get(path);
//        if (pathValueWriters != null) {
//            pathValueWriters.forEach(PathValueWriter::finalizeWrite);
//        }
//    }
//
//    public void put(Path path, PathValueWriter pathValueWriter) {
//        index.compute(path, (p, writers) -> {
//            if (writers == null) {
//                writers = new ArrayList<>();
//            }
//            writers.add(pathValueWriter);
//            return writers;
//        });
//    }
}
