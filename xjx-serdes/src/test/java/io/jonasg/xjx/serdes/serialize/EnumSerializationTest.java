package io.jonasg.xjx.serdes.serialize;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class EnumSerializationTest {


    @Test
    void mapToFieldOfTypeEnum_whenCharacterDataMatchesEnumConstantName() {
        // given
        var enumHolder = new EnumHolder(Unit.FAHRENHEIT, Unit.PERCENTAGE);

        // when
        String xml = new XjxSerdes().write(enumHolder);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <WeatherData>
                  <Day>
                    <High>
                      <Unit>FAHRENHEIT</Unit>
                    </High>
                    <Precipitation>
                      <Unit>PERCENTAGE</Unit>
                    </Precipitation>
                  </Day>
                </WeatherData>
                """);
    }

    @Nested
    class NullEnumFieldsShouldBeSerializedAsEmptyTag {

        @Test
        void withOnlyValue() {
            // given
            var enumHolder = new EnumHolder(null, Unit.PERCENTAGE);

            // when
            String xml = new XjxSerdes().write(enumHolder);

            // then
            Assertions.assertThat(xml).isEqualTo("""
                    <WeatherData>
                      <Day>
                        <High>
                          <Unit/>
                        </High>
                        <Precipitation>
                          <Unit>PERCENTAGE</Unit>
                        </Precipitation>
                      </Day>
                    </WeatherData>
                    """);
        }

        @Test
        void withValueAndAttribute() {
            // given
            var enumHolder = new EnumHolderWithAttr(null, "C", Unit.PERCENTAGE);

            // when
            String xml = new XjxSerdes().write(enumHolder);

            // then
            Assertions.assertThat(xml).isEqualTo("""
                    <WeatherData>
                      <Day>
                        <High>
                          <Unit abbr="C"/>
                        </High>
                        <Precipitation>
                          <Unit>PERCENTAGE</Unit>
                        </Precipitation>
                      </Day>
                    </WeatherData>
                    """);
        }
    }

    static class EnumHolder {

        public EnumHolder(Unit dayHighUnit, Unit precipitationUnit) {
            this.dayHighUnit = dayHighUnit;
            this.precipitationUnit = precipitationUnit;
        }

        @Tag(path = "/WeatherData/Day/High/Unit")
        Unit dayHighUnit;

        @Tag(path = "/WeatherData/Day/Precipitation/Unit")
        Unit precipitationUnit;
    }

    static class EnumHolderWithAttr {

        public EnumHolderWithAttr(Unit dayHighUnit, String abbreviation, Unit precipitationUnit) {
            this.dayHighUnit = dayHighUnit;
            this.abbreviation = abbreviation;
            this.precipitationUnit = precipitationUnit;
        }

        @Tag(path = "/WeatherData/Day/High/Unit")
        Unit dayHighUnit;

        @Tag(path = "/WeatherData/Day/High/Unit", attribute = "abbr")
        String abbreviation;

        @Tag(path = "/WeatherData/Day/Precipitation/Unit")
        Unit precipitationUnit;
    }

    enum Unit {
        FAHRENHEIT, PERCENTAGE
    }
}
