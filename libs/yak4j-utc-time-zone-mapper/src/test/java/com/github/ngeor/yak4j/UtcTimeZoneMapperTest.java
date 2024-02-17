package com.github.ngeor.yak4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link UtcTimeZoneMapper}.
 */
class UtcTimeZoneMapperTest {
    private UtcTimeZoneMapper mapper;

    @BeforeEach
    void before() {
        mapper = new UtcTimeZoneMapper();
    }

    @Test
    void asLocalDateTimeNullIsNull() {
        assertNull(mapper.asLocalDateTime(null));
    }

    @Test
    void asLocalDateTime() {
        OffsetDateTime input = OffsetDateTime.now();
        LocalDateTime expected = input.atZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime actual = mapper.asLocalDateTime(input);
        assertEquals(expected, actual);
    }

    @Test
    void asOffsetDateTimeNullIsNull() {
        assertNull(mapper.asOffsetDateTime(null));
    }

    @Test
    void asOffsetDateTime() {
        LocalDateTime input = LocalDateTime.now();
        OffsetDateTime expected = input.atOffset(ZoneOffset.UTC);
        OffsetDateTime actual = mapper.asOffsetDateTime(input);
        assertEquals(expected, actual);
    }
}
