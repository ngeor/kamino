# yak4j-xml

XML utilities.

[![Maven Central](https://img.shields.io/maven-central/v/com.github.ngeor/yak4j-xml.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.github.ngeor/yak4j-xml/overview)
[![Build yak4j-xml](https://github.com/ngeor/kamino/actions/workflows/build-libs-yak4j-xml.yml/badge.svg)](https://github.com/ngeor/kamino/actions/workflows/build-libs-yak4j-xml.yml)
[![javadoc](https://javadoc.io/badge2/com.github.ngeor/yak4j-xml/javadoc.svg)](https://javadoc.io/doc/com.github.ngeor/yak4j-xml)

## Usage

You can easily serialize and (de)serialize objects into XML with
`XmlSerializer`.

Any checked `JAXBException` will be wrapped inside the runtime (unchecked)
`XmlRuntimeException`.

```java
class Demo {
    void serialize() {
        XmlSerializer serializer = new XmlSerializer();
        String xml = serializer.serialize(myObject, MyObject.class);
    }
}
```

```java
class Demo {
    void deserialize() {
        XmlSerializer serializer = new XmlSerializer();
        String xml = "<MyObject><Name>hello, world</Name></MyObject>";
        MyObject myObject = serializer.deserialize(xml, MyObject.class);
    }
}
```
