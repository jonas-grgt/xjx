package io.jonasg.xjx.serdes.deserialize.accessor;

import io.jonasg.xjx.serdes.reflector.FieldReflector;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

public interface FieldAccessor {

    List<Class<Double>> DOUBLE_TYPES = List.of(double.class, Double.class);
    List<Class<Long>> LONG_TYPES = List.of(long.class, Long.class);
    List<Class<Character>> CHAR_TYPES = List.of(char.class, Character.class);

    static FieldAccessor of(FieldReflector field, Object instance) {
        var setterFieldAccessor = new SetterFieldAccessor(field, instance); //TODO optimize
        if (setterFieldAccessor.hasSetterForField()) {
            return new SetterFieldAccessor(field, instance);
        }
        Function<Object, Object> mapper = Function.identity();
        if (field.type().equals(String.class)) {
            mapper = String::valueOf;
        }
        if (field.type().equals(Integer.class)) {
            mapper = value -> Integer.parseInt(String.valueOf(value));
        }
        if (LONG_TYPES.contains(field.type())) {
            mapper = value -> Long.parseLong(String.valueOf(value));
        }
        if (field.type().equals(BigDecimal.class)) {
            mapper = value -> new BigDecimal(String.valueOf(value));
        }
        if (DOUBLE_TYPES.contains(field.type())) {
            mapper = value -> Double.valueOf(String.valueOf(value));
        }
        if (CHAR_TYPES.contains(field.type())) {
            mapper = value -> String.valueOf(value).charAt(0);
        }
        if (field.type().equals(LocalDate.class)) {
            mapper = value -> LocalDate.parse(String.valueOf(value));
        }
        if (field.type().equals(LocalDateTime.class)) {
            mapper = value -> LocalDateTime.parse(String.valueOf(value));
        }
        if (field.type().equals(ZonedDateTime.class)) {
            mapper = value -> ZonedDateTime.parse(String.valueOf(value));
        }
        if (field.type().isEnum()) {
            mapper = value -> toEnum(field.type(), String.valueOf(value));
        }
        return new ReflectiveFieldAccessor(field, instance, mapper);
    }

    void set(Object value);

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
