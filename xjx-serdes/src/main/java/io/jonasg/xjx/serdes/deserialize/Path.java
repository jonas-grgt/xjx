package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.serdes.Section;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

public class Path implements Iterable<Section> {

    private final LinkedList<String> sections = new LinkedList<>();

    private String attribute;

    private Path(String... paths) {
        Arrays.stream(paths)
                .filter(p -> !p.isEmpty())
                .forEach(sections::add);
    }

    private Path(LinkedList<String> sections, String section) {
        this.sections.addAll(sections);
        this.sections.add(section);
    }

    public Path(LinkedList<String> newSections) {
        this.sections.addAll(newSections);
    }

    public static Path of(String... paths) {
        return new Path(paths);
    }

    public static Path parse(String path) {
        return Path.of(path.split("/"));
    }

    public Path appendAttribute(String attribute) {
        this.attribute = attribute;
        return this;
    }

    public Path append(String section) {
        return new Path(sections, section);
    }

    public Path append(Path path) {
        var copiedSections = new LinkedList<>(this.sections);
        copiedSections.addAll(path.sections);
        return new Path(copiedSections);
    }

    public Path pop() {
        if (!sections.isEmpty()) {
            LinkedList<String> newSections = new LinkedList<>(sections);
            newSections.removeLast();
            return new Path(newSections);
        } else {
            return new Path();
        }
    }

    public String getRoot() {
        return sections.getFirst();
    }

    public int size() {
        return sections.size();
    }

    public boolean isRoot() {
        return sections.size() == 1;
    }

    public Section getSection(int position) {
        return new Section(this.sections.get(position), position == this.sections.size() - 1);
    }

    @Override
    public Iterator<Section> iterator() {
        int size = sections.size();
        return sections.stream()
                .map(s -> new Section(s, sections.indexOf(s) == size - 1))
                .iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path path = (Path) o;

        if (!sections.equals(path.sections)) return false;
        return Objects.equals(attribute, path.attribute);
    }

    @Override
    public int hashCode() {
        int result = sections.hashCode();
        result = 31 * result + Objects.hashCode(attribute);
        return result;
    }

    @Override
    public String toString() {
        return "/" + String.join("/", sections) + (attribute == null ? "" : "[" + attribute + "]");
    }
}
