package io.jonasg.xjx.serdes.deserialize.accessor;

import java.util.function.Function;

import io.jonasg.xjx.serdes.reflector.FieldReflector;

public class ReflectiveFieldAccessor implements FieldAccessor {

    private final FieldReflector field;
    private final Object instance;
    private final Function<Object, Object> mapper;

    public ReflectiveFieldAccessor(FieldReflector field, Object instance, Function<Object, Object> mapper) {
        this.field = field;
        this.instance = instance;
        this.mapper = mapper;
    }

    @Override
    public void set(Object value) {
        field.set(instance, mapper.apply(value));
    }
}
