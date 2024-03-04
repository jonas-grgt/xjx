package io.jonasg.xjx.scanners;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.jonasg.xjx.BufferedPositionedReader;
import io.jonasg.xjx.Token;

class CDATAScannerTest {

    @Test
    void shouldReadCDATA_asText() {
        // given
        var scanner = new CDATAScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        scanner.scan(new BufferedPositionedReader(new StringReader("<![CDATA[<greeting>Hello, world!</greeting>]]>")), tokens::add);

        // then
        Assertions.assertThat(tokens).containsExactly(new Token<>(Token.Type.CHARACTER_DATA, "<greeting>Hello, world!</greeting>"));
    }

    @Test
    void shouldReadCDATA_upUntilAndIncludingANewLine() {
        // given
        var scanner = new CDATAScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        scanner.scan(new BufferedPositionedReader(new StringReader("<![CDATA[<greeting>Hello, world!</greeting>\n]]>")), tokens::add);

        // then
        Assertions.assertThat(tokens).containsExactly(new Token<>(Token.Type.CHARACTER_DATA, "<greeting>Hello, world!</greeting>\n"));
    }

    @Test
    void shouldReadCDATA_asText_spreadOVerMultipleLines() {
        // given
        var scanner = new CDATAScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        scanner.scan(new BufferedPositionedReader(new StringReader("<![CDATA[<greeting>Hello\n, world!\n</greeting>\n]]>")), tokens::add);

        // then
        Assertions.assertThat(tokens).containsExactly(new Token<>(Token.Type.CHARACTER_DATA, "<greeting>Hello\n, world!\n</greeting>\n"));
    }

}
