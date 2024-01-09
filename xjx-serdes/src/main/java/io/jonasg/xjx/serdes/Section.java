package io.jonasg.xjx.serdes;

import java.util.StringJoiner;

public class Section {

    private final String name;
    private final boolean isLeaf;

    public Section(String name) {
        this.name = name;
        isLeaf = false;
    }

    public Section(String name, boolean isLeaf) {
        this.name = name;
        this.isLeaf = isLeaf;
    }

    public String name() {
        return name;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Section.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("isLeaf=" + isLeaf)
                .toString();
    }
}
