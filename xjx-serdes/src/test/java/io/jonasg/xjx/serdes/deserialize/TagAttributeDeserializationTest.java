package io.jonasg.xjx.serdes.deserialize;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;

public class TagAttributeDeserializationTest {
    @Test
    void deserialize_StringField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <String value="11"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        assertThat(dataTypes.String).isEqualTo("11");
    }

	@Test
	void deserialize_IntegerField() {
		// given
		String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <Integer value="11"/>
                    </DataTypes>
                    """;

		// when
		DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

		// then
		assertThat(dataTypes.Integer).isEqualTo(11);
	}


	@Test
	void deserialize_primitiveIntField() {
		// given
		String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <primitiveInt value="11"/>
                    </DataTypes>
                    """;

		// when
		DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

		// then
		assertThat(dataTypes.primitiveInt).isEqualTo(11);
	}

    @Test
    void deserialize_LongField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <Long value="11"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        assertThat(dataTypes.Long).isEqualTo(11L);
    }

    @Test
    void deserialize_primitiveLongField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <primitiveLong value="11"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        assertThat(dataTypes.primitiveLong).isEqualTo(11L);
    }

    @Test
    void deserialize_BigDecimalField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <BigDecimal value="11"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        assertThat(dataTypes.BigDecimal).isEqualTo(BigDecimal.valueOf(11));
    }

    @Test
    void deserialize_DoubleField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <Double value="11"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        assertThat(dataTypes.Double).isEqualTo(Double.valueOf(11));
    }

    @Test
    void deserialize_primitiveDoubleField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <primitiveDouble value="11"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        assertThat(dataTypes.primitiveDouble).isEqualTo(Double.valueOf(11));
    }

    @Test
    void deserialize_multiCharString_toPrimitiveCharField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <multipleCharacters value="ABC"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        assertThat(dataTypes.multipleCharacters).isEqualTo('A');
    }

    @Test
    void deserialize_primitiveCharField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <primitiveChar value="A"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        assertThat(dataTypes.primitiveChar).isEqualTo('A');
    }

    @Test
    void deserialize_CharacterField() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <Character value="A"/>
                    </DataTypes>
                    """;

        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        assertThat(dataTypes.Character).isEqualTo(Character.valueOf('A'));
    }

    @ParameterizedTest
    @ValueSource(strings = {"True","true","1","yes","YeS"})
    void deserializeTrueValuesFor_booleanField(String value) {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <BooleanTrue Boolean="%s" boolean="%1$s"/>
                </DataTypes>
                """.formatted(value);
        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        assertThat(dataTypes.BooleanTrue).isTrue();
        assertThat(dataTypes.booleanTrue).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"False","false","0","no","No"})
    void deserializeFalseValuesFor_booleanField(String value) {
        // given
        String data = """
                <?xml version="1.0" encoding="UTF-8"?>
                <DataTypes>
                    <BooleanFalse Boolean="%s" boolean="%1$s"/>
                </DataTypes>
                """.formatted(value);
        // when
        DataTypeHolder dataTypes = new XjxSerdes().read(data, DataTypeHolder.class);

        // then
        assertThat(dataTypes.BooleanFalse).isFalse();
        assertThat(dataTypes.booleanFalse).isFalse();
    }

    @Test
    void deserialize_StringField_mappedUsingRelativePath() {
        // given
        String data = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <DataTypes>
                        <String value="11"/>
                    </DataTypes>
                    """;

        // when
        ParentHolder parentHolder = new XjxSerdes().read(data, ParentHolder.class);

        // then
        assertThat(parentHolder.nestedField.String).isEqualTo("11");
    }

	@Test
	void deserialize_mapValueAndAtTheSameTimeAttributeValuesOfOneTag() {
		// given
		String data = """
				<?xml version="1.0" encoding="UTF-8"?>
				<Person>
					<Name age="18" sex="MALE">John</Name>
				</Person>
				""";

		// when
		var person = new XjxSerdes().read(data, Person.class);

		// then
		assertThat(person.name).isEqualTo("John");
		assertThat(person.sex).isEqualTo("MALE");
		assertThat(person.age).isEqualTo(18);
	}

	public static class Person {
		@Tag(path = "/Person/Name")
		String name;

		@Tag(path = "/Person/Name", attribute = "sex")
		String sex;

		@Tag(path = "/Person/Name", attribute = "age")
		int age;
	}


    public static class DataTypeHolder {
        public DataTypeHolder() {
        }

        @Tag(path = "/DataTypes/String", attribute = "value")
        String String;

		@Tag(path = "/DataTypes/Integer", attribute = "value")
		Integer Integer;

		@Tag(path = "/DataTypes/primitiveInt", attribute = "value")
		int primitiveInt;

        @Tag(path = "/DataTypes/Long", attribute = "value")
        Long Long;

        @Tag(path = "/DataTypes/primitiveLong", attribute = "value")
        long primitiveLong;

        @Tag(path = "/DataTypes/BigDecimal", attribute = "value")
        BigDecimal BigDecimal;

        @Tag(path = "/DataTypes/Double", attribute = "value")
        Double Double;

        @Tag(path = "/DataTypes/primitiveDouble", attribute = "value")
        double primitiveDouble;

        @Tag(path = "/DataTypes/multipleCharacters", attribute = "value")
        char multipleCharacters;

        @Tag(path = "/DataTypes/primitiveChar", attribute = "value")
        char primitiveChar;

        @Tag(path = "/DataTypes/Character", attribute = "value")
        Character Character;

        @Tag(path = "/DataTypes/BooleanTrue", attribute = "Boolean")
        boolean BooleanTrue;

        @Tag(path = "/DataTypes/BooleanTrue", attribute = "boolean")
        boolean booleanTrue;

        @Tag(path = "/DataTypes/BooleanFalse", attribute = "Boolean")
        boolean BooleanFalse = true;

        @Tag(path = "/DataTypes/BooleanFalse", attribute = "boolean")
        boolean booleanFalse = true;
    }

    public static class ParentHolder {
        @Tag(path = "/DataTypes")
        NestedField nestedField;
    }

    public static class NestedField {
        @Tag(path = "String", attribute = "value")
        String String;
    }
}
