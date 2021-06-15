package com.github.ngeor.yak4jcli;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link SemVerUtil}.
 */
class SemVerUtilTest {
    @ParameterizedTest
    @CsvSource(value = {
        "1.0.0, 1.1.0",
        "2.3.4, 2.4.0",
        "1.0-SNAPSHOT, 1.1.0"
    })
    void testBump(String oldVersion, String expectedNewVersion) {
        String actualNewVersion = SemVerUtil.bump(oldVersion);
        assertEquals(expectedNewVersion, actualNewVersion);
    }
}
