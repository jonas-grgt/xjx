package io.jonasg.xjx.serdes;

import io.jonasg.xjx.serdes.deserialize.XjxDeserializationException;
import io.jonasg.xjx.serdes.deserialize.config.XjxConfiguration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public final class TypeMappers {

    static List<Class<Double>> DOUBLE_TYPES = List.of(double.class, Double.class);
    static List<Class<Long>> LONG_TYPES = List.of(long.class, Long.class);
	static List<Class<Integer>> INTEGER_TYPES = List.of(int.class, Integer.class);
    static List<Class<Character>> CHAR_TYPES = List.of(char.class, Character.class);
    static List<Class<Boolean>> BOOLEAN_TYPES = List.of(boolean.class, Boolean.class);

    public static Set<Class<?>> TYPES;

    static {
        TYPES = new HashSet<>();
		TYPES.addAll(INTEGER_TYPES);
        TYPES.addAll(DOUBLE_TYPES);
        TYPES.addAll(LONG_TYPES);
        TYPES.addAll(CHAR_TYPES);
        TYPES.addAll(BOOLEAN_TYPES);
        TYPES.add(String.class);
        TYPES.add(LocalDate.class);
    }

    public static Function<Object, Object> forType(Class<?> type, XjxConfiguration configuration) {
        Function<Object, Object> mapper = Function.identity();
        if (type.equals(String.class)) {
            mapper = String::valueOf;
        }
		if (INTEGER_TYPES.contains(type)) {
            mapper = value -> Integer.parseInt(String.valueOf(value));
        }
        if (LONG_TYPES.contains(type)) {
            mapper = value -> Long.parseLong(String.valueOf(value));
        }
        if (type.equals(BigDecimal.class)) {
            mapper = value -> new BigDecimal(String.valueOf(value));
        }
        if (DOUBLE_TYPES.contains(type)) {
            mapper = value -> Double.valueOf(String.valueOf(value));
        }
        if (CHAR_TYPES.contains(type)) {
            mapper = value -> String.valueOf(value).charAt(0);
        }
        if (BOOLEAN_TYPES.contains(type)) {
            mapper = value -> {
                String lowered = String.valueOf(value).toLowerCase();
                if (lowered.equals("true") || lowered.equals("yes") || lowered.equals("1")) {
                    return true;
                }
                return false;
            };
        }
        if (type.equals(LocalDate.class)) {
            mapper = value -> LocalDate.parse(String.valueOf(value));
        }
        if (type.equals(LocalDateTime.class)) {
            mapper = value -> LocalDateTime.parse(String.valueOf(value));
        }
        if (type.equals(ZonedDateTime.class)) {
            mapper = value -> ZonedDateTime.parse(String.valueOf(value));
        }
        if (type.isEnum()) {
			mapper = value -> {
				Object enumValue = toEnum(type, String.valueOf(value));
				if (enumValue == null && configuration.failOnUnknownEnumValue()) {
					throw new XjxDeserializationException("Cannot map value '" + value + "' to enum " + type.getSimpleName());
				}
				return enumValue;
			};
        }
        return mapper;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Enum<T>> T toEnum(Class<?> type, String value) {
        try {
            T[] enumConstants = (T[]) type.getEnumConstants();
            for (T constant : enumConstants) {
                if (value.equals(constant.name())) {
                    return constant;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
