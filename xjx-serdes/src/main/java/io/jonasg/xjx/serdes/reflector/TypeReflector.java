package io.jonasg.xjx.serdes.reflector;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TypeReflector<T> {

    private final Class<T> clazz;

    private final List<FieldReflector> fields;

    public TypeReflector(Class<T> clazz) {
        this.clazz = clazz;
        this.fields = Arrays.stream(clazz.getDeclaredFields()).map(FieldReflector::new).toList();
    }

    public static <T> TypeReflector<T> reflect(Class<T> clazz) {
        return new TypeReflector<>(clazz);
    }

    public InstanceReflector<T> instanceReflector() {
        return new InstanceReflector<>(clazz, this);
    }

    public Optional<FieldReflector> field(String fieldName) {
        return this.fields.stream()
                .filter(f -> f.name().equals(fieldName))
                .findFirst();
    }

    public List<FieldReflector> fields() {
        return this.fields;
    }

    public <E extends Annotation> E annotation(Class<E> annotation) {
        return clazz.getAnnotation(annotation);
    }
}
