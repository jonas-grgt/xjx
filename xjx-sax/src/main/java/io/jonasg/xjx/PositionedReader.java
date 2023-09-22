package io.jonasg.xjx;

import java.util.Optional;

public interface PositionedReader {
    String currentLine();

    String peekLine();

    Character readOneChar();

    char peekOneChar();

    String readChars(int i);

    boolean hasMoreToRead();

    Optional<String> readUntil(String until);

    Character getCurrentChar();

    void ltrim();
}
