package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

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
}
