package io.jonasg.xjx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

public class BufferedPositionedReader implements PositionedReader {

    private final BufferedReader reader;

    private String currentLine;

    private int currentLinePos;

    private boolean hasMoreToRead = true;

    public BufferedPositionedReader(BufferedReader reader) {
        this.reader = reader;
        this.readNextLine();
    }

    public BufferedPositionedReader(Reader reader) {
        this(new BufferedReader(reader));
    }

    @Override
    public String currentLine() {
        if (currentLine != null && currentLinePos < currentLine.length()) {
            return currentLine.substring(currentLinePos);
        }
        return null;
    }

    private String readNextLine() {
        try {
            String readLine = reader.readLine();
            if (readLine != null) {
                currentLine = readLine;
                currentLinePos = 0;
            } else {
                hasMoreToRead = false;
                currentLine = null;
                currentLinePos = 0;
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return currentLine;
    }

    @Override
    public String peekLine() {
        if (currentLine == null) {
            return null;
        }
        if (currentLine.length() == currentLinePos) {
            readNextLine();
            if (currentLine == null) {
                return null;
            }
        }
        return currentLine.substring(currentLinePos);
    }

    @Override
    public Character readOneChar() {
        if (currentLine == null) {
            return null;
        }
        if (currentLine.length() == currentLinePos) {
            readNextLine();
            if (currentLine == null) {
                return null;
            }
            return '\n';
        }
        return currentLine.charAt(currentLinePos++);
    }

    @Override
    public char peekOneChar() {
        if (currentLine.length() == currentLinePos) {
            return '\n';
        }
        return currentLine.charAt(currentLinePos);

    }

    @Override
    public String readChars(int i) {
        currentLinePos += i;
        return currentLine.substring(currentLinePos - i, currentLinePos);
    }

    @Override
    public boolean hasMoreToRead() {
        if (currentLine.length() == currentLinePos) {
            currentLine = readNextLine();
        }
        return hasMoreToRead;
    }

    @Override
    public Optional<String> readUntil(String until) {
        if (currentLine == null) {
            return Optional.empty();
        }
        var currentLineUntilPos = currentLine.substring(currentLinePos);
        var indexOfUntil = currentLineUntilPos.indexOf(until);
        if (indexOfUntil == -1) {
            this.currentLinePos = currentLine.length();
            readNextLine();
            return readUntil(until)
                    .map(read -> currentLineUntilPos + "\n" + read);
        } else {
            var read = currentLineUntilPos.substring(0, indexOfUntil);
            this.currentLinePos = this.currentLinePos + indexOfUntil + until.length();
            return Optional.of(read);
        }
    }

    @Override
    public Character getCurrentChar() {
        if (currentLine == null) {
            return null;
        }
        return currentLine.charAt(currentLinePos - 1);
    }

    @Override
    public void ltrim() {
        if (currentLine == null) {
            return;
        }
        if (currentLine.length() == currentLinePos) {
            readNextLine();
            if (currentLine == null) {
                return;
            }
        }
        String replacedLine = removeLeadingWhitespace(currentLine());
        currentLinePos += (currentLine().length() - replacedLine.length());
    }

    public static String removeLeadingWhitespace(String input) {
        int length = input.length();
        int startIndex = 0;
        while (startIndex < length && Character.isWhitespace(input.charAt(startIndex))) {
            startIndex++;
        }
        return input.substring(startIndex);
    }


    @Override
    public String toString() {
        return currentLine.substring(0, currentLinePos) + "|" + currentLine.substring(currentLinePos);
    }
}
