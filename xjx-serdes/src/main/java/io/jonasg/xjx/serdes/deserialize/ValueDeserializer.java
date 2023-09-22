package io.jonasg.xjx.serdes.deserialize;

public interface ValueDeserializer<T> {
    T deserializer(String value);
}
