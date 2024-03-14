package io.jonasg.xjx.serdes.deserialize;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;

@SuppressWarnings("unused")
public class ListDeserializationTest {

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
        List<String> strings;
    }

    @Test
    void deserializeIntoListField_OfBooleanType() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Data>
                    <Booleans>
                        <Boolean>True</Boolean>
                        <Boolean>true</Boolean>
                        <Boolean>yes</Boolean>
                        <Boolean>YeS</Boolean>
                        <Boolean>1</Boolean>
                    </Booleans>
                </Data>
                """;

        // when
        var weatherData = new XjxSerdes().read(data, ListOfBooleans.class);

        // then
        assertThat(weatherData.booleans).containsOnly(Boolean.TRUE);
    }

    static class ListOfBooleans {
        @Tag(path = "/Data/Booleans", items = "Boolean")
        List<Boolean> booleans;
    }


    @Test
    void deserializeIntoListField_OfLongType() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Data>
                    <Longs>
                        <Long>123456789</Long>
                        <Long>-987654321</Long>
                        <Long>0</Long>
                    </Longs>
                </Data>
                """;

        // when
        var listOfLongs = new XjxSerdes().read(data, ListOfLongs.class);

        // then
        assertThat(listOfLongs.longs).containsExactly(123456789L, -987654321L, 0L);
    }

    static class ListOfLongs {
        @Tag(path = "/Data/Longs", items = "Long")
        List<Long> longs;
    }

    @Test
    void deserializeIntoListField_OfDoubleType() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Data>
                    <Doubles>
                        <Double>3.14</Double>
                        <Double>-2.5</Double>
                        <Double>0.0</Double>
                    </Doubles>
                </Data>
                """;

        // when
        var listOfDoubles = new XjxSerdes().read(data, ListOfDoubles.class);

        // then
        assertThat(listOfDoubles.doubles).containsExactly(3.14, -2.5, 0.0);
    }

    static class ListOfDoubles {
        @Tag(path = "/Data/Doubles", items = "Double")
        List<Double> doubles;
    }


    @Test
    void deserializeIntoListField_OfLocalDate() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Data>
                    <LocalDates>
                        <LocalDate>2024-01-01</LocalDate>
                        <LocalDate>2024-02-01</LocalDate>
                        <LocalDate>2024-03-01</LocalDate>
                    </LocalDates>
                </Data>
                """;

        // when
        var listOfDates = new XjxSerdes().read(data, ListOfDates.class);

        // then
        assertThat(listOfDates.dates).containsExactly(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 3, 1)
        );
    }

    static class ListOfDates {
        @Tag(path = "/Data/LocalDates", items = "LocalDate")
        List<LocalDate> dates;
    }

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

        @Tag(path = "/WeatherData/Forecasts", items = "Day")
        List<Forecast> forecasts;
    }

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

        @Tag(path = "/WeatherData/Forecasts", items = "Day")
        List<Precipitation> precipitations;

    }

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
    void informUserThatAMappedListField_shouldHaveAFilledInItemsParameter() {
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
                .hasMessage("Field (ForecastWithMissingTag) requires @Tag to have items parameter describing the tag name of a single repeated tag");
    }

    @SuppressWarnings("unused")
    public static class WeatherDataWithMissingTag {
        public WeatherDataWithMissingTag() {
        }

        @Tag(path = "/WeatherData/Forecasts")
        List<ForecastWithMissingTag> forecasts;
    }

    @SuppressWarnings("unused")
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

        @Tag(path = "/WeatherData/Forecasts", items = "Day")
        List<ForecastRelativeMapping> forecasts;
    }

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

        @Tag(path = "/WeatherData/Forecasts", items = "Day")
        List<ForecastRelativeAndAbsoluteMapping> forecasts;
    }

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

	@Test
	void recordWithListOfRecord() {
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
		record CityWeather(@Tag(path = "Condition") String condition, @Tag(path = "Name") String name) {}

		record WeatherReport(@Tag(path = "/WeatherReport", items = "City") List<CityWeather> cityWeather) {}


		// when
		var weatherReport = new XjxSerdes().read(data, WeatherReport.class);

		// then
		assertThat(weatherReport.cityWeather).containsExactly(
				new CityWeather("Sunny", "New York"),
				new CityWeather("Cloudy", "London"));
	}

	@Nested
	class RepeatedTagsMappedToMultipleListsTest {

		@Test
		void wrappedInContainerTag() {
			String data = """
				<?xml version="1.0" encoding="UTF-8"?>
				 <WeatherReport>
				  <Locations>
				   <City name="A"/>
				   <Town name="B"/>
				   <Town name="D"/>
				   <Town name="E"/>
				   <City name="F"/>
				   <City name="H"/>
				   <Town name="C"/>
				   <City name="G"/>
				  </Locations>
				 </WeatherReport>
				 """;
			XjxSerdes xjx = new XjxSerdes();
			var weatherReport = xjx.read(data, WeatherReport.class);

			assertThat(weatherReport.cities)
					.hasSize(4)
					.containsExactly(new City("A"), new City("F"), new City("H"), new City("G"));
			assertThat(weatherReport.towns)
					.hasSize(4)
					.containsExactly(new Town("B"), new Town("D"), new Town("E"), new Town("C"));
		}

		@Test
		void atRootLevel() {
			String data = """
				<?xml version="1.0" encoding="UTF-8"?>
				 <WeatherReport>
				  <City name="A"/>
				  <Town name="B"/>
				  <Town name="D"/>
				  <Town name="E"/>
				  <City name="F"/>
				  <City name="H"/>
				  <Town name="C"/>
				  <City name="G"/>
				 </WeatherReport>
				 """;
			XjxSerdes xjx = new XjxSerdes();
			var weatherReport = xjx.read(data, WeatherReportAtRoot.class);

			assertThat(weatherReport.cities)
					.hasSize(4)
					.containsExactly(new CityAtRoot("A"), new CityAtRoot("F"), new CityAtRoot("H"), new CityAtRoot("G"));
			assertThat(weatherReport.towns)
					.hasSize(4)
					.containsExactly(new TownAtRoot("B"), new TownAtRoot("D"), new TownAtRoot("E"), new TownAtRoot("C"));
		}

		@Test
		void repeatedTagContainingCollection() {
			String data = """
			<?xml version="1.0" encoding="UTF-8"?>
			<manifest>
			 <foo name="A"/>
			 <foo name="B"/>
			 
			 <bar name="X">
			     <info val="InfoValX1"/>
			     <info val="InfoValX2"/>
			 </bar>
			 <bar name="Z">
			     <info val="InfoValZ1"/>
			 </bar>
			</manifest>
			""";
			XjxSerdes xjx = new XjxSerdes();
			Manifest manifest = xjx.read(data, Manifest.class);

			assertThat(manifest.foos)
					.hasSize(2)
					.containsExactly(new Foo("A"), new Foo("B"));
			assertThat(manifest.bars)
					.hasSize(2)
					.containsExactly(
							new Bar("X", List.of(new Info("InfoValX1"), new Info("InfoValX2"))),
							new Bar("Z", List.of(new Info("InfoValZ1"))));
		}
	}

	private static class Manifest {
		@Tag(path = "/manifest", items = "foo")
		private List<Foo> foos;

		@Tag(path = "/manifest", items = "bar")
		private List<Bar> bars;
	}

	private static class Foo {
		@Tag(path = "/manifest/foo", attribute = "name")
		private String name;

		public Foo() {
		}

		public Foo(String name) {
			this.name = name;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Foo foo = (Foo) o;
			return Objects.equals(name, foo.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}
	}

	private static class Bar {
		@Tag(path = "/manifest/bar", attribute = "name")
		private String name;

		@Tag(path = "/manifest/bar", items = "info")
		private List<Info> infos;

		public Bar() {
		}

		public Bar(String name, List<Info> infos) {
			this.name = name;
			this.infos = infos;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Bar bar = (Bar) o;
			return Objects.equals(name, bar.name) && Objects.equals(infos, bar.infos);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, infos);
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Bar.class.getSimpleName() + "[", "]")
					.add("name='" + name + "'")
					.add("infos=" + infos)
					.toString();
		}
	}

	private static class Info {
		@Tag(path = "/manifest/bar/info", attribute = "val")
		private String val;

		public Info() {
		}

		public Info(String val) {
			this.val = val;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Info info = (Info) o;
			return Objects.equals(val, info.val);
		}

		@Override
		public int hashCode() {
			return Objects.hash(val);
		}
	}

	static class TownAtRoot {
		public TownAtRoot() {
		}

		@Tag(path = "/WeatherReport/Town", attribute = "name")
		String name;

		public TownAtRoot(String name) {
			this.name = name;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			TownAtRoot townAtRoot = (TownAtRoot) o;
			return Objects.equals(name, townAtRoot.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}
	}

	static class CityAtRoot {
		public CityAtRoot() {
		}

		@Tag(path = "/WeatherReport/City", attribute = "name")
		String name;

		public CityAtRoot(String name) {
			this.name = name;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			CityAtRoot cityAtRoot = (CityAtRoot) o;
			return Objects.equals(name, cityAtRoot.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}
	}

	static class WeatherReportAtRoot {

		public WeatherReportAtRoot() {
		}

		@Tag(path = "/WeatherReport", items = "Town")
		List<TownAtRoot> towns;

		@Tag(path = "/WeatherReport", items = "City")
		List<CityAtRoot> cities;
	}

	static class Town {
		public Town() {
		}

		@Tag(path = "/WeatherReport/Locations/Town", attribute = "name")
		String name;

		public Town(String name) {
			this.name = name;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Town townAtRoot = (Town) o;
			return Objects.equals(name, townAtRoot.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}
	}

	static class City {
		public City() {
		}

		@Tag(path = "/WeatherReport/Locations/City", attribute = "name")
		String name;

		public City(String name) {
			this.name = name;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			City cityAtRoot = (City) o;
			return Objects.equals(name, cityAtRoot.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}
	}

	static class WeatherReport {

		public WeatherReport() {
		}

		@Tag(path = "/WeatherReport/Locations", items = "Town")
		List<Town> towns;

		@Tag(path = "/WeatherReport/Locations", items = "City")
		List<City> cities;
	}

    static class Gpx {
        public Gpx() {
        }

        @Tag(path = "/gpx", items = "wpt")
        List<Wpt> wayPoints;
    }

    static class Wpt {
        public Wpt() {
        }

        @Tag(path = "/gpx/wpt/desc")
        String description;

        @Tag(path = "time")
        String time;
    }
}
