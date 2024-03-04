package io.jonasg.xjx.scanners;

import static io.jonasg.xjx.Token.Type.CLOSE_COMMENT;
import io.jonasg.xjx.PositionedReader;
import io.jonasg.xjx.Token;
import io.jonasg.xjx.TokenEmitter;

class CommentBodyScanner implements Scanner {
    @Override
    public Scanner scan(PositionedReader reader, TokenEmitter tokenEmitter) {
        var commentBody = reader.readUntil("-->")
                .orElseThrow(() -> new XmlParsingException("Comment tag found without ending -->"));
        tokenEmitter.emit(new Token<>(CLOSE_COMMENT, commentBody));
        return Scanner.nextScanner(reader);
    }
}
