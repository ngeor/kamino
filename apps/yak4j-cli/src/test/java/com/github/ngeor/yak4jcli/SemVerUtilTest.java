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
        "1.2.3,          MAJOR, false, 1.2.3",
        "1.2.3,          MINOR, false, 1.2.3",
        "1.2.3,          PATCH, false, 1.2.3",

        "1.2.3,          MAJOR, true,  2.0.0-SNAPSHOT",
        "1.2.3,          MINOR, true,  1.3.0-SNAPSHOT",
        "1.2.3,          PATCH, true,  1.2.4-SNAPSHOT",

        "1.2.3-SNAPSHOT, MAJOR, false, 1.2.3",
        "1.2.3-SNAPSHOT, MINOR, false, 1.2.3",
        "1.2.3-SNAPSHOT, PATCH, false, 1.2.3",

        "1.2.3-SNAPSHOT, MAJOR, true,  1.2.3-SNAPSHOT",
        "1.2.3-SNAPSHOT, MINOR, true,  1.2.3-SNAPSHOT",
        "1.2.3-SNAPSHOT, PATCH, true,  1.2.3-SNAPSHOT",

        "1.0-SNAPSHOT,   MAJOR, false, 1.0.0",
        "1.0-SNAPSHOT,   MAJOR, true,  1.0-SNAPSHOT",
    })
    void testBump(String oldVersion, SemVerBump bump, boolean snapshot, String expectedNewVersion) {
        String actualNewVersion = SemVerUtil.bump(oldVersion, bump, snapshot);
        assertEquals(expectedNewVersion, actualNewVersion);
    }
}
