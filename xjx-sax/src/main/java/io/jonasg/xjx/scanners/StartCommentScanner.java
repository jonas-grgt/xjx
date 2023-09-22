package io.jonasg.xjx.scanners;

import io.jonasg.xjx.PositionedReader;
import io.jonasg.xjx.Token;
import io.jonasg.xjx.TokenEmitter;

class StartCommentScanner implements Scanner {

    @Override
    public Scanner scan(PositionedReader reader, TokenEmitter tokenEmitter) {
        reader.readChars(4);
        tokenEmitter.emit(new Token<>(Token.Type.START_COMMENT));
        return new CommentBodyScanner();
    }

}
