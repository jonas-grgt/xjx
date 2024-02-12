package io.jonasg.xjx.serdes.deserialize;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.jonasg.xjx.sax.Attribute;
import io.jonasg.xjx.sax.SaxHandler;

public class TypedValueMapSaxHandler implements SaxHandler {

	private final Class<?> valueType;

	private final Map<String, Object> instance;

    private PathBasedSaxHandler<Object> objectPathBasedSaxHandler;

    private String activeKey;

	public TypedValueMapSaxHandler(MapWithTypeInfo instance) {
		this.instance = instance.map();
        this.valueType = instance.valueType();
    }

    @Override
    public void startDocument() {
    }

    @Override
    public void startTag(String namespace, String name, List<Attribute> attributes) {
        if (this.activeKey == null) {
            this.activeKey = name;
            objectPathBasedSaxHandler = new PathBasedSaxHandler<>(rootTag ->
                    new PathWriterIndexFactory().createIndexForType(valueType, this.activeKey), this.activeKey);
        } else {
            objectPathBasedSaxHandler.startTag(namespace, name, attributes);
        }
    }

    @Override
    public void endTag(String namespace, String name) {
        if (Objects.equals(this.activeKey, name)) {
            this.instance.put(this.activeKey, objectPathBasedSaxHandler.instance());
            this.activeKey = null;
        } else {
            objectPathBasedSaxHandler.endTag(namespace, name);
        }
    }

    @Override
    public void characters(String data) {
        objectPathBasedSaxHandler.characters(data);
    }

    public Map<String, Object> instance() {
        return instance;
    }

}
