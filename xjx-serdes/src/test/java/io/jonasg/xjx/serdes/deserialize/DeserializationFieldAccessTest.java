package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class DeserializationFieldAccessTest {

    @Test
    void accessThroughPublicField() {
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

    @Test
    void accessThroughPrivateField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Animal>
                    <test>blabal</test>
                    <type>dog</type>
                    <name>John</name>
                    <genus>
                        <family>Canidae</family>
                        <name>
                            <value>Canis</value>
                        </name>
                    </genus>
                </Animal>
                """;

        // when
        AnimalWithPrivateFields animal = new XjxSerdes().read(data, AnimalWithPrivateFields.class);

        // then
        Assertions.assertThat(animal.type).isEqualTo("dog");
        Assertions.assertThat(animal.name).isEqualTo("John");
        Assertions.assertThat(animal.genus.name.value).isEqualTo("Canis");
        Assertions.assertThat(animal.genus.family).isEqualTo("Canidae");
    }

    @Test
    void accessThroughSetter() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <Animal>
                        <type>dog</type>
                        <name>John</name>
                    </Animal>
                    """;

        // when
        AnimalWithPublicSetterFields animal = new XjxSerdes().read(data, AnimalWithPublicSetterFields.class);

        // then
        Assertions.assertThat(animal.type).isEqualTo("dog");
        Assertions.assertThat(animal.typeSetThroughSetter).isTrue();
        Assertions.assertThat(animal.name).isEqualTo("John");
        Assertions.assertThat(animal.nameSetThroughSetter).isTrue();
    }

    public static class AnimalWithPublicFields {
        public AnimalWithPublicFields() {
        }

        @Tag(path = "/Animal/name")
        String name;

        @Tag(path = "/Animal/type")
        String type;
    }

    public static class AnimalWithPrivateFields {
        public AnimalWithPrivateFields() {
        }

        @Tag(path = "/Animal/type")
        private String type;

        @Tag(path = "/Animal/name")
        private String name;

        private Genus genus;
    }

    public static class Genus {
        @Tag(path = "/Animal/genus/family")
        String family;

        GenusName name;
    }

    public static class GenusName {
        @Tag(path = "/Animal/genus/name/value")
        String value;
    }

    public static class AnimalWithPublicSetterFields {

        public AnimalWithPublicSetterFields() {
        }

        public boolean typeSetThroughSetter;

        public boolean nameSetThroughSetter;

        @Tag(path = "/Animal/type")
        private String type;

        @Tag(path = "/Animal/name")
        private String name;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.typeSetThroughSetter = true;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.nameSetThroughSetter = true;
            this.name = name;
        }
    }
}
