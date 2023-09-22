package io.jonasg.xjx;

public record EndTag(String namespace, String name) {

    public EndTag(String name) {
        this(null, name);
    }
}
