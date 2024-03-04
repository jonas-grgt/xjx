package io.jonasg.xjx.sax;

import java.io.Reader;
import java.util.List;

import io.jonasg.xjx.EndTag;
import io.jonasg.xjx.StartTag;
import io.jonasg.xjx.Token;
import io.jonasg.xjx.Tokenizer;

public class SaxParser {

    private final Tokenizer tokenizer;

    public SaxParser() {
        this.tokenizer = new Tokenizer();
    }

    public void parse(Reader reader, SaxHandler saxHandler) {
        saxHandler.startDocument();
        tokenizer.tokenize(reader)
                .forEach(t -> handleToken(t, saxHandler));
    }

    private void handleToken(Token<?> token, SaxHandler saxHandler) {
        if (token.type().equals(Token.Type.START_TAG)) {
            var startTag = (StartTag)token.value();
            var attributes = getAttributes(startTag);
            saxHandler.startTag(startTag.namespace(), startTag.name(), attributes);
        } else if (token.type().equals(Token.Type.SELF_CLOSING_TAG)) {
            var startTag = (StartTag)token.value();
            var attributes = getAttributes(startTag);
            saxHandler.startTag(startTag.namespace(), startTag.name(), attributes);
            saxHandler.endTag(startTag.namespace(), startTag.name());
        } else if (token.type().equals(Token.Type.END_TAG)) {
            var endTag = (EndTag)token.value();
            saxHandler.endTag(endTag.namespace(), endTag.name());
        } else if (token.type().equals(Token.Type.CHARACTER_DATA)) {
            saxHandler.characters((String)token.value());
        }
    }

    private List<Attribute> getAttributes(StartTag startTagValue) {
        return startTagValue.attributes().stream()
                .map(a -> new Attribute(a.name(), a.value()))
                .toList();
    }
}
