package io.jonasg.xjx.serdes.serialize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.jonasg.xjx.Attributes;

public final class XmlNode {
    private final String name;
    private Object value;
    private final List<XmlNode> children;
    private final Attributes attributes;

    public XmlNode(String name, Object value, List<XmlNode> children, Attributes attributes) {
        this.name = name;
        this.value = value;
        this.children = children;
        this.attributes = attributes;
    }

    public XmlNode(String name) {
        this(name, null, new ArrayList<>(), new Attributes());
    }

    public XmlNode(String name, Object value) {
        this(name, value, null, new Attributes());
    }

    public void addValueNode(String name, Object value) {
        this.children.stream()
                .filter(n -> Objects.equals(n.name, name))
                .findFirst()
                .map(n -> n.value = value)
                .orElseGet(() -> {
                    var node = new XmlNode(name, value);
                    this.children.add(node);
                    return node;
                });
    }

    public XmlNode addNode(String name) {
        return this.children.stream()
                .filter(n -> Objects.equals(n.name, name))
                .findFirst()
                .orElseGet(() -> {
                    var node = new XmlNode(name);
                    this.children.add(node);
                    return node;
                });
    }

    public void addAttribute(String attribute, Object value) {
        this.attributes.add(attribute, String.valueOf(value));
    }

    public boolean hasChildren() {
        return this.children != null && !this.children.isEmpty();
    }

    public boolean containsAValue() {
        return value != null;
    }

    public boolean hasAttributes() {
        return !this.attributes.isEmpty();
    }

    public String name() {
        return name;
    }

    public Object value() {
        return value;
    }

    public List<XmlNode> children() {
        return children;
    }

    public Attributes attributes() {
        return attributes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (XmlNode) obj;
        return Objects.equals(this.name, that.name) &&
               Objects.equals(this.value, that.value) &&
               Objects.equals(this.children, that.children) &&
               Objects.equals(this.attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, children, attributes);
    }

    @Override
    public String toString() {
        return "XmlNode[" +
               "name=" + name + ", " +
               "value=" + value + ", " +
               "children=" + children + ", " +
               "attributes=" + attributes + ']';
    }

}
