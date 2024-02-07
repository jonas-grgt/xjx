package io.jonasg.xjx.serdes.deserialize;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;

public class EnumDeserializationTest {


    @Test
    void mapToFieldOfTypeEnum_whenCharacterDataMatchesEnumConstantName() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <WeatherData>
                    <Day>
                        <High>
                            <Value>78</Value>
                            <Unit>FAHRENHEIT</Unit>
                        </High>
                        <Low>
                            <Value>62</Value>
                            <Unit>FAHRENHEIT</Unit>
                        </Low>
                        <Precipitation>
                            <Value>10</Value>
                            <Unit>PERCENTAGE</Unit>
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

    @Test
    void defaultToNullWhenValueCannotBeMappedToName() {
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
        Assertions.assertThat(enumHolder.dayHighUnit).isNull();
        Assertions.assertThat(enumHolder.precipitationUnit).isNull();
    }

    static class EnumHolder {
        @Tag(path = "/WeatherData/Day/High/Unit")
        Unit dayHighUnit;

        @Tag(path = "/WeatherData/Day/Precipitation/Unit")
        Unit precipitationUnit;
    }

    enum Unit {
        FAHRENHEIT, PERCENTAGE
    }
}
