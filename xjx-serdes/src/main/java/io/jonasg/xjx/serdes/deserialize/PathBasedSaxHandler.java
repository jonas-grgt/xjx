package io.jonasg.xjx.serdes.deserialize;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.jonasg.xjx.sax.Attribute;
import io.jonasg.xjx.sax.SaxHandler;
import io.jonasg.xjx.serdes.Path;
import io.jonasg.xjx.serdes.deserialize.config.XjxConfiguration;

public class PathBasedSaxHandler<T> implements SaxHandler {

    private final Function<String, PathWriterIndex> indexSupplier;

	private final XjxConfiguration configuration;

	private final LinkedList<Object> objectInstances = new LinkedList<>();

    private String rootTag;

    private Path path;

    private PathWriterIndex pathWriterIndex;

    private String data;

    private SaxHandler mapRootSaxHandlerDelegate;

    private String mapStartTag;

    public PathBasedSaxHandler(Function<String, PathWriterIndex> indexSupplier, XjxConfiguration configuration) {
        this.indexSupplier = indexSupplier;
		this.configuration = configuration;
	}

    public PathBasedSaxHandler(Function<String, PathWriterIndex> indexSupplier, String rootTag, XjxConfiguration configuration) {
        this.indexSupplier = indexSupplier;
        this.rootTag = rootTag;
		this.configuration = configuration;
        handleRootTag(rootTag);
    }

    @Override
    public void startDocument() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void startTag(String namespace, String name, List<Attribute> attributes) {
        if (this.mapRootSaxHandlerDelegate != null) {
            this.mapRootSaxHandlerDelegate.startTag(namespace, name, attributes);
        }
        if (this.rootTag == null) {
            handleRootTag(name);
        } else {
            this.path = path.append(name);
            List<PathWriter> pathWriters = pathWriterIndex.get(path);
            if (pathWriters != null) {
				pathWriters.forEach(pathWriter -> {
					if (pathWriter.getObjectInitializer() != null) {
						Object object = pathWriter.getObjectInitializer().get();
						if (object instanceof Map) {
							this.mapRootSaxHandlerDelegate = new MapRootSaxHandler((HashMap<String, Object>) object);
							this.mapStartTag = name;
						} else if (object instanceof MapWithTypeInfo mapWithTypeInfo) {
							this.mapRootSaxHandlerDelegate = new TypedValueMapSaxHandler(mapWithTypeInfo, configuration);
							this.mapStartTag = name;
						}
						this.objectInstances.push(object);
					}
				});
            }
            attributes.forEach(a -> {
                List<PathWriter> attributeWriters = pathWriterIndex.get(path.appendAttribute(a.name()));
                if (attributeWriters != null) {
					attributeWriters.stream().forEach(attributeWriter -> attributeWriter.getValueInitializer().accept(a.value()));
                }
            });
        }
    }

    @Override
    public void endTag(String namespace, String name) {
        if (this.mapRootSaxHandlerDelegate != null) {
            if (name.equals(mapStartTag)) {
                this.mapRootSaxHandlerDelegate = null;
            } else {
                this.mapRootSaxHandlerDelegate.endTag(namespace, name);
            }
        }
        List<PathWriter> pathWriters = pathWriterIndex.get(path);
        if (pathWriters != null) {
			pathWriters.forEach(pathWriter -> {
				if (data != null) {
					pathWriter.getValueInitializer().accept(data);
				}
				if (pathWriter.getObjectInitializer() != null && !objectInstances.isEmpty() && objectInstances.size() != 1) {
					 if (pathWriter.getValueInitializer() != null) {
						 pathWriter.getValueInitializer().accept(objectInstances.peek());
					 }
					objectInstances.pop();
				}
			});
        }
        data = null;
        path = path.pop();
    }

    @Override
    public void characters(String data) {
        if (this.mapRootSaxHandlerDelegate != null) {
            this.mapRootSaxHandlerDelegate.characters(data);
        }
        this.data = data;
    }

    private void handleRootTag(String name) {
        this.pathWriterIndex = indexSupplier.apply(name);
        this.rootTag = name;
        path = Path.of(name);
        List<PathWriter> pathWriters = pathWriterIndex.get(path);
        if (pathWriters != null) {
			pathWriters.forEach(pathWriter -> {
				Object parent = pathWriter.getRootInitializer().get();
				if (parent instanceof MapAsRoot mapAsRoot) {
					this.mapRootSaxHandlerDelegate = new MapRootSaxHandler(mapAsRoot.map());
					this.objectInstances.push(mapAsRoot.root());
				} else {
					this.objectInstances.push(parent);
				}
			});
        }
    }

    @SuppressWarnings("unchecked")
    public T instance() {
		Object instance = objectInstances.pop();
		if (instance instanceof RecordWrapper recordWrapper) {
			return (T) recordWrapper.record();
		}
		return (T) instance;
    }
}
