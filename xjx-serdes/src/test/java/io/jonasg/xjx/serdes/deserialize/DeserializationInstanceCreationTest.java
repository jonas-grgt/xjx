package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class DeserializationInstanceCreationTest {

    @Test
    void instantiateUsingDefaultConstructor() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Animal>
                    <type>dog</type>
                    <name>John</name>
                </Animal>
                """;

        // when
        AnimalWithPublicFields animal = new XjxSerdes().read(data, AnimalWithPublicFields.class);

        // then
        Assertions.assertThat(animal.type).isEqualTo("dog");
        Assertions.assertThat(animal.name).isEqualTo("John");
    }

    static class AnimalWithPublicFields {
        @Tag(path = "/Animal/type")
        String type;
        @Tag(path = "/Animal/name")
        String name;
    }

    @Test
    void instantiateUsingPrivateConstructor() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Animal>
                    <type>dog</type>
                    <name>John</name>
                </Animal>
                """;

        // when
        ClassWithPrivateConstructor animal = new XjxSerdes().read(data, ClassWithPrivateConstructor.class);

        // then
        Assertions.assertThat(animal.type).isEqualTo("dog");
        Assertions.assertThat(animal.name).isEqualTo("John");
    }

    static class ClassWithPrivateConstructor {
        @Tag(path = "/Animal/type")
        String type;
        @Tag(path = "/Animal/name")
        String name;

        private ClassWithPrivateConstructor() {
        }
    }
}
