package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.reflector.FieldReflector;

public class TagPath {

    private final Tag tag;
    private final FieldReflector field;

    public TagPath(Tag tag, FieldReflector field) {
        this.tag = tag;
        this.field = field;
    }

    public boolean isAbsolute() {
        return tag.path().trim().startsWith("/");
    }

    public FieldReflector field() {
        return field;
    }

    public String path() {
        return tag.path().trim();
    }

    public String attribute() {
        return tag.attribute();
    }
}
