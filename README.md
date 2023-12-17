# Xjx 
Streamlined XML serdes library: No Dependencies, Just Simplicity

# 🤔 Why
The "why" behind Xjx is rooted in the necessity for a minimalist, actively maintained XML-to-Java and vice versa library. 

# 📦 Modules
Xjx exists out of two modules:
- **xjx-sax** a standalone SAX like parser
- **xjx-serdes** XML to Java deserializer and serializer

# 🔑 Key Features
- Explicitly map fields to specific tags using `@Tag`
- Select specific tags using an **XPath** like expression `@Tag(path = "/WeatherData/Location/City)`
- Out of the box support for most common data types
- Explicit deserialization of values using `@ValueDeserialization`

# ✨ xjx-serdes

Contains the XML serializer (TODO) and deserialization code.

## ⚙️ Installation

```xml
<dependency>
    <groupId>io.jonasg</groupId>
    <artifactId>xjx-serdes</artifactId>
    <version>0.1.0</version>
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

    @Tag(path = "/WeatherData/Location/City")
    private String City;

    @Tag(path = "/WeatherData/Location/Country")
    private String Country;
}
```

```java
String document = """
    <?xml version="1.0" encoding="UTF-8"?>
    <WeatherData>
        <Location>
            <City>New York</City>
            <Country>USA</Country>
        </Location>
        <CurrentConditions>
            <Temperature>
                <Value>75</Value>
                <Unit>°F</Unit>
            </Temperature>
            <Humidity>
                <Value>60</Value>
                <Unit>%</Unit>
            </Humidity>
            <WeatherCondition>Sunny</WeatherCondition>
        </CurrentConditions>
    </WeatherData>""";


var weatherData = new XjxSerdes().read(document, WeatherData.class);
```
## General deserialization rules
Deserialization is guided by the use of the `@Tag` annotation. Fields annotated with `@Tag` are candidates for deserialization, while unannotated fields are ignored.

`@Tag` annotation is mandatory for fields considered for deserialization. 
Each `@Tag` annotation must include a `path` property, using an XPath-like expression to map the field within the XML document.

### Path Expressions

Path expressions can be absolute, starting with a slash, representing a path from the root tag to the mapped tag.
Relative paths, without a starting slash, require a parent to be mapped absolutely.

```java
import java.math.BigDecimal;

class Weather {
    // not annotated with @Tag hence is ignored
    String id;
    
    // example for an absolute mapped tag
    @Tag(path = "/WeatherData/Location") 
    Location location;

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

### Attributes


### Collection types
When deserializing XML data containing a collection type, the following conventions apply:

- Only `List` and `Set` types are supported
- The List or Set field should be annotated with `@Tag` having a `path` pointing to the containing tag that holds the repeated tags.
- The nested complex type should be annotated top-level with `@Tag` having a `path` pointing to a single element that is repeated
- Fields within the nested complex type can be annotated as usual.

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

```java
public class WeatherData {
    @Tag(path = "/WeatherData/Forecasts")
    List<Forecast> forecasts;
}

@Tag(path = "/WeatherData/Forecasts/Day")
public class Forecast {
    // field can be both absolutely as relatively mapped
    @Tag(path = "High/Value")
    String maxTemperature;

    // field can be both absolutely as relatively mapped
    @Tag(path = "/WeatherData/Forecasts/Day/Low/Value")
    String minTemperature;
}
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

