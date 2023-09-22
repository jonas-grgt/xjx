package io.jonasg.xjx.scanners;

import io.jonasg.xjx.EndTag;
import io.jonasg.xjx.PositionedReader;
import io.jonasg.xjx.Token;
import io.jonasg.xjx.TokenEmitter;

import java.util.Objects;

class EndTagScanner implements Scanner {

    @Override
    public Scanner scan(PositionedReader reader, TokenEmitter tokenEmitter) {
        reader.ltrim();
        String opening = reader.readChars(2);
        if (!Objects.equals(opening, "</")) {
            throw new XmlParsingException(String.format("End tag does not start with </ in: '%s", opening + reader.currentLine() + "'"));
        }
        var builder = new StringBuilder();
        while(reader.hasMoreToRead() && reader.peekOneChar() != '>') {
            builder.append(reader.readOneChar());
        }
        reader.readOneChar();
        var name = builder.toString();
        String actualName = name;
        String namespace = null;
        if (name.contains(":")) {
            var splitName = name.split(":");
            namespace = splitName[0];
            actualName = splitName[1];
        }
        tokenEmitter.emit(new Token<>(Token.Type.END_TAG, new EndTag(namespace, actualName)));
        return Scanner.nextScanner(reader);
    }

}
