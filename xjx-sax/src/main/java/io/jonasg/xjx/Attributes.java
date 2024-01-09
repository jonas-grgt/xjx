package io.jonasg.xjx;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Stream;

public class Attributes {
    private final Map<String, String> attributes = new LinkedHashMap<>();

    public Attributes(String... values) {
        int length = values.length;
        for (int i = 0; i < length - 1; i += 2) {
            String attributeName = values[i];
            String attributeValue = values[i + 1];
            attributes.put(attributeName, attributeValue);
        }

        if (length % 2 == 1) {
            String attributeName = values[length - 1];
            attributes.put(attributeName, null);
        }
    }

    public static Attributes empty() {
        return new Attributes();
    }

    public void add(String attributeName, String attributeValue) {
        attributes.put(attributeName, attributeValue);
    }

    public boolean isPresent() {
        return !attributes.isEmpty();
    }

    public Stream<Attribute> stream() {
        return attributes.entrySet()
                .stream()
                .map(e -> new Attribute(e.getKey(), e.getValue()));
    }

    public boolean isEmpty() {
        return attributes.isEmpty();
    }


    public record Attribute(String name, String value) {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attributes that = (Attributes) o;

        return attributes.equals(that.attributes);
    }

    @Override
    public int hashCode() {
        return attributes.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Attributes.class.getSimpleName() + "[", "]")
                .add("attributes=" + attributes)
                .toString();
    }
}

