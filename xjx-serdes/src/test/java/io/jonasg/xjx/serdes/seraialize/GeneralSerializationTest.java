package io.jonasg.xjx.serdes.seraialize;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class GeneralSerializationTest {
    @Test
    void serializeNestedTags() {
        // given
        var weatherData = new WeatherData("USA", "New York");

        // when
        String xml = new XjxSerdes().write(weatherData);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <WeatherData>
                  <Location>
                    <Country>USA</Country>
                    <City>
                      <Name>New York</Name>
                    </City>
                  </Location>
                </WeatherData>
                """);
    }

    @Test
    void serializeNullFieldsToSelfClosingTag() {
        // given
        var weatherData = new WeatherData(null, "New York");

        // when
        String xml = new XjxSerdes().write(weatherData);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <WeatherData>
                  <Location>
                    <Country/>
                    <City>
                      <Name>New York</Name>
                    </City>
                  </Location>
                </WeatherData>
                """);
    }


    @Test
    void serializeNullFieldsToSelfClosingTagContainingNonNullAttribute() {
        // given
        var weatherData = new WeatherDataWithAttribute(null, "USA");

        // when
        String xml = new XjxSerdes().write(weatherData);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <WeatherData>
                  <Location>
                    <Country code="USA"/>
                  </Location>
                </WeatherData>
                """);
    }

    @SuppressWarnings("unused")
    static class WeatherData {

        @Tag(path = "/WeatherData/Location/Country")
        private final String country;

        @Tag(path = "/WeatherData/Location/City/Name")
        private final String city;

        public WeatherData(String country, String city) {
            this.country = country;
            this.city = city;
        }
    }


    @SuppressWarnings("unused")
    static class WeatherDataWithAttribute {

        @Tag(path = "/WeatherData/Location/Country")
        private final String country;

        @Tag(path = "/WeatherData/Location/Country", attribute = "code")
        private final String code;

        public WeatherDataWithAttribute(String country, String code) {
            this.country = country;
            this.code = code;
        }
    }
}
