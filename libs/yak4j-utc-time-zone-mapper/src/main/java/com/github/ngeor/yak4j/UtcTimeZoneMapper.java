package com.github.ngeor.yak4j;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * UTC time zone mapper can map between {@link OffsetDateTime} and
 * {@link LocalDateTime}, keeping the time zone in UTC.
 */
public class UtcTimeZoneMapper {
    /**
     * Converts the given {@link OffsetDateTime} to UTC time zone
     * and then to a {@link LocalDateTime} instance.
     * @param offsetDateTime The offset date time to convert.
     * @return The local date time instance at UTC.
     */
    @SuppressWarnings("WeakerAccess")
    public LocalDateTime asLocalDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null
                ? offsetDateTime.atZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime()
                : null;
    }

    /**
     * Converts the given {@link LocalDateTime} to an {@link OffsetDateTime}
     * instance at UTC time zone.
     * @param localDateTime The local date time instance to convert.
     * @return The offset date time instance at UTC.
     */
    @SuppressWarnings("WeakerAccess")
    public OffsetDateTime asOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }
}
