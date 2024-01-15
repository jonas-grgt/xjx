package io.jonasg.xjx.serdes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code Tag} annotation is used to mark a field for XML serialization and deserialization.
 * It provides information about the XML path and optional attributes to be used during serialization and deserialization.
 *
 * <p>Example XML document:</p>
 * <pre>{@code
 * <Products>
 *     <Name>Product 1</Name>
 *     <Name>Product 2</Name>
 *     <Name>Product 3</Name>
 * </Products>
 * }</pre>
 *
 * <p>Example Usage:</p>
 * <pre>{@code
 * @Tag(path = "/Products", items = "Name")
 * List<String> productNames;
 * }</pre>
 * In this example, the {@code List<String>} field 'productNames' will be serialized to and deserialized from the XML path "/Products/Name".
 *
 * <p>Example XML for Serialization:</p>
 * <pre>{@code
 * <Products>
 *     <Name>Product 1</Name>
 *     <Name>Product 2</Name>
 *     <Name>Product 3</Name>
 * </Products>
 * }</pre>
 * In this example, when the {@code List<String>} field 'productNames' is serialized, the generated XML will look like the above representation.
 *
 * <p>Annotation Usage:</p>
 * <ul>
 *   <li>{@code path}: Specifies the Path expression indicating the location of the XML data for serialization and deserialization.</li>
 *   <li>{@code attribute}: Specifies the name of an XML attribute to be used during serialization and deserialization (optional).</li>
 *   <li>{@code items}: Specifies additional information for serializing and deserializing items within a collection (optional).</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Tag {
    /**
     * Specifies the Path expression indicating the location of the XML data for serialization and deserialization.
     *
     * @return The Path expression representing the location of the XML data.
     */
    String path();

    /**
     * Specifies the name of an XML attribute to be used during serialization and deserialization (optional).
     *
     * @return The name of the XML attribute.
     */
    String attribute() default "";

    /**
     * Specifies additional information for serializing and deserializing items within a collection (optional).
     *
     * @return Additional information for serializing and deserializing items.
     */
    String items() default "";
}
