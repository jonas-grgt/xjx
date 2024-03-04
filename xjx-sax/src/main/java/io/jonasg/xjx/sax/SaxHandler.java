package io.jonasg.xjx.sax;

import java.util.List;

/**
 * A handler for SAX parsing.
 */
public interface SaxHandler {

	/**
	 * Called when the document starts.
	 */
    void startDocument();

	/**
	 * Called when a start tag is encountered.
	 * @param namespace the namespace of the tag or null when no namespace is present
	 * @param name the name of the tag
	 * @param attributes the attributes of the tag
	 */
    void startTag(String namespace, String name, List<Attribute> attributes);

	/**
	 * Called when a end tag is encountered.
	 * @param namespace the namespace of the tag or null when no namespace is present
	 * @param name the name of the tag
	 */
    void endTag(String namespace, String name);

	/**
	 * Called when character data is encountered.
	 * @param data the character data
	 */
    void characters(String data);
}
