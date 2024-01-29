package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SemVerTest {
    @Test
    void testParse() {
        SemVer semVer = SemVer.parse("1.2.3");
        assertThat(semVer).isEqualTo(new SemVer(1, 2, 3));
    }

    @Test
    void testIncreasePatch() {
        SemVer semVer = new SemVer(1, 2, 3);
        SemVer increased = semVer.increasePatch();
        assertThat(increased).isEqualTo(new SemVer(1, 2, 4));
    }

    @Test
    void testIncreaseMinor() {
        SemVer semVer = new SemVer(1, 2, 3);
        SemVer increased = semVer.increaseMinor();
        assertThat(increased).isEqualTo(new SemVer(1, 3, 0));
    }
}
