package io.jonasg.xjx.serdes;

import io.jonasg.xjx.sax.SaxParser;
import io.jonasg.xjx.serdes.deserialize.MapOf;
import io.jonasg.xjx.serdes.deserialize.MapRootSaxHandler;
import io.jonasg.xjx.serdes.deserialize.PathBasedSaxHandler;
import io.jonasg.xjx.serdes.deserialize.PathWriterIndexFactory;
import io.jonasg.xjx.serdes.deserialize.XjxDeserializationException;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

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

    public <K, V> Map<K, V> read(String data, MapOf<K, V> mapOf) {
        return read(new StringReader(data), mapOf);
    }

    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> read(Reader data, MapOf<K, V> mapOf) {
        Class<?> keyType = mapOf.keyType();
        Class<?> valueType = mapOf.valueType();
        if (keyType == String.class && valueType == Object.class) {
            HashMap<String, Object> map = new HashMap<>();
            MapRootSaxHandler mapRootSaxHandler = new MapRootSaxHandler(map, true);
            saxParser.parse(data, mapRootSaxHandler);
            return (Map<K, V>) map;
        }
        throw new XjxDeserializationException("Maps only support String as key");
    }
}
