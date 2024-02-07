package io.jonasg.xjx.serdes.serialize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import io.jonasg.xjx.serdes.Path;
import io.jonasg.xjx.serdes.Section;
import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.reflector.InstanceField;
import io.jonasg.xjx.serdes.reflector.Reflector;

public class XmlNodeStructureFactory {

	public static final List<Class<?>> BASIC_TYPES = List.of(
			String.class, Integer.class, Boolean.class, boolean.class, Long.class, long.class, BigDecimal.class, Double.class,
			double.class, char.class, Character.class, LocalDate.class, LocalDateTime.class, ZonedDateTime.class, byte[].class);

	public <T> XmlNode build(T data) {
		return getXmlNode(Path.parse("/"), data, null);
	}

	private <T> XmlNode getXmlNode(Path parentPath, T data, XmlNode node) {
		if (data != null && !BASIC_TYPES.contains(data.getClass()) && !data.getClass().isEnum()) {
			List<InstanceField> fields = Reflector.reflect(data).fields();
			node = buildNodeForFields(parentPath, node, fields);
		}
		return node;
	}

	private XmlNode buildNodeForFields(Path parentPath, XmlNode node, List<InstanceField> fields) {
		for (InstanceField field : fields) {
			if (field.hasAnnotation(Tag.class)) {
				node = buildNodeForField(field, parentPath, node);
			}
			else if (!BASIC_TYPES.contains(field.type())) {
				return buildNodeForFields(parentPath, node, field.reflect().fields());
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
			}
			else {
				node = node.addNode(section.name());
			}
		}
		return getXmlNode(path, field.getValue(), rootNode);
	}

	private void handleLeafNode(InstanceField field, Section section, Tag tag, XmlNode node) {
		if (!tag.attribute().isEmpty()) {
			if (field.getValue() != null) {
				node.addNode(section.name())
						.addAttribute(tag.attribute(), field.getValue());
			}
		}
		else {
			node.addValueNode(section.name(), field.getValue());
		}
	}

}
