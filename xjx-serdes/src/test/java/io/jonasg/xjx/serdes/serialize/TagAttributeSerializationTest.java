package io.jonasg.xjx.serdes.serialize;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;

public class TagAttributeSerializationTest {

    @Test
    void serialize_TagContainingAValueAndMultipleAttributes() {
        // given
        var dataHolder = new TagContainingValueAndAttributes("11", "A", "B");

        // when
        String xml = new XjxSerdes().write(dataHolder);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <DataTypes>
                  <String attrA="A" attrB="B">11</String>
                </DataTypes>
                """);
    }

    @Test
    void serialize_ToEmptyTag_ContainingMultipleAttributes() {
        // given
        var dataType = new TagContainingMultipleAttributesAndNoValue("A", "B");

        // when
        String xml = new XjxSerdes().write(dataType);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <DataTypes>
                  <String attrA="A" attrB="B"/>
                </DataTypes>
                """);
    }

    @Test
    void serialize_IntegerField() {
        // given
        IntegerData dataTypes = new IntegerData(11);

        // when
        String xml = new XjxSerdes().write(dataTypes);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <DataTypes>
                  <Integer value="11"/>
                </DataTypes>
                """);
    }

    @Test
    void serialize_LongField() {
        // given
        LongData dataTypes = new LongData(11L);

        // when
        String xml = new XjxSerdes().write(dataTypes);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <DataTypes>
                  <Long value="11"/>
                </DataTypes>
                """);
    }

    @Test
    void serialize_primitiveLongField() {
        // given
        PrimitiveLongData dataTypes = new PrimitiveLongData(11L);

        // when
        String xml = new XjxSerdes().write(dataTypes);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <DataTypes>
                  <primitiveLong value="11"/>
                </DataTypes>
                """);
    }

    @Test
    void serialize_BigDecimalField() {
        // given
        BigDecimalData dataTypes = new BigDecimalData(BigDecimal.valueOf(11));

        // when
        String xml = new XjxSerdes().write(dataTypes);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <DataTypes>
                  <BigDecimal value="11"/>
                </DataTypes>
                """);
    }

    @Test
    void serialize_DoubleField() {
        // given
        DoubleData dataTypes = new DoubleData(11.0);

        // when
        String xml = new XjxSerdes().write(dataTypes);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <DataTypes>
                  <Double value="11.0"/>
                </DataTypes>
                """);
    }

    @Test
    void serialize_primitiveDoubleField() {
        // given
        PrimitiveDoubleData dataTypes = new PrimitiveDoubleData(11);

        // when
        String xml = new XjxSerdes().write(dataTypes);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <DataTypes>
                  <primitiveDouble value="11.0"/>
                </DataTypes>
                """);
    }

    @Test
    void serialize_multiCharString_toPrimitiveCharField() {
        // given
        MultipleCharactersData dataTypes = new MultipleCharactersData('A');

        // when
        String xml = new XjxSerdes().write(dataTypes);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <DataTypes>
                  <multipleCharacters value="A"/>
                </DataTypes>
                """);
    }

    @Test
    void serialize_primitiveCharField() {
        // given
        PrimitiveCharData dataTypes = new PrimitiveCharData('A');

        // when
        String xml = new XjxSerdes().write(dataTypes);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <DataTypes>
                  <primitiveChar value="A"/>
                </DataTypes>
                """);
    }

    @Test
    void serialize_CharacterField() {
        // given
        CharacterData dataTypes = new CharacterData('A');

        // when
        String xml = new XjxSerdes().write(dataTypes);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <DataTypes>
                  <Character value="A"/>
                </DataTypes>
                """);
    }

    @Test
    void serialize_booleanFields() {
        // given
        BooleanData dataTypes = new BooleanData(true, true, false, false);

        // when
        String xml = new XjxSerdes().write(dataTypes);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <DataTypes>
                  <BooleanTrue Boolean="true" boolean="true"/>
                  <BooleanFalse Boolean="false" boolean="false"/>
                </DataTypes>
                """);
    }

    @Test
    void serialize_TagMappedUsingRelativePath() {
        // given
        ParentHolder parentHolder = new ParentHolder();
        parentHolder.nestedField = new NestedField.StringData("11");

        // when
        String xml = new XjxSerdes().write(parentHolder);

        // then
        Assertions.assertThat(xml).isEqualTo("""
                <DataTypes>
                  <String value="11"/>
                </DataTypes>
                """);
    }

    public static class TagContainingValueAndAttributes {
        public TagContainingValueAndAttributes(String tagValue, String attributeA, String attributeB) {
            this.attributeA = attributeA;
            this.attributeB = attributeB;
            this.tagValue = tagValue;
        }

        @Tag(path = "/DataTypes/String", attribute = "attrA")
        String attributeA;

        @Tag(path = "/DataTypes/String", attribute = "attrB")
        String attributeB;

        @Tag(path = "/DataTypes/String")
        String tagValue;
    }

    public static class TagContainingMultipleAttributesAndNoValue {

        public TagContainingMultipleAttributesAndNoValue(String attrA, String attrB) {
            this.attrA = attrA;
            this.attrB = attrB;
        }

        @Tag(path = "/DataTypes/String", attribute = "attrA")
        String attrA;

        @Tag(path = "/DataTypes/String", attribute = "attrB")
        String attrB;
    }

    public static class IntegerData {
        public IntegerData(Integer value) {
            this.Integer = value;
        }

        @Tag(path = "/DataTypes/Integer", attribute = "value")
        Integer Integer;
    }

    public static class LongData {
        public LongData(Long value) {
            this.Long = value;
        }

        @Tag(path = "/DataTypes/Long", attribute = "value")
        Long Long;
    }

    public static class PrimitiveLongData {
        public PrimitiveLongData(long value) {
            this.primitiveLong = value;
        }

        @Tag(path = "/DataTypes/primitiveLong", attribute = "value")
        long primitiveLong;
    }

    public static class BigDecimalData {
        public BigDecimalData(BigDecimal value) {
            this.BigDecimal = value;
        }

        @Tag(path = "/DataTypes/BigDecimal", attribute = "value")
        BigDecimal BigDecimal;
    }

    public static class DoubleData {
        public DoubleData(Double value) {
            this.Double = value;
        }

        @Tag(path = "/DataTypes/Double", attribute = "value")
        Double Double;
    }

    public static class PrimitiveDoubleData {
        public PrimitiveDoubleData(double value) {
            this.primitiveDouble = value;
        }

        @Tag(path = "/DataTypes/primitiveDouble", attribute = "value")
        double primitiveDouble;
    }

    public static class MultipleCharactersData {
        public MultipleCharactersData(char value) {
            this.multipleCharacters = value;
        }

        @Tag(path = "/DataTypes/multipleCharacters", attribute = "value")
        char multipleCharacters;
    }

    public static class PrimitiveCharData {
        public PrimitiveCharData(char value) {
            this.primitiveChar = value;
        }

        @Tag(path = "/DataTypes/primitiveChar", attribute = "value")
        char primitiveChar;
    }

    public static class CharacterData {
        public CharacterData(char value) {
            this.Character = value;
        }

        @Tag(path = "/DataTypes/Character", attribute = "value")
        Character Character;
    }

    public static class BooleanData {
        public BooleanData(boolean boolTrue, boolean primitiveBoolTrue, boolean boolFalse, boolean primitiveBoolFalse) {
            this.BooleanTrue = boolTrue;
            this.booleanTrue = primitiveBoolTrue;
            this.BooleanFalse = boolFalse;
            this.booleanFalse = primitiveBoolFalse;
        }

        @Tag(path = "/DataTypes/BooleanTrue", attribute = "Boolean")
        boolean BooleanTrue;

        @Tag(path = "/DataTypes/BooleanTrue", attribute = "boolean")
        boolean booleanTrue;

        @Tag(path = "/DataTypes/BooleanFalse", attribute = "Boolean")
        boolean BooleanFalse;

        @Tag(path = "/DataTypes/BooleanFalse", attribute = "boolean")
        boolean booleanFalse;
    }

    public static class ParentHolder {
        @Tag(path = "/DataTypes")
        NestedField nestedField;
    }

    public static class NestedField {
        public static class StringData extends NestedField {
            public StringData(String value) {
                this.String = value;
            }

            @Tag(path = "String", attribute = "value")
            String String;
        }
    }
}
