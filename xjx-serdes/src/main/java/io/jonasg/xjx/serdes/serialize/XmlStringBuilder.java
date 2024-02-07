package io.jonasg.xjx.serdes.serialize;

import java.util.List;

public class XmlStringBuilder {

    public String build(XmlNode nodes) {
        var sb = new StringBuilder();
        sb.append("<").append(nodes.name()).append(">\n");
        buildNodes(nodes.children(), sb);
        sb.append("</").append(nodes.name()).append(">\n");
        return sb.toString();
    }

    private void buildNodes(List<XmlNode> nodes, StringBuilder sb) {
        int indentationLevel = 1;
        buildNodes(nodes, sb, indentationLevel);
    }

    private void buildNodes(List<XmlNode> nodes, StringBuilder sb, int indentationLevel) {
        String indentation = "  ".repeat(indentationLevel);

        nodes.forEach(node -> {
            sb.append(indentation)
                    .append("<").append(node.name());

            if (node.hasAttributes()) {
                node.attributes().stream().forEach(attribute ->
                        sb.append(" ")
                                .append(attribute.name())
                                .append("=\"")
                                .append(attribute.value())
                                .append("\""));
            }

            if (node.hasChildren() || node.containsAValue()) {
                sb.append(">");
                if (node.containsAValue()) {
                    sb.append(node.value());
                }
                if (node.hasChildren()) {
                    sb.append("\n");
                    buildNodes(node.children(), sb, indentationLevel + 1);
                    sb.append(indentation);
                }
                sb.append("</").append(node.name()).append(">\n");
            } else {
                sb.append("/>\n");
            }
        });
    }

}
