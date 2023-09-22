package io.jonasg.xjx;

public record StartTag(String name, String namespace, Attributes attributes) {

    public StartTag(String name) {
        this(name, null, Attributes.empty());
    }

    public StartTag(String name, String namespace) {
        this(name, namespace, Attributes.empty());
    }

    public StartTag(String name, Attributes attributes) {
        this(name, null, attributes);
    }
}
