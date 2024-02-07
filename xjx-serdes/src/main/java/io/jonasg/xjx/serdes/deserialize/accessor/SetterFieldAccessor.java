package io.jonasg.xjx.serdes.deserialize.accessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import io.jonasg.xjx.serdes.reflector.FieldReflector;

public class SetterFieldAccessor implements FieldAccessor {

    private final Object instance;

    private final Method method;

    public SetterFieldAccessor(FieldReflector field, Object instance) {
        String name = field.name();
        this.instance = instance;
        this.method = Arrays.stream(instance.getClass().getMethods())
                .filter(method -> method.getName().equals("set" + name.substring(0, 1).toUpperCase() + name.substring(1)))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void set(Object value) {
        try {
            method.invoke(instance, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasSetterForField() {
        return method != null;
    }
}
