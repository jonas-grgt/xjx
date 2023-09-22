package io.jonasg.xjx.scanners;

import io.jonasg.xjx.PositionedReader;
import io.jonasg.xjx.Token;
import io.jonasg.xjx.TokenEmitter;

class CDATAScanner implements Scanner {

    private static final int CDATA_OPENING_TAG_LENGTH = 9;

    @Override
    public Scanner scan(PositionedReader reader, TokenEmitter tokenEmitter) {
        reader.readChars(CDATA_OPENING_TAG_LENGTH);
        var characters = reader.readUntil("]]>")
                .orElseThrow(() -> new XmlParsingException("CDATA tag found without closing ]]"));
        tokenEmitter.emit(new Token<>(Token.Type.CHARACTER_DATA, characters));
        return Scanner.nextScanner(reader);
    }

}
