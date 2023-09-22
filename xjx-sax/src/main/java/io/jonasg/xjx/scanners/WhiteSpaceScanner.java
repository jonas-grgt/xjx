package io.jonasg.xjx.scanners;

import io.jonasg.xjx.PositionedReader;
import io.jonasg.xjx.TokenEmitter;

public class WhiteSpaceScanner implements Scanner {

    @Override
    public Scanner scan(PositionedReader reader, TokenEmitter tokenEmitter) {
        while (reader.hasMoreToRead() && Character.isWhitespace(reader.peekOneChar())) {
            reader.readOneChar();
        }
        return Scanner.nextScanner(reader);
    }
}
