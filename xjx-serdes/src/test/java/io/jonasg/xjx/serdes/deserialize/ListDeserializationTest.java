package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ListDeserializationTest {

    @Test
    void deserializeIntoListField_OfComplexType_ContainingTopLevelMapping() {
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
        Assertions.assertThat(weatherData.forecasts.get(0).maxTemperature).isEqualTo("71");
        Assertions.assertThat(weatherData.forecasts.get(1).maxTemperature).isEqualTo("78");
    }

    public static class WeatherData {
        public WeatherData() {
        }

        @Tag(path = "/WeatherData/Forecasts")
        List<Forecast> forecasts;
    }

    @Tag(path = "/WeatherData/Forecasts/Day")
    public static class Forecast {
        public Forecast() {
        }

        @Tag(path = "/WeatherData/Forecasts/Day/High/Value")
        String maxTemperature;
    }

    @Test
    void deserializeIntoListField_OfComplexType_ContainingComplexTypesWithCustomMapping() {
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
        Assertions.assertThat(precipitationData.precipitations.get(0).precipitationValue.value).isEqualTo("10");
        Assertions.assertThat(precipitationData.precipitations.get(1).precipitationValue.value).isEqualTo("12");
    }

    @Test
    void deserializeIntoListField_EvenIfNoneOfTheInnerMappedFieldOfComplexTypeMatch() {
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
        PrecipitationData precipitationData = new XjxSerdes().read(data, PrecipitationData.class);

        // then
        Assertions.assertThat(precipitationData.precipitations).hasSize(2);
    }

    @Test
    void listsMappedOntoSelfClosingTag_containsEmptyList() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <WeatherData>
                        <Forecasts/>
                    </WeatherData>
                    """;

        // when
        PrecipitationData precipitationData = new XjxSerdes().read(data, PrecipitationData.class);

        // then
        Assertions.assertThat(precipitationData.precipitations).isEmpty();
    }

    static class PrecipitationData {

        public PrecipitationData() {
        }

        @Tag(path = "/WeatherData/Forecasts")
        List<Precipitation> precipitations;

    }

    @Tag(path = "/WeatherData/Forecasts/Day")
    static class Precipitation {

        public Precipitation() {
        }

        @Tag(path = "/WeatherData/Forecasts/Day/Precipitation")
        PrecipitationValue precipitationValue;
    }

    static class PrecipitationValue {

        public PrecipitationValue() {
        }

        @Tag(path = "/WeatherData/Forecasts/Day/Precipitation/Value")
        String value;
    }

    @Test
    void informUserThatAList_itsGenericType_shouldBeAnnotatedWithTag() {
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
        ThrowableAssert.ThrowingCallable when = () -> new XjxSerdes().read(data, WeatherDataWithMissingTag.class);

        // then
        Assertions.assertThatThrownBy(when)
                .hasMessage("Generics of type List require @Tag pointing to mapped XML path (ForecastWithMissingTag)");
    }

    public static class WeatherDataWithMissingTag {
        public WeatherDataWithMissingTag() {
        }

        @Tag(path = "/WeatherData/Forecasts")
        List<ForecastWithMissingTag> forecasts;
    }

    public static class ForecastWithMissingTag {
        public ForecastWithMissingTag() {
        }

        @Tag(path = "/WeatherData/Forecasts/Day/High/Value")
        String maxTemperature;
    }
}
