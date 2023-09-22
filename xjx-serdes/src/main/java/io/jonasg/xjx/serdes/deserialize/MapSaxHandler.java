package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.sax.Attribute;
import io.jonasg.xjx.sax.SaxHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapSaxHandler implements SaxHandler {

    private final LinkedList<Map<String, Object>> mapsStack;

    private String characterData;

    private String prevStartTag;

    public MapSaxHandler(HashMap<String, Object> instance) {
        this.mapsStack = new LinkedList<>();
        this.mapsStack.add(instance);
    }

    @Override
    public void startDocument() {
    }

    @Override
    public void startTag(String namespace, String name, List<Attribute> attributes) {
        Map<String, Object> activeMap = this.mapsStack.getLast();
        Map<String, Object> newMap = new LinkedHashMap<>();
        activeMap.put(name, newMap);
        this.mapsStack.add(newMap);
        this.prevStartTag = name;
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
}
