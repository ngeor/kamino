# yak4j-utc-time-zone-mapper

Java utility library to map between OffsetDateTime and LocalDateTime in UTC.

[![Maven Central](https://img.shields.io/maven-central/v/com.github.ngeor/yak4j-utc-time-zone-mapper.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.github.ngeor/yak4j-utc-time-zone-mapper/overview)
[![Build yak4j-utc-time-zone-mapper](https://github.com/ngeor/kamino/actions/workflows/build-libs-yak4j-utc-time-zone-mapper.yml/badge.svg)](https://github.com/ngeor/kamino/actions/workflows/build-libs-yak4j-utc-time-zone-mapper.yml)
[![javadoc](https://javadoc.io/badge2/com.github.ngeor/yak4j-utc-time-zone-mapper/javadoc.svg)](https://javadoc.io/doc/com.github.ngeor/yak4j-utc-time-zone-mapper)

## Usage

The package contains currently only one class, `UtcTimeZoneMapper`.

```java
import com.github.ngeor.yak4j.UtcTimeZoneMapper;

UtcTimeZoneMapper mapper = new UtcTimeZoneMapper();

// map from OffsetDateTime to LocalDateTime
LocalDateTime localDateTime = mapper.asLocalDateTime(OffsetDateTime.now());

// map from LocalDateTime to OffsetDateTime
OffsetDateTime offsetDateTime = mapper.asOffsetDateTime(localDateTime);
```
