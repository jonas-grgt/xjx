package io.jonasg.xjx.scanners;

import io.jonasg.xjx.PositionedReader;
import io.jonasg.xjx.Token;
import io.jonasg.xjx.TokenEmitter;

class DocumentTypeDeclarationScanner implements Scanner {
    @Override
    public Scanner scan(PositionedReader reader, TokenEmitter tokenEmitter) {
        var read = reader.readChars(5);
        if (!"<?xml".equals(read)) {
            throw new XmlParsingException("Document type declaration not declared correctly");
        }
        reader.readUntil("?>").orElseThrow(() -> new XmlParsingException("Document type declaration never closed"));
        tokenEmitter.emit(new Token<>(Token.Type.DOC_TYPE_DECLARATION));
        return Scanner.nextScanner(reader);
    }
}
