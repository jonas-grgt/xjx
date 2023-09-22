package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.serdes.XjxSerdes;
import io.jonasg.xjx.serdes.Tag;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.Set;

public class SetDeserializationTest {
    @Test
    void deserializeIntoSetField_OfComplexType_ContainingTopLevelMapping() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <WeatherData>
                    <Forecasts>
                        <Day Date="2023-09-12">
                            <High>
                                <Value>71</Value>
                                <Unit>°F</Unit>
                            </High>
                            <Low>
                                <Value>60</Value>
                                <Unit>°F</Unit>
                            </Low>
                            <WeatherCondition>Partly Cloudy</WeatherCondition>
                        </Day>
                        
                        <Day Date="2023-09-13">
                            <High>
                                <Value>78</Value>
                                <Unit>°F</Unit>
                            </High>
                            <Low>
                                <Value>62</Value>
                                <Unit>°F</Unit>
                            </Low>
                            <WeatherCondition>Partly Cloudy</WeatherCondition>
                        </Day>
                    </Forecast>
                </WeatherData>
                """;

        // when
        WeatherData weatherData = new XjxSerdes().read(data, WeatherData.class);

        // then
        Assertions.assertThat(weatherData.forecasts).hasSize(2);
        Assertions.assertThat(weatherData.forecasts).isInstanceOf(Set.class);
        Assertions.assertThat(weatherData.forecasts).contains(new Forecast("71"));
        Assertions.assertThat(weatherData.forecasts).contains(new Forecast("78"));
    }

    public static class WeatherData {
        @Tag(path = "/WeatherData/Forecasts")
        Set<Forecast> forecasts;

        public WeatherData() {
        }
    }

    @Tag(path = "/WeatherData/Forecasts/Day")
    public static class Forecast {

        @Tag(path = "/WeatherData/Forecasts/Day/High/Value")
        String maxTemperature;

        public Forecast() {
        }

        public Forecast(String maxTemperature) {
            this.maxTemperature = maxTemperature;

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Forecast forecast = (Forecast) o;

            return Objects.equals(maxTemperature, forecast.maxTemperature);
        }

        @Override
        public int hashCode() {
            return maxTemperature != null ? maxTemperature.hashCode() : 0;
        }
    }

    @Test
    void deserializeIntoSetField_OfComplexType_ContainingComplexTypesWithCustomMapping() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <WeatherData>
                        <Forecasts>
                            <Day Date="2023-09-12">
                                <High>
                                    <Value>71</Value>
                                    <Unit>°F</Unit>
                                </High>
                                <Low>
                                    <Value>60</Value>
                                    <Unit>°F</Unit>
                                </Low>
                                <Precipitation>
                                    <Value>10</Value>
                                    <Unit>%</Unit>
                                </Precipitation>
                                <WeatherCondition>Partly Cloudy</WeatherCondition>
                            </Day>
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
                                    <Value>12</Value>
                                    <Unit>%</Unit>
                                </Precipitation>
                                <WeatherCondition>Partly Cloudy</WeatherCondition>
                            </Day>
                        </Forecast>
                    </WeatherData>
                    """;

        // when
        PrecipitationData precipitationData = new XjxSerdes().read(data, PrecipitationData.class);

        // then
        Assertions.assertThat(precipitationData.precipitations).hasSize(2);
        Assertions.assertThat(precipitationData.precipitations).isInstanceOf(Set.class);
        Assertions.assertThat(precipitationData.precipitations).contains(new Precipitation("10"));
        Assertions.assertThat(precipitationData.precipitations).contains(new Precipitation("12"));
    }

    static class PrecipitationData {

        public PrecipitationData() {
        }

        @Tag(path = "/WeatherData/Forecasts")
        Set<Precipitation> precipitations;

    }

    @Tag(path = "/WeatherData/Forecasts/Day")
    static class Precipitation {

        PrecipitationValue precipitationValue;

        public Precipitation() {
        }

        public Precipitation(String number) {
            this.precipitationValue = new PrecipitationValue(number);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Precipitation that = (Precipitation) o;

            return Objects.equals(precipitationValue, that.precipitationValue);
        }

        @Override
        public int hashCode() {
            return precipitationValue != null ? precipitationValue.hashCode() : 0;
        }
    }

    static class PrecipitationValue {

        @Tag(path = "/WeatherData/Forecasts/Day/Precipitation/Value")
        String value;

        public PrecipitationValue(String value) {
            this.value = value;
        }

        public PrecipitationValue() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PrecipitationValue that = (PrecipitationValue) o;

            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }
}
