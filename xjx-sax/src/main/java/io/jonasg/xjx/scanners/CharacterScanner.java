package io.jonasg.xjx.scanners;

import io.jonasg.xjx.PositionedReader;
import io.jonasg.xjx.Token;
import io.jonasg.xjx.TokenEmitter;

class CharacterScanner implements Scanner {
    @Override
    public Scanner scan(PositionedReader reader, TokenEmitter tokenEmitter) {
        char character = reader.peekOneChar();
        var builder = new StringBuilder();
        while (character != '<') {
            builder.append(reader.readOneChar());
            character = reader.peekOneChar();
        }
        tokenEmitter.emit(new Token<>(Token.Type.CHARACTER_DATA, builder.toString()));
        return Scanner.nextScanner(reader);
    }
}
