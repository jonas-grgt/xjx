package io.jonasg.xjx.scanners;

import io.jonasg.xjx.Attributes;
import io.jonasg.xjx.PositionedReader;
import io.jonasg.xjx.StartTag;
import io.jonasg.xjx.Token;
import io.jonasg.xjx.TokenEmitter;

class StartTagScanner implements Scanner {

    @Override
    public Scanner scan(PositionedReader reader, TokenEmitter emitter) {
        char character;
        reader.ltrim();
        character = reader.readOneChar();
        if (character != '<') {
            throw new XmlParsingException("Start tag missing < in: '" + character + reader.currentLine() + "'");
        }
        var startTagName = tokenizeTag(reader);
        var currentChar = reader.getCurrentChar();
        Attributes attributes = Attributes.empty();
        if (currentChar != null && currentChar != Character.valueOf('>')) {
            attributes = tokenizeAttributes(reader);
        }
        emitter.emit(new Token<>(startTagName.type, startTag(startTagName.name, attributes)));
        return Scanner.nextScanner(reader);
    }

    private StartTag startTag(String name, Attributes attributes) {
        String actualName = name;
        String namespace = null;
        if (name.contains(":")) {
            var splitName = name.split(":");
            namespace = splitName[0];
            actualName = splitName[1];

        }
        return attributes.isPresent() ? new StartTag(actualName, namespace, attributes) : new StartTag(actualName, namespace);
    }

    private StartTagName tokenizeTag(PositionedReader reader) {
        StringBuilder tagNameBuilder = new StringBuilder();
        char character;
        Token.Type tokenType = Token.Type.START_TAG;
        while (reader.hasMoreToRead()) {
            character = reader.readOneChar();
            if (character == '>') {
                break;
            } else if (Character.isWhitespace(character)) {
                break;
            } else if (character == '/') {
                reader.readOneChar();
                tokenType = Token.Type.SELF_CLOSING_TAG;
                break;
            } else if (!Character.isWhitespace(character)) {
                tagNameBuilder.append(character);
            }
        }
        return new StartTagName(tagNameBuilder.toString(), tokenType);
    }

    private Attributes tokenizeAttributes(PositionedReader reader) {
        var attributes = new Attributes();
        var attributeNameBuilder = new StringBuilder();
        char character;
        while (reader.hasMoreToRead()) {
            character = reader.readOneChar();
            if (character == '>') {
                break;
            }
            if (character == '=') {
                String attributeName = attributeNameBuilder.toString();
                attributeNameBuilder.setLength(0);
                String attributeValue = readAttributeValue(reader);
                attributes.add(attributeName, attributeValue);
            } else {
                if (!Character.isWhitespace(character)) {
                    attributeNameBuilder.append(character);
                }
            }
        }
        return attributes;
    }

    private String readAttributeValue(PositionedReader reader) {
        StringBuilder valueBuilder = new StringBuilder();
        char quote = reader.readOneChar();
        char character;
        while (reader.hasMoreToRead()) {
            character = reader.readOneChar();
            if (character == quote) {
                break;
            } else {
                valueBuilder.append(character);
            }
        }
        return valueBuilder.toString();
    }

    private record StartTagName(String name, Token.Type type) {

    }
}
