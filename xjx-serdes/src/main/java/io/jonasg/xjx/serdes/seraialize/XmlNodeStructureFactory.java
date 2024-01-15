package io.jonasg.xjx.serdes.seraialize;

import io.jonasg.xjx.serdes.Section;
import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.Path;
import io.jonasg.xjx.serdes.reflector.InstanceField;
import io.jonasg.xjx.serdes.reflector.Reflector;

public class XmlNodeStructureFactory {

    public <T> XmlNode build(T data) {
        return getXmlNode(Path.parse("/"), data, null);
    }

    private <T> XmlNode getXmlNode(Path parentPath, T data, XmlNode node) {
        if (data != null) {
            for (InstanceField field : Reflector.reflect(data)
                    .fields(f -> f.hasAnnotation(Tag.class))) {
                node = buildNodeForField(field, parentPath, node);
            }
        }
        return node;
    }

    private XmlNode buildNodeForField(InstanceField field, Path parentPath, XmlNode rootNode) {
        var tag = field.getAnnotation(Tag.class);
        var path = parentPath.append(Path.parse(tag.path()));
        if (rootNode == null) {
            rootNode = new XmlNode(path.getRoot());
        }
        var node = rootNode;
        for (int i = 1; i < path.size(); i++) {
            Section section = path.getSection(i);
            if (section.isLeaf()) {
                handleLeafNode(field, section, tag, node);
            } else {
                node = node.addNode(section.name());
            }
        }
        return getXmlNode(path, field.getValue(), rootNode);
    }

    private static void handleLeafNode(InstanceField field, Section section, Tag tag, XmlNode node) {
        if (!tag.attribute().isEmpty()) {
            node.addNode(section.name())
                    .addAttribute(tag.attribute(), field.getValue());
        } else {
            node.addValueNode(section.name(), field.getValue());
        }
    }

}
