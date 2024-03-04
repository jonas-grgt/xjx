package io.jonasg.xjx.scanners;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.StringReader;
import java.util.ArrayList;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import io.jonasg.xjx.BufferedPositionedReader;
import io.jonasg.xjx.EndTag;
import io.jonasg.xjx.Token;

class EndTagScannerTest {


    @Test
    void shouldFail_whenInputDoesNotStartWithALessThenSignFollowedByASlash() {
        // given
        var endTagAction = new EndTagScanner();

        // when
        ThrowableAssert.ThrowingCallable act = () -> endTagAction.scan(new BufferedPositionedReader(new StringReader("home</a>")), t -> {});

        // then
        assertThatThrownBy(act)
                .isInstanceOf(XmlParsingException.class)
                .hasMessage("End tag does not start with </ in: 'home</a>'");
    }

    @Test
    void shouldEmit_endTag() {
        // given
        var endTagAction = new EndTagScanner();
        var tokens = new ArrayList<Token<?>>();

        // when
        endTagAction.scan(new BufferedPositionedReader(new StringReader("</a>")), tokens::add);

        // then
        assertThat(tokens).containsExactly(new Token<>(Token.Type.END_TAG, new EndTag(null, "a")));
    }


    @Test
    void shouldEmit_endTagWithNamespace() {
        // given
        var endTagAction = new EndTagScanner();
        var tokens = new ArrayList<Token<?>>();

        // when
        endTagAction.scan(new BufferedPositionedReader(new StringReader("</a:b>")), tokens::add);

        // then
        assertThat(tokens).containsExactly(new Token<>(Token.Type.END_TAG, new EndTag("a", "b")));
    }

}
