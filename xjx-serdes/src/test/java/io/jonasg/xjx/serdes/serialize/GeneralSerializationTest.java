package io.jonasg.xjx.serdes.serialize;

import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;

public class GeneralSerializationTest {

	static Stream<Arguments> serializeNestedTags() {
		class WeatherDataPojo {

			@Tag(path = "/WeatherData/Location/Country")
			private final String country;

			@Tag(path = "/WeatherData/Location/City/Name")
			private final String city;

			@Tag(path = "/WeatherData/Location/City/Name", attribute = "code")
			private final String code;

			public WeatherDataPojo(String country, String city, String code) {
				this.country = country;
				this.city = city;
				this.code = code;
			}
		}
		record WeatherDataRecord(
				@Tag(path = "/WeatherData/Location/Country")
				String country,
				@Tag(path = "/WeatherData/Location/City/Name")
				String city,
				@Tag(path = "/WeatherData/Location/City/Name", attribute = "code")
				String postalCode
		) {}
		return Stream.of(
				Arguments.of(new WeatherDataPojo("USA", "New York", "NY")),
				Arguments.of(new WeatherDataRecord("USA", "New York", "NY"))
		);
	}

	@ParameterizedTest
	@MethodSource("serializeNestedTags")
	void serializeNestedTags(Object data) {
		// when
		String xml = new XjxSerdes().write(data);

		// then
		Assertions.assertThat(xml).isEqualTo("""
				<WeatherData>
				  <Location>
				    <Country>USA</Country>
				    <City>
				      <Name code="NY">New York</Name>
				    </City>
				  </Location>
				</WeatherData>
				""");
	}

	static Stream<Arguments> serializeNestedObjectsWithoutTagAnnotation() {
		class Code {
			@Tag(path = "/WeatherData/Location/Country", attribute = "code")
			private final String value;
			Code(String value) { this.value = value; }
		}
		class Country {
			@Tag(path = "/WeatherData/Location/Country")
			private final String name;
			private final Code code;
			Country(String name, Code code) { this.name = name; this.code = code; }
		}
		class WeatherData {
			private final Country country;
			WeatherData(Country country) { this.country = country; }
		}

		record CodeRecord(@Tag(path = "/WeatherData/Location/Country", attribute = "code") String value) {}
		record CountryRecord(@Tag(path = "/WeatherData/Location/Country") String name, CodeRecord code) {}
		record WeatherDataRecord(CountryRecord country) {}

		return Stream.of(
				Arguments.of(new WeatherData(new Country("United States of America", new Code("US")))),
				Arguments.of(new WeatherDataRecord(new CountryRecord("United States of America", new CodeRecord("US"))))
		);
	}

	@ParameterizedTest
	@MethodSource("serializeNestedObjectsWithoutTagAnnotation")
	void serializeNestedObjectsWithoutTagAnnotation(Object data) {
		// when
		String xml = new XjxSerdes().write(data);

		// then
		Assertions.assertThat(xml).isEqualTo("""
				<WeatherData>
				  <Location>
				    <Country code="US">United States of America</Country>
				  </Location>
				</WeatherData>
				""");
	}

	static Stream<Arguments> serializeNullFieldsToSelfClosingTag() {
		class WeatherData {
			@Tag(path = "/WeatherData/Location/Country")
			private final String country;
			@Tag(path = "/WeatherData/Location/City/Name")
			private final String city;
			WeatherData(String country, String city) { this.country = country; this.city = city; }
		}
		record WeatherDataRecord(@Tag(path = "/WeatherData/Location/Country") String country,
								 @Tag(path = "/WeatherData/Location/City/Name") String city) {}

		return Stream.of(
				Arguments.of(new WeatherData(null, "New York")),
				Arguments.of(new WeatherDataRecord(null, "New York"))
		);
	}

