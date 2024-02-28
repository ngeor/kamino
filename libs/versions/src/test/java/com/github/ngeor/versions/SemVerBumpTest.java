package com.github.ngeor.versions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SemVerBumpTest {
    @ParameterizedTest
    @ValueSource(strings = {"MAJOR", "major", "MINOR", "minor", "PATCH", "patch"})
    void parse(String input) {
        assertThat(SemVerBump.parse(input)).isEqualTo(SemVerBump.valueOf(input.toUpperCase()));
    }

    @Test
    void parseFail() {
        assertThatThrownBy(() -> SemVerBump.parse("unknown")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void compare() {
        assertThat(SemVerBump.MAJOR).isGreaterThan(SemVerBump.MINOR).isGreaterThan(SemVerBump.PATCH);

        assertThat(SemVerBump.MINOR).isGreaterThan(SemVerBump.PATCH).isLessThan(SemVerBump.MAJOR);

        assertThat(SemVerBump.PATCH).isLessThan(SemVerBump.MINOR).isLessThan(SemVerBump.MAJOR);
    }
}
