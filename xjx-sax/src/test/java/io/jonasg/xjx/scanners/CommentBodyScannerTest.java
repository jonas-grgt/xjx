package io.jonasg.xjx.scanners;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import io.jonasg.xjx.BufferedPositionedReader;
import io.jonasg.xjx.Token;

class CommentBodyScannerTest {
    @Test
    void shouldEmit_closeCommentToken() {
        // given
        var scanner = new CommentBodyScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        scanner.scan(new BufferedPositionedReader(new StringReader("bla-->")), tokens::add);

        // then
        Assertions.assertThat(tokens).containsExactly(new Token<>(Token.Type.CLOSE_COMMENT, "bla"));
    }

    @Test
    void shouldEmit_closeCommentToken_forMultiLineComment() {
        // given
        var scanner = new CommentBodyScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        scanner.scan(new BufferedPositionedReader(new StringReader("bla\nbla\n\tbla-->")), tokens::add);

        // then
        Assertions.assertThat(tokens).containsExactly(new Token<>(Token.Type.CLOSE_COMMENT, "bla\nbla\n\tbla"));
    }

    @Test
    void shouldThrowException_whenNoCommentCloseTagIsPresent() {
        // given
        var scanner = new CommentBodyScanner();

        // when
        ThrowableAssert.ThrowingCallable act = () -> scanner.scan(new BufferedPositionedReader(new StringReader("bla\nbla\n\tbla")), a -> {});

        // then
        Assertions.assertThatThrownBy(act)
                .isInstanceOf(XmlParsingException.class)
                .hasMessage("Comment tag found without ending -->");
    }
}
