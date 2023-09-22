package io.jonasg.xjx.serdes;

import io.jonasg.xjx.sax.SaxParser;
import io.jonasg.xjx.serdes.deserialize.PathBasedSaxHandler;
import io.jonasg.xjx.serdes.deserialize.PathWriterIndexFactory;

import java.io.Reader;
import java.io.StringReader;

public class XjxSerdes {

    private final SaxParser saxParser;

    private final PathWriterIndexFactory pathWriterIndexFactory;

    private XjxSerdes(SaxParser saxParser, PathWriterIndexFactory pathWriterIndexFactory) {
        this.saxParser = saxParser;
        this.pathWriterIndexFactory = pathWriterIndexFactory;
    }

    public XjxSerdes() {
        this(new SaxParser(), new PathWriterIndexFactory());
    }

    public <T> T read(String data, Class<T> clazz) {
        return read(new StringReader(data), clazz);
    }

    public <T> T read(Reader data, Class<T> clazz) {
        PathBasedSaxHandler<T> saxHandler = new PathBasedSaxHandler<>((rootTag) -> pathWriterIndexFactory.createIndexForType(clazz, rootTag));
        saxParser.parse(data, saxHandler);
        return saxHandler.instance();
    }
}
