package io.jonasg.xjx.serdes.deserialize;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class MapOf<K, V> {

    protected final Type keyType;

    protected final Type valueType;

    protected MapOf() {
        Type superClass = this.getClass().getGenericSuperclass();
        this.keyType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        this.valueType = ((ParameterizedType) superClass).getActualTypeArguments()[1];
    }

    @SuppressWarnings("unchecked")
    public Class<K> keyType() {
        return (Class<K>) keyType;
    }

    @SuppressWarnings("unchecked")
    public Class<V> valueType() {
        return (Class<V>) valueType;
    }
}
