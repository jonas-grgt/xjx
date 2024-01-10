package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

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
        assertThat(weatherData.forecasts).hasSize(2);
        assertThat(weatherData.forecasts.get(0).maxTemperature).isEqualTo("71");
        assertThat(weatherData.forecasts.get(1).maxTemperature).isEqualTo("78");
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
        assertThat(precipitationData.precipitations).hasSize(2);
        assertThat(precipitationData.precipitations.get(0).precipitationValue.value).isEqualTo("10");
        assertThat(precipitationData.precipitations.get(1).precipitationValue.value).isEqualTo("12");
    }

    @Test
    void deserializeIntoListField_EvenIfNoneOfTheInnerMappedFieldsOfComplexTypeCanBeMapped() {
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
        assertThat(precipitationData.precipitations).hasSize(2);
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
        assertThat(precipitationData.precipitations).isEmpty();
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
        assertThatThrownBy(when)
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

    @Test
    void deserializeIntoListField_OfComplexType_ContainingRelativeMappedField() {
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
        var weatherData = new XjxSerdes().read(data, WeatherDataRelativeMapping.class);

        // then
        assertThat(weatherData.forecasts).hasSize(2);
        assertThat(weatherData.forecasts.get(0).maxTemperature).isEqualTo("71");
        assertThat(weatherData.forecasts.get(1).maxTemperature).isEqualTo("78");
    }

    public static class WeatherDataRelativeMapping {
        public WeatherDataRelativeMapping() {
        }

        @Tag(path = "/WeatherData/Forecasts")
        List<ForecastRelativeMapping> forecasts;
    }

    @Tag(path = "/WeatherData/Forecasts/Day")
    public static class ForecastRelativeMapping {
        public ForecastRelativeMapping() {
        }

        @Tag(path = "High/Value")
        String maxTemperature;
    }

    @Test
    void deserializeIntoListField_OfComplexType_ContainingRelativeAndAbsoluteMappedField() {
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
        var weatherData = new XjxSerdes().read(data, WeatherDataRelativeAndAbsoluteMapping.class);

        // then
        assertThat(weatherData.forecasts).hasSize(2);
        assertThat(weatherData.forecasts.get(0).maxTemperature).isEqualTo("71");
        assertThat(weatherData.forecasts.get(0).minTemperature).isEqualTo("60");
        assertThat(weatherData.forecasts.get(1).maxTemperature).isEqualTo("78");
        assertThat(weatherData.forecasts.get(1).minTemperature).isEqualTo("62");
    }

    public static class WeatherDataRelativeAndAbsoluteMapping {
        public WeatherDataRelativeAndAbsoluteMapping() {
        }

        @Tag(path = "/WeatherData/Forecasts")
        List<ForecastRelativeAndAbsoluteMapping> forecasts;
    }

    @Tag(path = "/WeatherData/Forecasts/Day")
    public static class ForecastRelativeAndAbsoluteMapping {
        public ForecastRelativeAndAbsoluteMapping() {
        }

        @Tag(path = "High/Value")
        String maxTemperature;

        @Tag(path = "/WeatherData/Forecasts/Day/Low/Value")
        String minTemperature;
    }

    @Test
    void deserializeIntoListField_whereRootTagContainsRepeatedElements() {
        // given
        String xmlDoc = """
                <gpx
                        version="1.0"
                        creator="ExpertGPS 1.1 - https://www.topografix.com"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xmlns="http://www.topografix.com/GPX/1/0"
                        xsi:schemaLocation="http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd">
                    <wpt lat="42.438878" lon="-71.119277">
                        <ele>44.586548</ele>
                        <time>2001-11-28T21:05:28Z</time>
                        <name>5066</name>
                        <desc><![CDATA[5066]]></desc>
                        <sym>Crossing</sym>
                        <type><![CDATA[Crossing]]></type>
                    </wpt>
                    <wpt lat="42.439227" lon="-71.119689">
                        <ele>57.607200</ele>
                        <time>2001-06-02T03:26:55Z</time>
                        <name>5067</name>
                        <desc><![CDATA[5067]]></desc>
                        <sym>Dot</sym>
                        <type><![CDATA[Intersection]]></type>
                    </wpt>
                </gpx>""";

        // when
        var gpx = new XjxSerdes().read(xmlDoc, Gpx.class);

        // then
        assertThat(gpx.wayPoints).hasSize(2);
        assertThat(gpx.wayPoints.get(0).description).isEqualTo("5066");
        assertThat(gpx.wayPoints.get(0).time).isEqualTo("2001-11-28T21:05:28Z");
        assertThat(gpx.wayPoints.get(1).description).isEqualTo("5067");
        assertThat(gpx.wayPoints.get(1).time).isEqualTo("2001-06-02T03:26:55Z");
    }

    static class Gpx {
        public Gpx() {
        }

        @Tag(path = "/gpx")
        List<Wpt> wayPoints;
    }

    @Tag(path = "/gpx/wpt")
    static class Wpt {
        public Wpt() {
        }

        @Tag(path = "/gpx/wpt/desc")
        String description;

        @Tag(path = "time")
        String time;
    }
}