	@ParameterizedTest
	@MethodSource("serializeNullFieldsToSelfClosingTag")
	void serializeNullFieldsToSelfClosingTag(Object data) {

		// when
		String xml = new XjxSerdes().write(data);

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

	static Stream<Arguments> serializeNullFieldsMappedToAnAttribute_byNotAddingAttributeToTag_ofAnAlreadyExistingTag() {
		class WeatherData {
			@Tag(path = "/WeatherData/Location/Country")
			private final String country;
			@Tag(path = "/WeatherData/Location/City/Name")
			private final String city;
			@Tag(path = "/WeatherData/Location/City/Name", attribute = "code")
			private final String code;
			WeatherData(String country, String city, String code) { this.country = country; this.city = city; this.code = code; }
		}
		record WeatherDataRecord(@Tag(path = "/WeatherData/Location/Country") String country,
								 @Tag(path = "/WeatherData/Location/City/Name") String city,
								 @Tag(path = "/WeatherData/Location/City/Name", attribute = "code") String code) {}

		return Stream.of(
				Arguments.of(new WeatherData(null, "New York", null)),
				Arguments.of(new WeatherDataRecord(null, "New York", null))
		);
	}

	@ParameterizedTest
	@MethodSource("serializeNullFieldsMappedToAnAttribute_byNotAddingAttributeToTag_ofAnAlreadyExistingTag")
	void serializeNullFieldsMappedToAnAttribute_byNotAddingAttributeToTag_ofAnAlreadyExistingTag(Object data) {
		// when
		String xml = new XjxSerdes().write(data);

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

	static Stream<Arguments> serializeNullFieldsMappedToAnAttribute_byNotAddingAttributeToTag_ofATagThatIsNotMapped() {
		class WeatherData {
			@Tag(path = "/WeatherData/Location/Country")
			private final String country;
			@Tag(path = "/WeatherData/Location/City/Name", attribute = "code")
			private final String code;
			WeatherData(String country, String code) { this.country = country; this.code = code; }
		}
		record WeatherDataRecord(@Tag(path = "/WeatherData/Location/Country") String country,
								 @Tag(path = "/WeatherData/Location/City/Name", attribute = "code") String code) {}

		return Stream.of(
				Arguments.of(new WeatherData(null, null)),
				Arguments.of(new WeatherDataRecord(null, null))
		);
	}

	@ParameterizedTest
	@MethodSource("serializeNullFieldsMappedToAnAttribute_byNotAddingAttributeToTag_ofATagThatIsNotMapped")
	void serializeNullFieldsMappedToAnAttribute_byNotAddingAttributeToTag_ofATagThatIsNotMapped(Object data) {
		// when
		String xml = new XjxSerdes().write(data);

		// then
		Assertions.assertThat(xml).isEqualTo("""
                <WeatherData>
                  <Location>
                    <Country/>
                    <City/>
                  </Location>
                </WeatherData>
                """);
	}

	static Stream<Arguments> serializeNullFieldsToSelfClosingTagContainingNonNullAttribute() {
		class WeatherDataWithAttribute {
			@Tag(path = "/WeatherData/Location/Country")
			private final String country;
			@Tag(path = "/WeatherData/Location/Country", attribute = "code")
			private final String code;

			public WeatherDataWithAttribute(String country, String code) {
				this.country = country;
				this.code = code;
			}
		}
		record WeatherDataWithAttributeRecord(
				@Tag(path = "/WeatherData/Location/Country") String country,
				@Tag(path = "/WeatherData/Location/Country", attribute = "code") String code
		) {}

		return Stream.of(
				Arguments.of(new WeatherDataWithAttribute(null, "USA")),
				Arguments.of(new WeatherDataWithAttributeRecord(null, "USA"))
		);
	}

	@ParameterizedTest
	@MethodSource("serializeNullFieldsToSelfClosingTagContainingNonNullAttribute")
	void serializeNullFieldsToSelfClosingTagContainingNonNullAttribute(Object data) {
		// when
		String xml = new XjxSerdes().write(data);

		// then
		Assertions.assertThat(xml).isEqualTo("""
                <WeatherData>
                  <Location>
                    <Country code="USA"/>
                  </Location>
                </WeatherData>
                """);
	}
}
