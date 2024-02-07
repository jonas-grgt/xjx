package io.jonasg.xjx.serdes.deserialize;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.jonasg.xjx.sax.Attribute;
import io.jonasg.xjx.sax.SaxHandler;

public class MapRootSaxHandler implements SaxHandler {

    private final LinkedList<Map<String, Object>> mapsStack;

    private final boolean skipRootTag;

    private Map<String, Object> instance;

    private String characterData;

    private String prevStartTag;

    private String rootTag;

    public MapRootSaxHandler(Map<String, Object> instance) {
        this.mapsStack = new LinkedList<>();
        this.mapsStack.add(instance);
        this.skipRootTag = false;
        this.instance = instance;
    }

    public MapRootSaxHandler(HashMap<String, Object> instance, boolean skipRootTag) {
        this.mapsStack = new LinkedList<>();
        this.mapsStack.add(instance);
        this.skipRootTag = skipRootTag;
    }

    @Override
    public void startDocument() {
    }

    @Override
    public void startTag(String namespace, String name, List<Attribute> attributes) {
        if (this.rootTag != null || !skipRootTag) {
            Map<String, Object> activeMap = this.mapsStack.getLast();
            Map<String, Object> newMap = new LinkedHashMap<>();
            activeMap.put(name, newMap);
            this.mapsStack.add(newMap);
            this.prevStartTag = name;
        }
        if (this.rootTag == null) {
            this.rootTag = name;
        }
    }

    @Override
    public void endTag(String namespace, String name) {
        if (name.equals(this.prevStartTag)) {
            this.mapsStack.removeLast();
        }
        if (characterData != null) {
            Map<String, Object> currentMap = this.mapsStack.getLast();
            currentMap.put(name, characterData);
            this.characterData = null;
        } else {
            this.mapsStack.removeLast();
        }
    }

    @Override
    public void characters(String data) {
        this.characterData = data;
    }

    public Map<String, Object> instance() {
        return instance;
    }
}
