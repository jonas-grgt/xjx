package io.jonasg.xjx;

import java.io.Reader;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jonasg.xjx.scanners.Scanner;
import io.jonasg.xjx.scanners.WhiteSpaceScanner;

/**
 * Tokenize an XML document into a stream of {@link Token}.
 */
public class Tokenizer {

    public Stream<Token<?>> tokenize(Reader reader) {
        return StreamSupport.stream(new TokenSpliterator(reader), false);
    }

    static class TokenSpliterator implements Spliterator<Token<?>> {

        private final BufferedPositionedReader reader;

        private Scanner scanner;

        public TokenSpliterator(Reader reader) {
            this.scanner = new WhiteSpaceScanner();
            this.reader = new BufferedPositionedReader(reader);
        }

        @Override
        public boolean tryAdvance(Consumer<? super Token<?>> action) {
            if (scanner == null) {
                return false;
            }
            scanner = scanner.scan(reader, action::accept);
            return true;
        }

        @Override
        public Spliterator<Token<?>> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return ORDERED;
        }
    }

}
