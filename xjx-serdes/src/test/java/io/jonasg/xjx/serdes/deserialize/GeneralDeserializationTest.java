package io.jonasg.xjx.serdes.deserialize;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;

public class GeneralDeserializationTest {

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
        assertThat(unMappedDataHolder.aDouble).isEqualTo(Double.valueOf(5.7));
        assertThat(unMappedDataHolder.unmappedNull).isNull();
        assertThat(unMappedDataHolder.unmappedInitialized).isEqualTo("initialized");
    }

    static class UnMappedDataHolder {
        @Tag(path = "/DataTypes/Double")
        Double aDouble;

        String unmappedNull;

        String unmappedInitialized = "initialized";
    }

    @Test
    void nestedComplexTypesDoNotNeedTopLevelMapping_WhenInnerMappingsContainAbsolutePaths() {
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
        assertThat(dataHolder.nestedComplexType.aDouble).isEqualTo(Double.valueOf(5.7));
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
        assertThat(dataHolder.week.day.highValue).isEqualTo(Double.valueOf(78));
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
        assertThatThrownBy(deserializing)
                .isInstanceOf(XjxDeserializationException.class)
                .hasMessage("Field day is annotated with @Tag but one of it's parent is missing a @Tag.");
    }

    static class RelativeMappedWithoutParentTagDataHolder {
        Week week;
    }

    @Test
    void fieldsNotAnnotatedWithTagShouldBeIgnored() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <Double>5.7</Double>
                </DataTypes>
                """;

        // when
        var holder = new XjxSerdes().read(data, FieldsWithoutTagAnnotationHolder.class);

        // then
        assertThat(holder.Double).isNull();
    }

    static class FieldsWithoutTagAnnotationHolder {
        Double Double;
    }

    @Test
    void ignorePathMappingsEndingWithSlash() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <Double>5.7</Double>
                </DataTypes>
                """;

        // when
        var holder = new XjxSerdes().read(data, SlashSuffixedHolder.class);

        // then
        assertThat(holder.Double).isEqualTo(5.7D);
    }

    static class SlashSuffixedHolder {
        @Tag(path = "/DataTypes/Double/")
        Double Double;
    }

    @Test
    void ignoreSuffixedAndPrefixedWhiteSpaceInPathMappings() {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <Double>5.7</Double>
                </DataTypes>
                """;

        // when
        var holder = new XjxSerdes().read(data, WhiteSpacePathMappingHolder.class);

        // then
        assertThat(holder.Double).isEqualTo(5.7D);
    }

    static class WhiteSpacePathMappingHolder {
        @Tag(path = "   /DataTypes/Double   ")
        Double Double;
    }

	@Test
	void absoluteRootMappingWithTopLevelMappedRootType() {
		// given
		String data = """
				<?xml version="1.0" encoding="UTF-8"?>
				<DataTypes>
					<Double>5.7</Double>
				</DataTypes>
				""";

		// when
		var holder = new XjxSerdes().read(data, AbsoluteRootMappingHolder.class);

		// then
		assertThat(holder.Double).isEqualTo(5.7D);
	}

	@Tag(path = "/DataTypes")
	static class AbsoluteRootMappingHolder {
		@Tag(path = "/DataTypes/Double")
		Double Double;
	}

	@Test
	void namespaceSupport() {
		// given
		String data = """
				<?xml version="1.0" encoding="UTF-8"?>
				<xjx:Tables xmlns:xjx="https://github.com/jonas-grgt/xjx">
					<xjx:TableA>5.7</xjx:TableA>
					<TableB>TableB</TableB>
				</xjx:Tables>
				""";

		// when
		var holder = new XjxSerdes().read(data, NamespaceHolder.class);

		// then
		assertThat(holder.tableA).isEqualTo(5.7D);
		assertThat(holder.tableB).isEqualTo("TableB");
	}

	static class NamespaceHolder {
		@Tag(path = "/Tables/TableA")
		Double tableA;

		@Tag(path = "/Tables/TableB")
		String tableB;
	}
}
