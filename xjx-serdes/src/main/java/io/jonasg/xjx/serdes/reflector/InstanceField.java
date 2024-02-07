package io.jonasg.xjx.serdes.reflector;

import java.lang.annotation.Annotation;
import java.util.StringJoiner;

public class InstanceField {
    private final FieldReflector fieldReflector;
    private final Object instance;

    public <T> InstanceField(FieldReflector fieldReflector, T instance) {
        this.fieldReflector = fieldReflector;
        this.instance = instance;
    }

    public <A extends Annotation> boolean hasAnnotation(Class<A> annotation) {
        return fieldReflector.hasAnnotation(annotation);
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return fieldReflector.getAnnotation(type);
    }

    public Object getValue() {
        try {
            fieldReflector.rawField().setAccessible(true);
            Object value = fieldReflector.rawField().get(instance);
            fieldReflector.rawField().setAccessible(false);
            return value;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> type() {
        return fieldReflector.type();
    }

	public InstanceReflector<Object> reflect() {
		return new InstanceReflector<>(getValue());
	}

    @Override
    public String toString() {
        return new StringJoiner(", ", InstanceField.class.getSimpleName() + "[", "]")
                .add("fieldReflector=" + fieldReflector)
                .add("instance=" + instance)
                .toString();
    }
}
