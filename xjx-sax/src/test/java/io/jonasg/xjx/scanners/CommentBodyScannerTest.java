package io.jonasg.xjx.scanners;

import io.jonasg.xjx.BufferedPositionedReader;
import io.jonasg.xjx.Token;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

class CommentBodyScannerTest {
    @Test
    void shouldReadCDATA_asText() {
        // given
        var scanner = new CommentBodyScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        scanner.scan(new BufferedPositionedReader(new StringReader("bla\n-->")), tokens::add);

        // then
        Assertions.assertThat(tokens).containsExactly(new Token<>(Token.Type.CLOSE_COMMENT, "bla\n"));
    }
}
