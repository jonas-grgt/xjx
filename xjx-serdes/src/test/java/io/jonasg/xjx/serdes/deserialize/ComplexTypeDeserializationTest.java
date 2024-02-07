package io.jonasg.xjx.serdes.deserialize;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;

public class ComplexTypeDeserializationTest {
    @Test
    void mapFieldsOnComplexType() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <WeatherData>
                        <Location>
                            <City>New York</City>
                            <Country>
                                <Name>USA</Name>
                            </Country>
                        </Location>
                        <CurrentConditions>
                            <Temperature>
                                <Value>75</Value>
                                <Unit>°F</Unit>
                            </Temperature>
                            <Humidity>
                                <Value>60</Value>
                                <Unit>%</Unit>
                            </Humidity>
                            <WeatherCondition>Sunny</WeatherCondition>
                        </CurrentConditions>
                        <Forecast>
                            <Day Date="2023-09-13">
                                <High>
                                    <Value>78</Value>
                                    <Unit>°F</Unit>
                                </High>
                                <Low>
                                    <Value>62</Value>
                                    <Unit>°F</Unit>
                                </Low>
                                <Precipitation>
                                    <Value>10</Value>
                                    <Unit>%</Unit>
                                </Precipitation>
                                <WeatherCondition>Partly Cloudy</WeatherCondition>
                            </Day>
                        </Forecast>
                    </WeatherData>
                    """;

        // when
        WeatherData weatherData = new XjxSerdes().read(data, WeatherData.class);

        // then
        Assertions.assertThat(weatherData.location.city).isEqualTo("New York");
        Assertions.assertThat(weatherData.temperature).isEqualTo(75);
    }

    public static class WeatherData {
        public WeatherData() {
        }

        Location location;

        @Tag(path = "/WeatherData/CurrentConditions/Temperature/Value")
        Integer temperature;
    }

    public static class Location {
        public Location() {
        }

        @Tag(path = "/WeatherData/Location/City")
        String city;
    }

    @Test
    void mapFieldsIn_Nested_ComplexTypes() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <WeatherData>
                        <Location>
                            <City>New York</City>
                            <Country>
                                <Name>USA</Name>
                            </Country>
                        </Location>
                        <CurrentConditions>
                            <Temperature>
                                <Value>75</Value>
                                <Unit>°F</Unit>
                            </Temperature>
                            <Humidity>
                                <Value>60</Value>
                                <Unit>%</Unit>
                            </Humidity>
                            <WeatherCondition>Sunny</WeatherCondition>
                        </CurrentConditions>
                        <Forecast>
                            <Day Date="2023-09-13">
                                <High>
                                    <Value>78</Value>
                                    <Unit>°F</Unit>
                                </High>
                                <Low>
                                    <Value>62</Value>
                                    <Unit>°F</Unit>
                                </Low>
                                <Precipitation>
                                    <Value>10</Value>
                                    <Unit>%</Unit>
                                </Precipitation>
                                <WeatherCondition>Partly Cloudy</WeatherCondition>
                            </Day>
                        </Forecast>
                    </WeatherData>
                    """;

        // when
        WeatherDataMultiLevel weatherData = new XjxSerdes().read(data, WeatherDataMultiLevel.class);

        // then
        Assertions.assertThat(weatherData.yetAnotherLevel.location.city).isEqualTo("New York");
        Assertions.assertThat(weatherData.yetAnotherLevel.high).isEqualTo("78");
        Assertions.assertThat(weatherData.temperature).isEqualTo(75);
    }

    public static class WeatherDataMultiLevel {
        public WeatherDataMultiLevel() {
        }

        NestedComplexType yetAnotherLevel;

        @Tag(path = "/WeatherData/CurrentConditions/Temperature/Value")
        Integer temperature;
    }

    public static class NestedComplexType {
        LocationMultiLevel location;

        @Tag(path = "/WeatherData/Forecast/Day/High/Value")
        String high;
    }

    public static class LocationMultiLevel {
        public LocationMultiLevel() {
        }

        @Tag(path = "/WeatherData/Location/City")
        String city;
    }
}
