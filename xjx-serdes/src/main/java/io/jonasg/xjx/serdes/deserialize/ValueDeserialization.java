package io.jonasg.xjx.serdes.deserialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValueDeserialization {

    Class<? extends ValueDeserializer<?>> value();

}
