package io.jonasg.xjx.serdes.deserialize;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;
import io.jonasg.xjx.serdes.deserialize.ListDeserializationTest.Gpx;
import io.jonasg.xjx.serdes.deserialize.ListDeserializationTest.Wpt;

public class SetDeserializationTest {

    @Test
    void deserializeIntoListField_OfStringType() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Data>
                    <Strings>
                        <String>2023-09-12</String>
                        <String>2023-09-13</String>
                        <String>2023-09-14</String>
                        <String>2023-09-15</String>
                    </Strings>
                </Data>
                """;

        // when
        var dataHolder = new XjxSerdes().read(data, ListOfStrings.class);

        // then
        assertThat(dataHolder.strings).containsExactlyInAnyOrder(
                "2023-09-12",
                "2023-09-13",
                "2023-09-14",
                "2023-09-15"
        );
    }

    static class ListOfStrings {
        @Tag(path = "/Data/Strings", items = "String")
        Set<String> strings;
    }


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
        @Tag(path = "/WeatherData/Forecasts", items = "Day")
        Set<Forecast> forecasts;

        public WeatherData() {
        }
    }

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

        @Tag(path = "/WeatherData/Forecasts", items = "Day")
        Set<Precipitation> precipitations;

    }

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

    @Test
    void informUserThatFieldOfTypeSet_shouldHaveItemsFilledIn() {
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
                .hasMessage("Field (ForecastWithMissingTag) requires @Tag to have items parameter describing the tag name of a single repeated tag");
    }

    public static class WeatherDataWithMissingTag {
        public WeatherDataWithMissingTag() {
        }

        @Tag(path = "/WeatherData/Forecasts")
        Set<ForecastWithMissingTag> forecasts;
    }

    public static class ForecastWithMissingTag {
        public ForecastWithMissingTag() {
        }

        @Tag(path = "/WeatherData/Forecasts/Day/High/Value")
        String maxTemperature;
    }


    @Test
    void deserializeIntoSetField_OfComplexType_ContainingRelativeMappedField() {
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
        Assertions.assertThat(weatherData.forecasts).hasSize(2);
        Assertions.assertThat(weatherData.forecasts).extracting(r -> r.maxTemperature).containsExactlyInAnyOrder("78", "71");
    }

    public static class WeatherDataRelativeMapping {
        public WeatherDataRelativeMapping() {
        }

        @Tag(path = "/WeatherData/Forecasts", items = "Day")
        Set<ForecastRelativeMapping> forecasts;
    }

    @Tag(path = "/WeatherData/Forecasts/Day")
    public static class ForecastRelativeMapping {
        public ForecastRelativeMapping() {
        }

        @Tag(path = "High/Value")
        String maxTemperature;
    }

    @Test
    void deserializeIntoSetField_OfComplexType_ContainingRelativeAndAbsoluteMappedField() {
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
        Assertions.assertThat(weatherData.forecasts).hasSize(2);
        Assertions.assertThat(weatherData.forecasts).extracting(r -> r.maxTemperature).containsExactlyInAnyOrder("78", "71");
        Assertions.assertThat(weatherData.forecasts).extracting(r -> r.minTemperature).containsExactlyInAnyOrder("62", "60");
    }

    public static class WeatherDataRelativeAndAbsoluteMapping {
        public WeatherDataRelativeAndAbsoluteMapping() {
        }

        @Tag(path = "/WeatherData/Forecasts", items = "Day")
        Set<ForecastRelativeAndAbsoluteMapping> forecasts;
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
    void deserializeIntoSetField_whereRootTagContainsRepeatedElements() {
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
        Assertions.assertThat(gpx.wayPoints).extracting(r -> r.description).containsExactlyInAnyOrder("5066", "5067");
        Assertions.assertThat(gpx.wayPoints).extracting(r -> r.time).containsExactlyInAnyOrder("2001-11-28T21:05:28Z", "2001-06-02T03:26:55Z");
    }


	@Test
	void recordWithSetOfRecord() {
		// given
		String data = """
				<?xml version="1.0" encoding="UTF-8"?>
				<WeatherReport>
				  <City>
				    <Name>New York</Name>
				    <Condition>Sunny</Condition>
				  </City>
				  <City>
				    <Name>London</Name>
				    <Condition>Cloudy</Condition>
				  </City>
				</WeatherReport>
				""";
		record CityWeather(@Tag(path = "Condition") String condition,
						   @Tag(path = "Name") String name) {}

		record WeatherReport(
				@Tag(path = "/WeatherReport", items = "City") Set<CityWeather> cityWeather) {}


		// when
		var weatherReport = new XjxSerdes().read(data, WeatherReport.class);

		// then
		assertThat(weatherReport.cityWeather).containsExactly(
				new CityWeather("Cloudy", "London"),
				new CityWeather("Sunny", "New York"));
	}

	static class Gpx {
        public Gpx() {
        }

        @Tag(path = "/gpx", items = "wpt")
        Set<Wpt> wayPoints;
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
