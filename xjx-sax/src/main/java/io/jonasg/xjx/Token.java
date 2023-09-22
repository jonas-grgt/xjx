package io.jonasg.xjx;

public record Token<T>(Type type, T value) {

    public Token(Type type) {
        this(type, null);
    }

    public enum Type {
        START_TAG,
        END_TAG,
        START_COMMENT,
        SELF_CLOSING_TAG,
        CHARACTER_DATA,
        DOC_TYPE_DECLARATION,
        CLOSE_COMMENT
    }

    @Override
    public String toString() {
        return String.format("%s = %s", type, value);
    }
}
