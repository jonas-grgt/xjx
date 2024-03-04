package io.jonasg.xjx;

import java.io.StringReader;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BufferedPositionedReaderTest {

    @Nested
    class ReadOneCharTest {

        @Test
        void shouldMoveToNextLineIfAvailable() {
            // given
            var reader = new BufferedPositionedReader(new StringReader("t\na"));

            // when
            Character first = reader.readOneChar();
            Character second = reader.readOneChar();
            Character third = reader.readOneChar();

            // then
            Assertions.assertThat(first).isEqualTo('t');
            Assertions.assertThat(second).isEqualTo('\n');
            Assertions.assertThat(third).isEqualTo('a');
        }

        @Test
        void shouldReturnNull_whenReachedTheEndOfAvailableCharacters() {
            // given
            var reader = new BufferedPositionedReader(new StringReader("t\na"));

            // when
            reader.readOneChar();
            reader.readOneChar();
            reader.readOneChar();
            Character fourth = reader.readOneChar();
            Character fifth = reader.readOneChar();

            // then
            Assertions.assertThat(fourth).isNull();
            Assertions.assertThat(fifth).isNull();
        }
    }

    @Nested
    class PeelLineTest {

        @Test
        void shouldMoveToNextLineIfAvailable() {
            // given
            var reader = new BufferedPositionedReader(new StringReader("t\na"));

            // when
            reader.readOneChar();
            reader.readOneChar();
            var line = reader.peekLine();

            // then
            Assertions.assertThat(line).isEqualTo("a");
        }

        @Test
        void shouldReturnNull_whenReachedTheEndOfTheLastLine_andHaveToReadAgain_whenPeeking() {
            // given
            var reader = new BufferedPositionedReader(new StringReader("t\na"));

            // when
            reader.readOneChar();
            reader.readOneChar();
            reader.readOneChar();
            var line = reader.peekLine();

            // then
            Assertions.assertThat(line).isNull();
        }

        @Test
        void shouldReturnNull_whenReachedTheEndOfAllAvailableCharacters() {
            // given
            var reader = new BufferedPositionedReader(new StringReader("t\na"));

            // when
            reader.readOneChar();
            reader.readOneChar();
            reader.readOneChar();
            reader.readOneChar();
            var line = reader.peekLine();

            // then
            Assertions.assertThat(line).isNull();
        }
    }

    @Nested
    class ReadUntilTest {

        @Test
        void shouldReturnNull_ifUntilIsNotFound() {
            // given
            var reader = new BufferedPositionedReader(new StringReader("a\nb\nc\nd\ne"));

            // when
            var read = reader.readUntil("none-existent");

            // then
            Assertions.assertThat(read).isEmpty();
        }

        @Test
        void shouldReturnEverything_upToButNotIncluding_theGivenUntilValue() {
            // given
            var reader = new BufferedPositionedReader(new StringReader("a\nb\nc\nd\ne"));

            // when
            var read = reader.readUntil("c");

            // then
            Assertions.assertThat(read).isNotEmpty().contains("a\nb\n");
        }
    }

    @Nested
    class CurrentLineTest {

        @Test
        void shouldReturnCurrentLine() {
            // given
            var reader = new BufferedPositionedReader(new StringReader("a\nb\nc\nd\ne"));

            // when
            var read = reader.currentLine();

            // then
            Assertions.assertThat(read).isEqualTo("a");
        }

        @Test
        void shouldReturnNull_ifAtEndOfAvailableCharactersToRead() {
            // given
            var reader = new BufferedPositionedReader(new StringReader("a"));

            // when
            reader.readOneChar();
            reader.readOneChar();
            var read = reader.currentLine();

            // then
            Assertions.assertThat(read).isNull();
        }
    }

}
