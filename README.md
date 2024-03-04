# 🙅 Xjx 
Java - XML serializing and deserializing (serdes) library: No Dependencies, Just Simplicity

# 🤔 Why
The "why" behind Xjx is rooted in the necessity for a minimalist, actively maintained XML-to-Java and vice versa library. 

# 📦 Modules
Xjx exists out of two modules:
- **xjx-sax** a standalone SAX like parser
- **xjx-serdes** XML to Java deserializer and serializer

# 🔑 Key Features
- Explicitly map fields to specific tags using `@Tag`
- Select specific tags using an **XPath** like expression `@Tag(path = "/WeatherData/Location/City)`
- Out-of-the-box support for most common data types (including enums, collections, and maps)
- Explicit deserialization of values using `@ValueDeserialization`
- Support for records

# ✨ xjx-serdes

Contains the XML serializer and deserializer.

## ⚙️ Installation

```xml
<dependency>
    <groupId>io.jonasg</groupId>
    <artifactId>xjx-serdes</artifactId>
    <version>${xjx.version}</version>
</dependency>
```

## 🔆 Quick sample usage
Getters and setters are omitted for brevity, 
but setters are **not required** as Xjx can also reside to using reflection
but will **always** favor direct access through setters over it.
```java
public class WeatherData {
    private WeatherData() {
    }

    @Tag(path = "/WeatherData/Location")
    private Location location;

    @Tag(path = "/WeatherData/CurrentConditions/Temperature/Value")
    private Integer temperature;
}

public class Location {
    private Location() {
    }

    @Tag(path = "Country", attribute = "City")
    private String City;

    @Tag(path = "Country")
    private String Country;
}
```

```java
String document = """
    <?xml version="1.0" encoding="UTF-8"?>
    <xjx:WeatherData xmlns:xjx="https://github.com/jonas-grgt/xjx">
      <xjx:Location>
          <xjx:Country City="New York">USA</xjx:Country>
      </xjx:Location>
      <xjx:CurrentConditions>
        <xjx:Temperature>
          <xjx:Value>75</Value>
            <xjx:Unit><![CDATA[°F]]></xjx:Unit>
        </xjx:Temperature>
        <xjx:Humidity>
          <xjx:Value>60</xjx:Value>
          <xjx:Unit>%</xjx:Unit>
        </xjx:Humidity>
        <xjx:WeatherCondition>Sunny</xjx:WeatherCondition>
      </xjx:CurrentConditions>
    </xjx:WeatherData>""";


var xjx = new XjxSerdes();
WeatherData weatherData = xjx.read(document, WeatherData.class);

String xmlDocument = xjx.write(weatherData);
```
## General deserialization rules
Deserialization is guided by the use of the `@Tag` annotation. Fields annotated with `@Tag` are candidates for deserialization, while unannotated fields are ignored.

`@Tag` annotation is mandatory for fields considered for deserialization. 
Each `@Tag` annotation must include a `path` property, using an XPath-like expression to map the field within the XML document.

### Path Expressions

Path expressions can be **absolute**, starting with a slash, representing a path from the root tag to the mapped tag.
**Relative** paths, without a starting slash, require a parent to be mapped absolutely.
Root mappings can be placed top-level on the class, all subsequent relative mappings are relative to the root mapping. 

```java
import java.math.BigDecimal;

@Tag(path = "/WeatherData")
class Weather {
    // not annotated with @Tag hence is ignored
    String id;
    
    // example for an absolute mapped tag
    @Tag(path = "/WeatherData/Location") 
    Location location;
	
	// example for a relative-mapped tag based upon the top-level mapping
	@Tag(path = "CurrentConditions")
	Conditions conditions;

    // normally all fields without @Tag are ignored, yet this field is 
    // taken into account because at least one of its child fields is
    // annotated with @Tag
    Temperature temperature;
}

class Location {
    // example for a relative mapped tag
    @Tag(path = "city")
    String city;
    
    // a combination of relative and absolute mapped tags is possible
    // within a nested object
    @Tag(path = "/WeatherData/CurrenConditions/CurrenConditions")
    String condition;
}

class Temperature {
    // absolute mapped tag
    @Tag(path = "/WeatherData/CurrenConditions/Temperature")
    BigDecimal max;
}
```

Absolute mapping a field of top-level class containing a top-level root mapping is supported.
```java
@Tag(path = "/WeatherData")
class Weather {
    @Tag(path = "/WeatherData/Location")
    Location location;
}

```

