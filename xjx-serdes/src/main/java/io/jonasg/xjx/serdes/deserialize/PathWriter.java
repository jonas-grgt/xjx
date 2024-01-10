package io.jonasg.xjx.serdes.deserialize;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PathWriter {

    private Supplier<Object> rootInitializer;

    private Supplier<Object> objectInitializer;

    private Consumer<Object> valueInitializer;

    public static PathWriter rootInitializer(Supplier<Object> rootInitializer) {
        PathWriter pathWriter = new PathWriter();
        pathWriter.rootInitializer = rootInitializer;
        return pathWriter;
    }

    public static PathWriter objectInitializer(Supplier<Object> objectInitializer) {
        PathWriter pathWriter = new PathWriter();
        pathWriter.objectInitializer = objectInitializer;
        return pathWriter;
    }

    public void setRootInitializer(Supplier<Object> rootInitializer) {
        this.rootInitializer = rootInitializer;
    }

    public static PathWriter valueInitializer(Consumer<Object> o) {
        PathWriter pathWriter = new PathWriter();
        pathWriter.valueInitializer = o;
        return pathWriter;
    }

    public Supplier<Object> getRootInitializer() {
        return rootInitializer;
    }

    public Supplier<Object> getObjectInitializer() {
        return objectInitializer;
    }

    public Consumer<Object> getValueInitializer() {
        return valueInitializer;
    }
}
