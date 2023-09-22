package io.jonasg.xjx.sax;

import java.util.List;

public interface SaxHandler {

    void startDocument();

    void startTag(String namespace, String name, List<Attribute> attributes);

    void endTag(String namespace, String name);

    void characters(String data);
}
