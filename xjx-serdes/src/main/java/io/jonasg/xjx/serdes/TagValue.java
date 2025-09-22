package io.jonasg.xjx.serdes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code TagValue} annotation is used to mark an object for XML deserialization.
 * It provides information about the XML path to be used during deserialization.
 *
 * <p>Example XML document:</p>
 * <pre>{@code
 * <Products>
 *     <Name id="1">Product 1</Name>
 *     <Name id="2">Product 2</Name>
 *     <Name id="3">Product 3</Name>
 * </Products>
 * }</pre>
 *
 * <p>Example Usage:</p>
 * <pre>{@code
 * @Tag(path = "/Products", items = "Name")
 * List<Name> productNames;
 *
 * ...
 *
 * class Name {
 *
 *    @TagValue
 *    String value;
 *
 * }
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface TagValue {

}
