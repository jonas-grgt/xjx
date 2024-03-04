package io.jonasg.xjx.serdes.deserialize;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;

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
        assertThat(dataTypes.String).isEqualTo("11");
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
		assertThat(dataTypes.Integer).isEqualTo(11);
	}

	@Test
	void deserialize_primitiveIntField() {
		// given
		String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <primitiveInt>11</primitiveInt>
                </DataTypes>
                """;

		// when
		DataTypes dataTypes = new XjxSerdes().read(data, DataTypes.class);

		// then
		assertThat(dataTypes.primitiveInt).isEqualTo(11);
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
        assertThat(dataTypes.Long).isEqualTo(Long.valueOf(12));
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
        assertThat(dataTypes.primitiveLong).isEqualTo(12L);
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
        assertThat(dataTypes.Double).isEqualTo(Double.valueOf(5.7));
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
        assertThat(dataTypes.primitiveDouble).isEqualTo(7.7);
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
        assertThat(dataTypes.BigDecimal).isEqualTo(BigDecimal.valueOf(4.7));
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
        assertThat(dataTypes.Character).isEqualTo(Character.valueOf('A'));
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
        assertThat(dataTypes.primitiveChar).isEqualTo('A');
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
        assertThat(dataTypes.multipleChar).isEqualTo('C');
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
        assertThat(dataTypes.multipleCharacter)
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
        assertThat(dataTypes.LocalDate).isEqualTo(LocalDate.of(1985, 11, 1));
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
        assertThat(dataTypes.LocalDateTime).isEqualTo(LocalDateTime.of(1985, 11, 1, 19, 12, 10));
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
        assertThat(dataTypes.ZonedDateTime)
                .isEqualTo(ZonedDateTime.of(LocalDateTime.of(1985, 11, 1, 19, 12, 10), ZoneId.of("Europe/Brussels")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"True", "true", "1", "yes", "YeS"})
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
        assertThat(dataTypes.BooleanTrue).isTrue();
        assertThat(dataTypes.booleanTrue).isTrue();
    }


    @ParameterizedTest
    @ValueSource(strings = {"False", "false", "0", "no", "No"})
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
        assertThat(dataTypes.BooleanFalse).isFalse();
        assertThat(dataTypes.booleanFalse).isFalse();
    }

    @Test
    void deserialize_SectionOfDocument_toMapField() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <MapB>
                        <MapAB>
                            <MapC>Value1</MapC>
                            <MapD>Value2</MapD>
                        </MapAB>
                    </MapB>
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
        assertThat(dataTypes.map)
                .isEqualTo(Map.of(
                        "MapAB", Map.of("MapC", "Value1", "MapD", "Value2"),
                        "MapAC", Map.of("MapC", "Value3", "MapD", "Value4"),
                        "MapAD", Map.of("MapC", Map.of("MapD", "Value5"))));
    }

    @Test
    void deserialize_wholeDocumentStartingFromTheRoot_toMapField() {
        // given
        String document = """
                <?xml version="1.0" encoding="UTF-8"?>
                <WeatherData>
                    <Location>
                        <City>New York</City>
                        <Country>USA</Country>
                    </Location>
                    <CurrentConditions>
                        <Temperature>
                            <Value>75</Value>
                            <Unit>°F</Unit>
                        </Temperature>
                        <WeatherCondition>Sunny</WeatherCondition>
                    </CurrentConditions>
                </WeatherData>""";


        // when
        var dataTypes = new XjxSerdes().read(document, DataTypes.class);

        // then
        assertThat(dataTypes.mapRoot)
                .isEqualTo(Map.of(
                        "Location", Map.of(
                                "City", "New York", "Country", "USA"),
                        "CurrentConditions", Map.of(
                                "Temperature", Map.of("Value", "75", "Unit", "°F"), "WeatherCondition", "Sunny")));
        // and is can map to other fields
        assertThat(dataTypes.city).isEqualTo("New York");
    }

    @Test
    void deserialize_wholeDocumentStartingFromTheRoot_toMapFieldWithCustomValueType() {
        // given
        String document = """
                <?xml version="1.0" encoding="UTF-8"?>
                <WeatherData>
                    <Location>
                        <City>New York</City>
                        <Country>USA</Country>
                    </Location>
                    <Measures>
                        <MeasureA>
                            <Value>75</Value>
                            <Unit>°F</Unit>
                        </MeasureA>
                        <MeasureB>
                            <Value>76</Value>
                            <Unit>°F</Unit>
                        </MeasureB>
                        <MeasureC>
                            <Value>77</Value>
                            <Unit>°F</Unit>
                        </MeasureC>
                    </Measures>
                </WeatherData>""";


        // when
        var dataTypes = new XjxSerdes().read(document, DataTypes.class);

        // then
        assertThat(dataTypes.mapCustomValue)
                .isEqualTo(Map.of(
                        "MeasureA", new Measure("75", "°F"),
                        "MeasureB", new Measure("76", "°F"),
                        "MeasureC", new Measure("77", "°F")));
    }

    @Test
    void deserialize_toMap() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
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
                        <MapD>
                            <MapDA>
                                <MapA>Value6</MapA>
                            </MapDA>
                        </MapD>
                    </MapAD>
                </DataTypes>
                """;
        // when
        Map<String, Object> map = new XjxSerdes().read(data, new MapOf<>() {
        });

        // then
        assertThat(map)
                .isEqualTo(Map.of(
                        "MapAB", Map.of("MapC", "Value1", "MapD", "Value2"),
                        "MapAC", Map.of("MapC", "Value3", "MapD", "Value4"),
                        "MapAD", Map.of(
                                "MapC", Map.of("MapD", "Value5"),
                                "MapD", Map.of("MapDA", Map.of("MapA", "Value6")))));
    }

    @Test
    @SuppressWarnings("unused")
    void deserialize_toMapOf_shouldOnlySupportStringAsKey() {
        ThrowableAssert.ThrowingCallable readWithNoneStringKey = () -> new XjxSerdes().read("", new MapOf<BigDecimal, Object>() {
        });

        assertThatThrownBy(readWithNoneStringKey)
                .isInstanceOf(XjxDeserializationException.class)
                .hasMessage("Maps only support String as key");
    }

    static class Measure {
        @Tag(path = "Value")
        private String temperature;

        @Tag(path = "Unit")
        private String unit;

        private Measure() {
        }

        public Measure(String temperature, String unit) {
            this.temperature = temperature;
            this.unit = unit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Measure measure = (Measure) o;

            if (!Objects.equals(temperature, measure.temperature))
                return false;
            return Objects.equals(unit, measure.unit);
        }

        @Override
        public int hashCode() {
            int result = temperature != null ? temperature.hashCode() : 0;
            result = 31 * result + (unit != null ? unit.hashCode() : 0);
            return result;
        }
    }

    static class DataTypes {

		@Tag(path = "/DataTypes/String")
        String String;

        @Tag(path = "/DataTypes/Integer")
        Integer Integer;

		@Tag(path = "/DataTypes/primitiveInt")
		int primitiveInt;

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

        @Tag(path = "/WeatherData")
        Map<String, Object> mapRoot;

        @Tag(path = "/WeatherData/Location/City")
        String city;

        @Tag(path = "/WeatherData/Measures")
        Map<String, Measure> mapCustomValue;

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
