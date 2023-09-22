package io.jonasg.xjx.serdes.deserialize;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PathWriter {

    private Supplier<Object> initializer;

    private Supplier<Object> objectInitializer;

    private Consumer<Object> valueInitializer;

    public static PathWriter initializer(Supplier<Object> initializer) {
        PathWriter pathWriter = new PathWriter();
        pathWriter.initializer = initializer;
        return pathWriter;
    }

    public static PathWriter objectInitializer(Supplier<Object> objectInitializer) {
        PathWriter pathWriter = new PathWriter();
        pathWriter.objectInitializer = objectInitializer;
        return pathWriter;
    }

    public static PathWriter valueInitializer(Consumer<Object> o) {
        PathWriter pathWriter = new PathWriter();
        pathWriter.valueInitializer = o;
        return pathWriter;
    }

    public Supplier<Object> getInitializer() {
        return initializer;
    }

    public Supplier<Object> getObjectInitializer() {
        return objectInitializer;
    }

    public Consumer<Object> getValueInitializer() {
        return valueInitializer;
    }
}
