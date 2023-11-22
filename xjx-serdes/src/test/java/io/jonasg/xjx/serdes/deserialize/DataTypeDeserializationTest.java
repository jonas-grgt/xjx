package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

public class DataTypeDeserializationTest {

    @Test
    void deserialize_StringField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <String>11</String>
                </DataTypes>
                """;

        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.String).isEqualTo("11");
    }


    @Test
    void deserialize_IntegerField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <Integer>11</Integer>
                </DataTypes>
                """;

        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.Integer).isEqualTo(11);
    }

    @Test
    void deserialize_LongField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <Long>12</Long>
                </DataTypes>
                """;

        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.Long).isEqualTo(Long.valueOf(12));
    }

    @Test
    void deserialize_longField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <primitiveLong>12</primitiveLong>
                </DataTypes>
                """;

        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.primitiveLong).isEqualTo(12L);
    }

    @Test
    void deserialize_DoubleField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <Double>5.7</Double>
                </DataTypes>
                """;

        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.Double).isEqualTo(Double.valueOf(5.7));
    }


    @Test
    void deserialize_doubleField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <primitiveDouble>7.7</primitiveDouble>
                </DataTypes>
                """;

        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.primitiveDouble).isEqualTo(7.7);
    }

    @Test
    void deserialize_BigDecimalField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <BigDecimal>4.7</BigDecimal>
                </DataTypes>
                """;

        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.BigDecimal).isEqualTo(BigDecimal.valueOf(4.7));
    }

    @Test
    void deserialize_Character() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <Character>A</Character>
                </DataTypes>
                """;

        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.Character).isEqualTo(Character.valueOf('A'));
    }

    @Test
    void deserialize_charField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <primitiveChar>A</primitiveChar>
                </DataTypes>
                """;

        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.primitiveChar).isEqualTo('A');
    }

    @Test
    void mapFirstCharacterOfMultiCharTagValue_into_charField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <multipleChar>CBA</multipleChar>
                </DataTypes>
                """;

        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.multipleChar).isEqualTo('C');
    }

    @Test
    void mapFirstCharacterOfMultiCharTagValue_into_CharacterField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <multipleCharacter>CBA</multipleCharacter>
                </DataTypes>
                """;

        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.multipleCharacter)
                .isInstanceOf(Character.class)
                .isEqualTo('C');
    }

    @Test
    void deserialize_LocalDateField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <LocalDate>1985-11-01</LocalDate>
                </DataTypes>
                """;

        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.LocalDate).isEqualTo(LocalDate.of(1985, 11, 1));
    }

    @Test
    void deserialize_LocalDateTimeField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <LocalDateTime>1985-11-01T19:12:10</LocalDateTime>
                </DataTypes>
                """;
        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.LocalDateTime).isEqualTo(LocalDateTime.of(1985, 11, 1, 19, 12, 10));
    }

    @Test
    void deserialize_ZonedDateTimeField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <ZonedDateTime>1985-11-01T19:12:10+01:00[Europe/Brussels]</ZonedDateTime>
                </DataTypes>
                """;
        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.ZonedDateTime)
                .isEqualTo(ZonedDateTime.of(LocalDateTime.of(1985, 11, 1, 19, 12, 10), ZoneId.of("Europe/Brussels")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"True","true","1","yes","YeS"})
    void deserializeTrueValuesFor_booleanField(String value) {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <BooleanTrue>%s</BooleanTrue>
                    <booleanTrue>%1$s</booleanTrue>
                </DataTypes>
                """.formatted(value);
        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.BooleanTrue).isTrue();
        Assertions.assertThat(dataTypes.booleanTrue).isTrue();
    }


    @ParameterizedTest
    @ValueSource(strings = {"False","false","0","no","No"})
    void deserializeFalseValuesFor_booleanField(String value) {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <BooleanFalse>%s</BooleanFalse>
                    <booleanFalse>%1$s</booleanFalse>
                </DataTypes>
                """.formatted(value);
        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.BooleanFalse).isFalse();
        Assertions.assertThat(dataTypes.booleanFalse).isFalse();
    }

    @Test
    void deserialize_MapField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <MapA>
                        <MapAB>
                            <MapC>Value1</MapC>
                            <MapD>Value2</MapD>
                        </MapAB>
                        <MapAC>
                            <MapC>Value3</MapC>
                            <MapD>Value4</MapD>
                        </MapAC>
                        <MapAD>
                            <MapC>
                                <MapD>Value5</MapD>
                            </MapC>
                        </MapAD>
                    </MapA>
                </DataTypes>
                """;
        // when
        DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

        // then
        Assertions.assertThat(dataTypes.map)
                .isEqualTo(Map.of(
                        "MapAB", Map.of("MapC", "Value1", "MapD", "Value2"),
                        "MapAC", Map.of("MapC", "Value3", "MapD", "Value4"),
                        "MapAD", Map.of("MapC", Map.of("MapD", "Value5"))));
    }

    static class DataTypes {

        @Tag(path = "/DataTypes/String")
        String String;

        @Tag(path = "/DataTypes/Integer")
        Integer Integer;

        @Tag(path = "/DataTypes/Long")
        Long Long;

        @Tag(path = "/DataTypes/primitiveLong")
        long primitiveLong;

        @Tag(path = "/DataTypes/BigDecimal")
        BigDecimal BigDecimal;

        @Tag(path = "/DataTypes/Double")
        Double Double;

        @Tag(path = "/DataTypes/primitiveDouble")
        double primitiveDouble;

        @Tag(path = "/DataTypes/multipleChar")
        char multipleChar;

        @Tag(path = "/DataTypes/primitiveChar")
        char primitiveChar;

        @Tag(path = "/DataTypes/Character")
        Character Character;

        @Tag(path = "/DataTypes/multipleCharacter")
        Character multipleCharacter;

        @Tag(path = "/DataTypes/LocalDate")
        LocalDate LocalDate;

        @Tag(path = "/DataTypes/LocalDateTime")
        LocalDateTime LocalDateTime;

        @Tag(path = "/DataTypes/ZonedDateTime")
        ZonedDateTime ZonedDateTime;

        @Tag(path = "/DataTypes/MapA")
        Map<String, Object> map;

        @Tag(path = "/DataTypes/BooleanTrue")
        Boolean BooleanTrue;

        @Tag(path = "/DataTypes/booleanTrue")
        boolean booleanTrue;

        @Tag(path = "/DataTypes/BooleanFalse")
        Boolean BooleanFalse = true;

        @Tag(path = "/DataTypes/booleanFalse")
        boolean booleanFalse = true;
    }
}
