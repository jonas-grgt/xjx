package io.jonasg.xjx.serdes;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import io.jonasg.xjx.sax.SaxParser;
import io.jonasg.xjx.serdes.deserialize.config.ConfigurationBuilder;
import io.jonasg.xjx.serdes.deserialize.MapOf;
import io.jonasg.xjx.serdes.deserialize.MapRootSaxHandler;
import io.jonasg.xjx.serdes.deserialize.PathBasedSaxHandler;
import io.jonasg.xjx.serdes.deserialize.PathWriterIndexFactory;
import io.jonasg.xjx.serdes.deserialize.XjxDeserializationException;
import io.jonasg.xjx.serdes.deserialize.config.XjxConfiguration;
import io.jonasg.xjx.serdes.serialize.XmlNodeStructureFactory;
import io.jonasg.xjx.serdes.serialize.XmlStringBuilder;

/**
 * XjxSerdes provides functionality for serializing and deserializing objects to and from XML.
 */
public class XjxSerdes {

    private final SaxParser saxParser;

    private final PathWriterIndexFactory pathWriterIndexFactory;

    private final XmlNodeStructureFactory xmlNodeStructureFactory = new XmlNodeStructureFactory();

    private final XmlStringBuilder xmlStringBuilder;

	private final XjxConfiguration configuration;

	private XjxSerdes(SaxParser saxParser,
			XmlStringBuilder xmlStringBuilder,
			Consumer<ConfigurationBuilder> configurationBuilder) {
		this.configuration = new XjxConfiguration();
		configurationBuilder.accept(new ConfigurationBuilder(configuration));
        this.saxParser = saxParser;
        this.pathWriterIndexFactory = new PathWriterIndexFactory(configuration);
        this.xmlStringBuilder = xmlStringBuilder;
	}

    /**
     * Constructs an XjxSerdes instance with default configurations.
     */
    public XjxSerdes() {
        this(new SaxParser(), new XmlStringBuilder(), (builder) -> {});
    }

	/**
	 * Constructs an XjxSerdes instance with custom configurations.
	 * @param configurationBuilder The configuration builder to configure the XjxSerdes instance.
	 */
	public XjxSerdes(Consumer<ConfigurationBuilder> configurationBuilder) {
		this(new SaxParser(), new XmlStringBuilder(), configurationBuilder);
	}

	/**
     * Reads XML data and deserializes it into an object of the specified class.
     *
     * @param data  The XML data to read.
     * @param clazz The class type to deserialize the XML data into.
     * @param <T>   The generic type of the class.
     * @return The deserialized object.
     */
    public <T> T read(String data, Class<T> clazz) {
        return read(new StringReader(data), clazz);
    }

    /**
     * Reads XML data from a reader and deserializes it into an object of the specified class.
     *
     * @param data  The reader containing XML data to read.
     * @param clazz The class type to deserialize the XML data into.
     * @param <T>   The generic type of the class.
     * @return The deserialized object.
     */
    public <T> T read(Reader data, Class<T> clazz) {
        PathBasedSaxHandler<T> saxHandler = new PathBasedSaxHandler<>((rootTag) -> pathWriterIndexFactory.createIndexForType(clazz, rootTag), this.configuration);
        saxParser.parse(data, saxHandler);
        return saxHandler.instance();
    }


    /**
     * Reads XML data and deserializes it into a map with specified key and value types.
     *
     * @param data   The XML data to read.
     * @param mapOf  The MapOf instance specifying key and value types.
     * @param <K>    The type of map keys (only supports String).
     * @param <V>    The type of map values.
     * @return The deserialized map.
     * @throws XjxDeserializationException If the map key type is not supported (only supports String).
     * <p>
     * Example usage:
     * <pre>{@code
     * MapOf<String, Object> mapOf = new MapOf<String, Object>() {};
     * Map<String, Object> deserializedMap = new XjxSerdes().read("<xml>...</xml>", mapOf);
     * }</pre>
     */
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

    /**
     * Writes an object to an XML document.
     *
     * @param data The object to serialize to XML.
     * @param <T>  The generic type of the object.
     * @return The XML representation of the object.
     */
    public <T> String write(T data) {
        var nodes = xmlNodeStructureFactory.build(data);
        return xmlStringBuilder.build(nodes);
    }

}
