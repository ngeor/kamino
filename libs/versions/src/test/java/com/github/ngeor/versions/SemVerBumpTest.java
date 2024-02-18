package com.github.ngeor.versions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SemVerBumpTest {
    @Test
    void parse() {
        assertEquals(SemVerBump.MAJOR, SemVerBump.parse("MAJOR"));
        assertEquals(SemVerBump.MAJOR, SemVerBump.parse("major"));
        assertEquals(SemVerBump.MINOR, SemVerBump.parse("MINOR"));
        assertEquals(SemVerBump.MINOR, SemVerBump.parse("minor"));
        assertEquals(SemVerBump.PATCH, SemVerBump.parse("PATCH"));
        assertEquals(SemVerBump.PATCH, SemVerBump.parse("patch"));
        assertNull(SemVerBump.parse("unknown"));
    }

    @Test
    void compare() {
        assertThat(SemVerBump.MAJOR).isGreaterThan(SemVerBump.MINOR);
        assertThat(SemVerBump.MAJOR).isGreaterThan(SemVerBump.PATCH);

        assertThat(SemVerBump.MINOR).isGreaterThan(SemVerBump.PATCH);
        assertThat(SemVerBump.MINOR).isLessThan(SemVerBump.MAJOR);

        assertThat(SemVerBump.PATCH).isLessThan(SemVerBump.MINOR);
        assertThat(SemVerBump.PATCH).isLessThan(SemVerBump.MAJOR);
    }
}
