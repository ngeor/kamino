package com.github.ngeor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}
