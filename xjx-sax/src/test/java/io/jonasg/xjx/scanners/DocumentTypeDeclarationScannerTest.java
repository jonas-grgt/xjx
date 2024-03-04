package io.jonasg.xjx.scanners;

import java.io.StringReader;
import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import io.jonasg.xjx.BufferedPositionedReader;
import io.jonasg.xjx.Token;

class DocumentTypeDeclarationScannerTest {

    @Test
    void shouldReadDocumentTypeDeclaration() {
        // given
        var scanner = new DocumentTypeDeclarationScanner();
        var tokens = new ArrayList<Token<?>>();

        // when
        scanner.scan(new BufferedPositionedReader(new StringReader("<?xml version=\"1.0\"?>")), tokens::add);

        // then
        Assertions.assertThat(tokens).containsExactly(new Token<>(Token.Type.DOC_TYPE_DECLARATION));
    }

    @Test
    void shouldThrowException_whenDeclaration_isNeverClosed() {
        // given
        var scanner = new DocumentTypeDeclarationScanner();

        // when
        ThrowableAssert.ThrowingCallable act = () -> scanner.scan(new BufferedPositionedReader(new StringReader("<?xml version=\"1.0\"")), t -> {});

        // then
        Assertions.assertThatThrownBy(act)
                .isInstanceOf(XmlParsingException.class)
                .hasMessage("Document type declaration never closed");
    }

    @Test
    void shouldThrowException_whenDeclaration_isNotOpened() {
        // given
        var scanner = new DocumentTypeDeclarationScanner();

        // when
        ThrowableAssert.ThrowingCallable act = () -> scanner.scan(new BufferedPositionedReader(new StringReader("version=\"1.0\"?>")), t -> {});

        // then
        Assertions.assertThatThrownBy(act)
                .isInstanceOf(XmlParsingException.class)
                .hasMessage("Document type declaration not declared correctly");
    }

}