### Attributes

Attributes can be mapped using the `attribute` property of the `@Tag` annotation.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Person>
	<Name age="18" sex="MALE">John</Name>
</Person>
```

```java
public class Person {
    @Tag(path = "/Person/Name")
    String name;

    @Tag(path = "/Person/Name", attribute = "sex")
    String sex;

    @Tag(path = "/Person/Name", attribute = "age")
    int age;
}
```

### Enum types

Xjx offers straightforward and efficient deserialization support for Enum types. 
When mapping XML character data to Enum fields in Java, Xjx matches the character data with the names of the Enum constants.
Deserialization Rules for Enums

- Direct Name Matching: The deserializer matches the XML character data directly with the names of the Enum constants. The match is case-sensitive.
- Defaulting to Null: If the XML character data does not match any Enum constant names, the field is set to null. This is the default behavior when a match cannot be established.

### Collection types
When deserializing an XML document containing repeated elements, it can be mapped onto one of the collection types `List` or `Set`.

The following conventions should be followed:

- Only `List` and `Set` types are supported for mapping repeated elements.
- The `@Tag` annotation should be used on a `List` or `Set` field.
    - Include a `path` attribute pointing to the containing tag that holds the repeated tags.
    - Include an `items` attribute pointing to the repeated tag, relatively.
    - The `path` attribute supports both relative and absolute paths.
- The generic argument can be any standard simple type (e.g., `String`, `Boolean`, `Double`, `Long`, etc.) or a custom complex type.
- Fields within the nested complex type can be annotated as usual, using relative or absolute paths.

Example XML document:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<WeatherData>
  <Forecasts>
    <Day Date="2023-09-12">
      <High>
        <Value>71</Value>
      </High>
      <Low>
        <Value>62</Value>
      </Low>
      </Day>
        <Day Date="2023-09-13">
          <High>
            <Value>78</Value>
          </High>
          <Low>
             <Value>71</Value>
          </Low>
        </Day>
    </Forecasts>
</WeatherData>
```

### Map types

Maps can be deserialized either as a field or a top-level type. Consider the following XML document:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<WeatherData>
  <CurrentConditions>
     <Temperature>
        <Value>75</Value>
          <Unit>°F</Unit>
      </Temperature>
  </CurrentConditions>
</WeatherData>
```

#### Option 1: Map a Specific Section

You can map a specific section from the XML onto a custom field:

```java
class WeatherData {
    @Tag(path = "/WeatherData/CurrentConditions")
    Map<String, Object> map;
}
```
In this case, the map field will contain:
```java
Map.of("Temperature", Map.of("Value", "75", "Unit", "°F"));
```

#### Option 2: Map the Whole Document

Alternatively, you can map the entire document onto a Map of String Object
```java
class WeatherData {
    @Tag(path = "/WeatherData")
    Map<String, Object> map;
}
```
In this case, the map field will contain:
```java
Map.of("CurrentConditions", 
    Map.of("Temperature", Map.of("Value", "75", "Unit", "°F"))));
```

#### Option 3: Map to a Map
```java
Map<String, Object> map = new XjxSerdes().read(document, new MapOf<>() {});
```
In this case, the result of `read` will contain a Map of String Object
```java
Map.of("CurrentConditions", 
    Map.of("Temperature", Map.of("Value", "75", "Unit", "°F"))));
```

## General serialization rules

Fields annotated with `@Tag` are considered for serialization, while unannotated fields are ignored.
### Path Expressions
Fields are serialized based on the path property specified in the @Tag annotation. 
The path property uses an XPath-like expression to determine the location of the field within the XML document.

```java
class WeatherData {
    @Tag(path = "/WeatherData/Location/Country")
    private final String country;

    @Tag(path = "/WeatherData/Location/City/Name")
    private final String city;

    // Constructor and other methods are omitted for brevity
}
```

Given that the above object is fully populated

```java
var weatherData = new WeatherData("Belgium", "Ghent");
```
The serialized result
```java
new XjxSerdes().write(weatherData);
```
Would look like:
```xml
<Weatherdata>
  <Location>
    <Country>Belgium</Country>
      <City>
       <Name>Ghent</Name>
      </City>
  </Location>
</Weatherdata>
```

## Null Fields

Null fields are serialized as self-closing tags by default.
If a field is null, the corresponding XML tag is included, but the tag content is empty.
