package io.jonasg.xjx.serdes.deserialize;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;
import io.jonasg.xjx.serdes.deserialize.CustomValueDeserializationTest.EnumHolder;
import io.jonasg.xjx.serdes.deserialize.CustomValueDeserializationTest.Unit;
import static org.assertj.core.api.Assertions.*;

public class EnumDeserializationTest {


	@Test
	void mapToFieldOfTypeEnum_whenCharacterDataMatchesEnumConstantName() {
		// given
		String data = """
				<?xml version="1.0" encoding="UTF-8"?>
				<WeatherData>
				    <Day>
				        <High>
				            <Value>78</Value>
				            <Unit>FAHRENHEIT</Unit>
				        </High>
				        <Low>
				            <Value>62</Value>
				            <Unit>FAHRENHEIT</Unit>
				        </Low>
				        <Precipitation>
				            <Value>10</Value>
				            <Unit>PERCENTAGE</Unit>
				        </Precipitation>
				        <WeatherCondition>Partly Cloudy</WeatherCondition>
				    </Day>
				</WeatherData>
				""";

		// when
		EnumHolder enumHolder = new XjxSerdes().read(data, EnumHolder.class);

		// then
		assertThat(enumHolder.dayHighUnit).isEqualTo(Unit.FAHRENHEIT);
		assertThat(enumHolder.precipitationUnit).isEqualTo(Unit.PERCENTAGE);
	}

	@Test
	void defaultToNullWhenValueCannotBeMappedToName() {
		// given
		String data = """
				<?xml version="1.0" encoding="UTF-8"?>
				<WeatherData>
				    <Day>
				        <High>
				            <Value>78</Value>
				            <Unit>fahrenheit</Unit>
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
				</WeatherData>
				""";

		// when
		EnumHolder enumHolder = new XjxSerdes().read(data, EnumHolder.class);

		// then
		assertThat(enumHolder.dayHighUnit).isNull();
		assertThat(enumHolder.precipitationUnit).isNull();
	}

	@Nested
	class ConfigurationTest {

		@Test
		void defaultToNullWhenValueCannotBeMappedToName() {
			// given
			String data = """
					<?xml version="1.0" encoding="UTF-8"?>
					<WeatherData>
					    <Day>
					        <High>
					            <Value>78</Value>
					            <Unit>fahrenheit</Unit>
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
					</WeatherData>
					""";

			// when
			EnumHolder enumHolder = new XjxSerdes(c -> c.failOnUnknownEnumValue(false))
					.read(data, EnumHolder.class);

			// then
			assertThat(enumHolder.dayHighUnit).isNull();
			assertThat(enumHolder.precipitationUnit).isNull();
		}

		@Test
		void failOnUnmappableEnumValue() {
			// given
			String data = """
					<?xml version="1.0" encoding="UTF-8"?>
					<WeatherData>
					    <Day>
					        <High>
					            <Value>78</Value>
					            <Unit>unmappable</Unit>
					        </High>
					        <Low>
					            <Value>62</Value>
					            <Unit>unmappable</Unit>
					        </Low>
					        <Precipitation>
					            <Value>10</Value>
					            <Unit>fahrenheit/Unit>
					        </Precipitation>
					        <WeatherCondition>Partly Cloudy</WeatherCondition>
					    </Day>
					</WeatherData>
					""";

			// when
			ThrowingCallable throwingCallable = () -> new XjxSerdes(c -> c.failOnUnknownEnumValue(true))
					.read(data, EnumHolder.class);

			// then
			assertThatThrownBy(throwingCallable)
					.isInstanceOf(XjxDeserializationException.class)
					.hasMessageContaining("Cannot map value 'unmappable' to enum Unit");
		}
	}

	static class EnumHolder {
		@Tag(path = "/WeatherData/Day/High/Unit")
		Unit dayHighUnit;

		@Tag(path = "/WeatherData/Day/Precipitation/Unit")
		Unit precipitationUnit;
	}

	enum Unit {
		FAHRENHEIT, PERCENTAGE
	}
}
