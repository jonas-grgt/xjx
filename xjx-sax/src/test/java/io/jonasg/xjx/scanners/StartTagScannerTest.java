package io.jonasg.xjx.scanners;

import io.jonasg.xjx.Attributes;
import io.jonasg.xjx.BufferedPositionedReader;
import io.jonasg.xjx.StartTag;
import io.jonasg.xjx.Token;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

class StartTagScannerTest {

    @Test
    void shouldFail_whenInputDoesNotStartWithALessThenSign() {
        // given
        var startTagAction = new StartTagScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        ThrowableAssert.ThrowingCallable act = () -> startTagAction.scan(new BufferedPositionedReader(new StringReader("a>home</a>")), tokens::add);

        // then
        Assertions.assertThatThrownBy(act)
                .isInstanceOf(XmlParsingException.class)
                .hasMessage("Start tag missing < in: 'a>home</a>'");
    }

    @Test
    void shouldEmit_startTag() {
        // given
        var startTagAction = new StartTagScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        startTagAction.scan(new BufferedPositionedReader(new StringReader("<a >home</a>")), tokens::add);

        // then
        Assertions.assertThat(tokens).containsExactly(new Token<>(Token.Type.START_TAG, new StartTag("a")));
    }


    @Test
    void shouldEmit_namespacedStartTag() {
        // given
        var startTagAction = new StartTagScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        startTagAction.scan(new BufferedPositionedReader(new StringReader("<bk:a >home</bk:a>")), tokens::add);

        // then
        Assertions.assertThat(tokens).containsExactly(new Token<>(Token.Type.START_TAG, new StartTag("a", "bk")));
    }

    @Test
    void shouldEmit_startTag_spreadOverMultipleLines() {
        // given
        var startTagAction = new StartTagScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        startTagAction.scan(new BufferedPositionedReader(new StringReader("<a \n >home</a>")), tokens::add);

        // then
        Assertions.assertThat(tokens).containsExactly(new Token<>(Token.Type.START_TAG, new StartTag("a")));
    }

    @Test
    void shouldEmit_singleNamespaceDeclarationAttribute() {
        // given
        var startTagAction = new StartTagScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        startTagAction.scan(new BufferedPositionedReader(new StringReader("<a xmlns:bk=\"urn:example.com\" >home</a>")), tokens::add);

        // then
        Assertions.assertThat(tokens)
                .containsExactly(new Token<>(Token.Type.START_TAG, new StartTag("a", new Attributes("xmlns:bk", "urn:example.com"))));
    }

    @Test
    void shouldEmit_singleAttribute_onOneLine() {
        // given
        var startTagAction = new StartTagScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        startTagAction.scan(new BufferedPositionedReader(new StringReader("<a href=\"home\" >home</a>")), tokens::add);

        // then
        Assertions.assertThat(tokens)
                .containsExactly(new Token<>(Token.Type.START_TAG, new StartTag("a", new Attributes("href", "home"))));
    }

    @Test
    void shouldEmit_singleAttribute_whereFirstAttributeIsPrefixedWithNewLinesAndTab() {
        // given
        var startTagAction = new StartTagScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        startTagAction.scan(new BufferedPositionedReader(new StringReader("<a \n\t href=\"home\" >home</a>")), tokens::add);

        // then
        Assertions.assertThat(tokens)
                .containsExactly(new Token<>(Token.Type.START_TAG, new StartTag("a", new Attributes("href", "home"))));
    }

    @Test
    void shouldEmit_multipleAttributes_onOneLine() {
        // given
        var startTagAction = new StartTagScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        startTagAction.scan(new BufferedPositionedReader(new StringReader("<a href=\"home\" target=\"_blank\">home</a>")), tokens::add);

        // then
        Assertions.assertThat(tokens)
                .containsExactly(new Token<>(Token.Type.START_TAG, new StartTag("a", new Attributes("href", "home", "target", "_blank"))));
    }

    @Test
    void shouldEmit_multipleAttributes_whereAllAttributeArePrefixedWithNewLinesAndTabs() {
        // given
        var startTagAction = new StartTagScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        startTagAction.scan(new BufferedPositionedReader(new StringReader("<a \n\thref=\"home\" \n\ttarget=\"_blank\">home</a>")), tokens::add);

        // then
        Assertions.assertThat(tokens)
                .containsExactly(new Token<>(Token.Type.START_TAG, new StartTag("a", new Attributes("href", "home", "target", "_blank"))));
    }

    @Test
    void shouldEmit_selfClosingTag() {
        // given
        var startTagAction = new StartTagScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        startTagAction.scan(new BufferedPositionedReader(new StringReader("<a/><b>c</b>")), tokens::add);

        // then
        Assertions.assertThat(tokens).containsExactly(new Token<>(Token.Type.SELF_CLOSING_TAG, new StartTag("a")));
    }


    @Test
    void shouldEmit_selfClosingTagWithNamespace() {
        // given
        var startTagAction = new StartTagScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        startTagAction.scan(new BufferedPositionedReader(new StringReader("<a:foo/><b>c</b>")), tokens::add);

        // then
        Assertions.assertThat(tokens).containsExactly(new Token<>(Token.Type.SELF_CLOSING_TAG, new StartTag("foo", "a")));
    }

    @Test
    void shouldEmit_selfClosingTag_withAttributes() {
        // given
        var startTagAction = new StartTagScanner();
        List<Token<?>> tokens = new ArrayList<>();

        // when
        startTagAction.scan(new BufferedPositionedReader(new StringReader("<a href=\"home\" target=\"_blank\"/>")), tokens::add);

        // then
        Assertions.assertThat(tokens)
                .containsExactly(new Token<>(Token.Type.START_TAG, new StartTag("a", new Attributes("href", "home", "target", "_blank"))));
    }
}
