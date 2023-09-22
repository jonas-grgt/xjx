# Xjx 
Streamlined XML serdes library: No Dependencies, Just Simplicity

# Why
The "why" behind Xjx is rooted in the necessity for a minimalist, actively maintained XML-to-Java and vice versa library. 

# Modules
Xjx exists out of two modules:
- **xjx-sax** a standalone SAX like parser
- **xjx-serdes** XML to Java deserializer and serializer

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

## 🔑 Key Features
- Explicitly map fields to specific tags using `@Tag`
- Explicit deserialization of values using `@ValueDeserialization`

