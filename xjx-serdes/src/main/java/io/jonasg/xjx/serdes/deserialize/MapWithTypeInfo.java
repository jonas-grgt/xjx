package io.jonasg.xjx.serdes.deserialize;

import java.util.HashMap;
import java.util.Map;

public record MapWithTypeInfo(Map<String, Object> map, Class<?> valueType) {
}
