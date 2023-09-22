package io.jonasg.xjx.scanners;

import io.jonasg.xjx.PositionedReader;
import io.jonasg.xjx.TokenEmitter;

@FunctionalInterface
public interface Scanner {

    Scanner START_COMMENT_SCANNER = new StartCommentScanner();
    Scanner END_TAG_SCANNER = new EndTagScanner();
    Scanner START_TAG_SCANNER = new StartTagScanner();
    Scanner CDATA_SCANNER = new CDATAScanner();
    Scanner CHARACTER_SCANNER = new CharacterScanner();
    Scanner DOCUMENT_START_SCANNER = new DocumentTypeDeclarationScanner();
    Scanner WHITE_SPACE_SCANNER = new WhiteSpaceScanner();

    static Scanner nextScanner(PositionedReader reader) {
        var peekedLine = reader.peekLine();
        if (peekedLine == null) {
            return null;
        }
        if (!peekedLine.isEmpty()) {
            return scannerForLine(peekedLine);
        }
        return WHITE_SPACE_SCANNER;
    }

    private static Scanner scannerForLine(String peekedLine) {
        var cleanedLine = removeLeadingWhitespace(peekedLine);
        if (cleanedLine.startsWith("<!--")) {
            return START_COMMENT_SCANNER;
        }
        if (cleanedLine.startsWith("</")) {
            return END_TAG_SCANNER;
        }
        if (cleanedLine.startsWith("<?xml")) {
            return DOCUMENT_START_SCANNER;
        }
        if (cleanedLine.startsWith("<![CDATA[")) {
            return CDATA_SCANNER;
        }
        if (cleanedLine.startsWith("<")) {
            return START_TAG_SCANNER;
        }
        return CHARACTER_SCANNER;
    }

    private static String removeLeadingWhitespace(String input) {
        int length = input.length();
        int startIndex = 0;
        while (startIndex < length && Character.isWhitespace(input.charAt(startIndex))) {
            startIndex++;
        }
        if (startIndex == 0) {
            return input;
        }
        return input.substring(startIndex);
    }

    Scanner scan(PositionedReader reader, TokenEmitter tokenEmitter);

}
