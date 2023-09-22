package io.jonasg.xjx.serdes.deserialize;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class ValueDeserializationHandler {

    private static ValueDeserializationHandler instance;

    public static ValueDeserializationHandler getInstance() {
        if (instance == null) {
            instance = new ValueDeserializationHandler();
        }
        return instance;
    }

    public Optional<Object> handle(Field field, String value) {
        ValueDeserialization valueDeserialization = field.getAnnotation(ValueDeserialization.class);
        if (valueDeserialization != null) {
            try {
                var valueDeserializer = valueDeserialization.value().getConstructor().newInstance();
                return Optional.of(valueDeserializer.deserializer(value));
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
        return Optional.empty();
    }
}
