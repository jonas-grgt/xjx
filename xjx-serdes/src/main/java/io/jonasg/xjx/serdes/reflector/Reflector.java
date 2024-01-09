package io.jonasg.xjx.serdes.reflector;

public class Reflector {

    public static <T> InstanceReflector<T> reflect(T instance) {
        return new InstanceReflector<>(instance);
    }

    public static <T> TypeReflector<T> reflect(Class<T> type) {
        return new TypeReflector<>(type);
    }
}
