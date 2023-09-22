package io.jonasg.xjx.scanners;

import io.jonasg.xjx.BufferedPositionedReader;
import io.jonasg.xjx.Token;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;

class CharacterScannerTest {

    @Test
    void shouldConsiderAllWhiteSpaceCharactersAsText() {
        // given
        var scanner = new CharacterScanner();
        var tokens = new ArrayList<Token<?>>();

        // when
        scanner.scan(new BufferedPositionedReader(new StringReader(" \n value1\n value2\n</a>")), tokens::add);

        // then
        Assertions.assertThat(tokens).containsExactly(new Token<>(Token.Type.CHARACTER_DATA, " \n value1\n value2\n"));
    }

}
