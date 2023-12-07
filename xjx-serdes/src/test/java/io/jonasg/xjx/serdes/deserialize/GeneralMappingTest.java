package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

public class GeneralMappingTest {

    @Test
    void ignoreUnmappedFieldsAndLeaveThemUninitialized() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <Double>5.7</Double>
                </DataTypes>
                """;

        // when
        UnMappedDataHolder unMappedDataHolder = new XjxSerdes().read(data, UnMappedDataHolder.class);

        // then
        Assertions.assertThat(unMappedDataHolder.aDouble).isEqualTo(Double.valueOf(5.7));
        Assertions.assertThat(unMappedDataHolder.unmappedNull).isNull();
        Assertions.assertThat(unMappedDataHolder.unmappedInitialized).isEqualTo("initialized");
    }

    static class UnMappedDataHolder {
        @Tag(path = "/DataTypes/Double")
        Double aDouble;

        String unmappedNull;

        String unmappedInitialized = "initialized";
    }

    @Test
    void nestedComplexTypesDoNotNeedTopLevelMapping_EvenWhenContainingInnerMappings() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <Double>5.7</Double>
                </DataTypes>
                """;

        // when
        NestedComplexTypeWithoutMappingDataHolder dataHolder = new XjxSerdes().read(data, NestedComplexTypeWithoutMappingDataHolder.class);

        // then
        Assertions.assertThat(dataHolder.nestedComplexType.aDouble).isEqualTo(Double.valueOf(5.7));
    }

    static class NestedComplexTypeWithoutMappingDataHolder {
        NestedComplexType nestedComplexType;
    }

    static class NestedComplexType {
        @Tag(path = "/DataTypes/Double")
        Double aDouble;
    }

    @Test
    void mapRelativePaths() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <WeatherData>
                    <Week>
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
                    </Week>
                </WeatherData>
                """;

        // when
        RelativeMappedParentDataHolder dataHolder = new XjxSerdes().read(data, RelativeMappedParentDataHolder.class);

        // then
        Assertions.assertThat(dataHolder.week.day.highValue).isEqualTo(Double.valueOf(78));
    }

    static class RelativeMappedParentDataHolder {
        @Tag(path = "/WeatherData/Week")
        Week week;
    }

    static class Week {
        @Tag(path = "Day")
        Day day;
    }

    static class Day {
        @Tag(path = "High/Value")
        Double highValue;
    }

    @Test
    void warnUserWHenRelativePathIsUsed_butParentsDoNotResolveToAbsolutePath() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <WeatherData>
                    <Week>
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
                    </Week>
                </WeatherData>
                """;

        // when
        ThrowableAssert.ThrowingCallable deserializing = () -> new XjxSerdes().read(data, RelativeMappedWithoutParentTagDataHolder.class);

        // then
        Assertions.assertThatThrownBy(deserializing)
                .isInstanceOf(XjxDeserializationException.class)
                .hasMessage("Field day is annotated with @Tag but one of it's parent is missing a @Tag.");
    }

    static class RelativeMappedWithoutParentTagDataHolder {
        Week week;
    }
}
