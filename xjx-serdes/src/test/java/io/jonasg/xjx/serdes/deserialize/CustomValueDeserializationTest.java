package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CustomValueDeserializationTest {

    @Test
    void useCustomValueDeserializerOnEnumTypes() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <WeatherData>
                    <Day>
                        <High>
                            <Value>78</Value>
                            <Unit>fahrenheit</Unit>
                        </High>
                        <Low>
                            <Value>62</Value>
                            <Unit>fahrenheit</Unit>
                        </Low>
                        <Precipitation>
                            <Value>10</Value>
                            <Unit>percentage</Unit>
                        </Precipitation>
                        <WeatherCondition>Partly Cloudy</WeatherCondition>
                    </Day>
                </WeatherData>
                """;

        // when
        EnumHolder enumHolder = new XjxSerdes().read(data, EnumHolder.class);

        // then
        Assertions.assertThat(enumHolder.dayHighUnit).isEqualTo(Unit.FAHRENHEIT);
        Assertions.assertThat(enumHolder.precipitationUnit).isEqualTo(Unit.PERCENTAGE);
    }

    static class EnumHolder {
        @Tag(path = "/WeatherData/Day/High/Unit")
        @ValueDeserialization(UnitDeserialize.class)
        Unit dayHighUnit;

        @Tag(path = "/WeatherData/Day/Precipitation/Unit")
        @ValueDeserialization(UnitDeserialize.class)
        Unit precipitationUnit;
    }

    enum Unit {
        FAHRENHEIT, PERCENTAGE
    }

    static class UnitDeserialize implements ValueDeserializer<Unit> {

        public UnitDeserialize() {
        }

        @Override
        public Unit deserializer(String value) {
            return Unit.valueOf(value.toUpperCase());
        }
    }

    @Test
    void useCustomValueDeserializerOnSimpleTypes() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <WeatherData>
                    <Day>
                        <High>
                            <Value>78</Value>
                            <Unit>celcius</Unit>
                        </High>
                        <Low>
                            <Value>62</Value>
                            <Unit>fahrenheit</Unit>
                        </Low>
                        <Precipitation>
                            <Value>10</Value>
                            <Unit>percentage</Unit>
                        </Precipitation>
                        <WeatherCondition>Partly Cloudy</WeatherCondition>
                    </Day>
                </WeatherData>
                """;

        // when
        SimpleTypeHolder simpleTypeHolder = new XjxSerdes().read(data, SimpleTypeHolder.class);

        // then
        Assertions.assertThat(simpleTypeHolder.maxTemperature).isEqualTo("78 °C");
    }

    @Test
    void useCustomValueDeserializerOnComplexType() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <WeatherData>
                    <Day>
                        <High>
                            <Value>78</Value>
                            <Unit>celcius</Unit>
                        </High>
                        <Low>
                            <Value>62</Value>
                            <Unit>fahrenheit</Unit>
                        </Low>
                        <Precipitation>
                            <Value>10</Value>
                            <Unit>percentage</Unit>
                        </Precipitation>
                        <WeatherCondition>Partly Cloudy</WeatherCondition>
                    </Day>
                </WeatherData>
                """;

        // when
        CustomTypeHolder customTypeHolder = new XjxSerdes().read(data, CustomTypeHolder.class);

        // then
        Assertions.assertThat(customTypeHolder.maxTemperatureAsLong).isEqualTo(new Temperature(78));
    }

    static class SimpleTypeHolder {
        @Tag(path = "/WeatherData/Day/High/Value")
        @ValueDeserialization(SimpleTemperatureDeserializer.class)
        String maxTemperature;
    }

    static class CustomTypeHolder {
        @Tag(path = "/WeatherData/Day/High/Value")
        @ValueDeserialization(CustomTemperatureDeserializer.class)
        Temperature maxTemperatureAsLong;
    }

    public static class SimpleTemperatureDeserializer implements ValueDeserializer<String> {
        @Override
        public String deserializer(String value) {
            return String.format("%s °C", value);
        }
    }

    public static class CustomTemperatureDeserializer implements ValueDeserializer<Temperature> {
        @Override
        public Temperature deserializer(String value) {
            return Temperature.fahrenheit(value);
        }
    }

    public static class Temperature {

        private final long value;

        private Temperature(long value) {
            this.value = value;
        }

        public static Temperature fahrenheit(String value) {
            return new Temperature(Long.parseLong(value));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Temperature that = (Temperature) o;

            return value == that.value;
        }

        @Override
        public int hashCode() {
            return (int) (value ^ (value >>> 32));
        }
    }

}
