package io.jonasg.xjx.serdes.reflector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class InstanceReflector<T> {

    private final Class<T> type;
    private final TypeReflector<T> typeReflector;

    private final T instance;

    public InstanceReflector(Class<T> type, TypeReflector<T> typeReflector) {
        this.type = type;
        this.typeReflector = typeReflector;
        this.instance = newInstance();
    }

    @SuppressWarnings("unchecked")
    public InstanceReflector(T instance) {
        this.type = (Class<T>) instance.getClass();
        this.typeReflector = TypeReflector.reflect((Class<T>)instance.getClass());
        this.instance = instance;
    }

    @SuppressWarnings("unchecked")
    private T newInstance() {
        Constructor<?> constructor = Arrays.stream(type.getDeclaredConstructors())
                .filter(c -> c.getParameters().length == 0)
                .findFirst()
                .orElseThrow();
        try {
            return (T) constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            constructor.setAccessible(true);
            T instance;
            try {
                instance = (T) constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
            constructor.setAccessible(false);
            return instance;
        }
    }

    public void setField(String fieldName, Object value) {
        typeReflector.field(fieldName)
                .ifPresent(f -> f.set(instance, value));
    }

    public List<InstanceField> fields(Predicate<InstanceField> predicate) {
        return typeReflector.fields()
                .stream()
                .map(f -> new InstanceField(f, instance))
                .filter(predicate)
                .toList();
    }

    public T instance() {
        return instance;
    }
}
