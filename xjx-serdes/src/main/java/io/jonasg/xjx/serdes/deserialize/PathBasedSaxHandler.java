package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.sax.Attribute;
import io.jonasg.xjx.sax.SaxHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PathBasedSaxHandler<T> implements SaxHandler {

    private final Function<String, Map<Path, PathWriter>> indexSupplier;

    private final LinkedList<Object> objectInstances = new LinkedList<>();

    private String rootTag;

    private Path path;

    private Map<Path, PathWriter> pathWriterIndex;

    private String data;

    private MapSaxHandler mapSaxHandlerDelegate;

    private String mapStartTag;

    public PathBasedSaxHandler(Function<String, Map<Path, PathWriter>> indexSupplier) {
        this.indexSupplier = indexSupplier;
    }

    @Override
    public void startDocument() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void startTag(String namespace, String name, List<Attribute> attributes) {
        if (this.mapSaxHandlerDelegate != null) {
            this.mapSaxHandlerDelegate.startTag(namespace, name, attributes);
        } else {
            if (this.rootTag == null) {
                handleRootTag(name);
            } else {
                this.path = path.append(name);
                var pathWriter = pathWriterIndex.get(path);
                if (pathWriter != null) {
                    if (pathWriter.getObjectInitializer() != null) {
                        Object object = pathWriter.getObjectInitializer().get();
                        if (object instanceof Map) {
                            this.mapSaxHandlerDelegate = new MapSaxHandler((HashMap<String, Object>) object);
                            this.mapStartTag = name;
                        }
                        this.objectInstances.push(object);
                    }
                }
                attributes.forEach(a -> {
                    var attributeWriter = pathWriterIndex.get(path.appendAttribute(a.name()));
                    if (attributeWriter != null) {
                        attributeWriter.getValueInitializer().accept(a.value());
                    }
                });
            }
        }
    }

    @Override
    public void endTag(String namespace, String name) {
        if (this.mapSaxHandlerDelegate != null) {
            if (name.equals(mapStartTag)) {
                this.mapSaxHandlerDelegate = null;
            } else {
                this.mapSaxHandlerDelegate.endTag(namespace, name);
            }
        } else {
            PathWriter pathWriter = pathWriterIndex.get(path);
            if (pathWriter != null) {
                if (data != null) {
                    pathWriter.getValueInitializer().accept(data);
                }
                if (pathWriter.getObjectInitializer() != null && !objectInstances.isEmpty() && objectInstances.size() != 1) {
                    objectInstances.pop();
                }
            }
            data = null;
            path = path.pop();
        }
    }

    @Override
    public void characters(String data) {
        if (this.mapSaxHandlerDelegate != null) {
            this.mapSaxHandlerDelegate.characters(data);
        } else {
            this.data = data;
        }
    }

    private void handleRootTag(String name) {
        this.pathWriterIndex = indexSupplier.apply(name);
        this.rootTag = name;
        path = Path.of(name);
        PathWriter pathWriter = pathWriterIndex.get(path);
        if (pathWriter != null) {
            this.objectInstances.push(pathWriter.getInitializer().get());
        }
    }

    @SuppressWarnings("unchecked")
    public T instance() {
        return (T) objectInstances.pop();
    }
}
