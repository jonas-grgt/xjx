package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TagAttributeDeserializationTest {
    @Test
    void deserialize_StringFieldField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <String value="11"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        Assertions.assertThat(dataTypes.String).isEqualTo("11");
    }

    @Test
    void deserialize_IntegerFieldField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <Integer value="11"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        Assertions.assertThat(dataTypes.Integer).isEqualTo(11);
    }

    @Test
    void deserialize_LongFieldField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <Long value="11"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        Assertions.assertThat(dataTypes.Long).isEqualTo(11L);
    }

    @Test
    void deserialize_primitiveLongFieldField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <primitiveLong value="11"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        Assertions.assertThat(dataTypes.primitiveLong).isEqualTo(11L);
    }

    @Test
    void deserialize_BigDecimalFieldField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <BigDecimal value="11"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        Assertions.assertThat(dataTypes.BigDecimal).isEqualTo(BigDecimal.valueOf(11));
    }

    @Test
    void deserialize_DoubleField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <Double value="11"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        Assertions.assertThat(dataTypes.Double).isEqualTo(Double.valueOf(11));
    }

    @Test
    void deserialize_primitiveDoubleField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <primitiveDouble value="11"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        Assertions.assertThat(dataTypes.primitiveDouble).isEqualTo(Double.valueOf(11));
    }

    @Test
    void deserialize_multiCharString_toPrimitiveCharField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <multipleCharacters value="ABC"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        Assertions.assertThat(dataTypes.multipleCharacters).isEqualTo('A');
    }

    @Test
    void deserialize_primitiveCharField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <primitiveChar value="A"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        Assertions.assertThat(dataTypes.primitiveChar).isEqualTo('A');
    }

    @Test
    void deserialize_CharacterField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <Character value="A"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        Assertions.assertThat(dataTypes.Character).isEqualTo(Character.valueOf('A'));
    }

    public static class DataTypeHolder {
        public DataTypeHolder() {
        }
        @Tag(path = "/DataTypes/String", attribute = "value")
        String String;

        @Tag(path = "/DataTypes/Integer", attribute = "value")
        Integer Integer;

        @Tag(path = "/DataTypes/Long", attribute = "value")
        Long Long;

        @Tag(path = "/DataTypes/primitiveLong", attribute = "value")
        long primitiveLong;

        @Tag(path = "/DataTypes/BigDecimal", attribute = "value")
        BigDecimal BigDecimal;

        @Tag(path = "/DataTypes/Double", attribute = "value")
        Double Double;

        @Tag(path = "/DataTypes/primitiveDouble", attribute = "value")
        double primitiveDouble;

        @Tag(path = "/DataTypes/multipleCharacters", attribute = "value")
        char multipleCharacters;

        @Tag(path = "/DataTypes/primitiveChar", attribute = "value")
        char primitiveChar;

        @Tag(path = "/DataTypes/Character", attribute = "value")
        Character Character;
    }
}
