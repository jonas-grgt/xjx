package io.jonasg.xjx.serdes.reflector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.StringJoiner;

public class FieldReflector {

    private final Field field;

    public FieldReflector(Field field) {
        this.field = field;
    }

    public String name() {
        return field.getName();
    }

    public <T> void set(T instance, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> type() {
        return (Class<T>) field.getType();
    }

    public boolean isOfType(Class<?> type) {
        return type() == type;
    }

    public Field rawField() {
        return field;
    }

    public Type genericType() {
        return field.getGenericType();
    }

    public <T extends Annotation> T getAnnotation(Class<T> clazz) {
        return field.getAnnotation(clazz);
    }

    public <T extends Annotation> boolean isAnnotatedWith(Class<T> annotation) {
        return field.getAnnotation(annotation) != null;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FieldReflector.class.getSimpleName() + "[", "]")
                .add("field=" + field)
                .toString();
    }
}
