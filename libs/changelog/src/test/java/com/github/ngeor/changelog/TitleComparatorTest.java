package com.github.ngeor.changelog;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TitleComparatorTest {
    private final TitleComparator comparator = new TitleComparator("Unreleased");

    @ParameterizedTest
    @CsvSource({
        "abc, def",
        "9.6.3, 10.5.3",
        "1.0.0, 1.1.0",
        "[1.0.0], 1.1.0",
        "1.2.0, [2.0.0]",
        "[3.0.0], [3.1.0] - whatever",
        "1.2.3, Unreleased"
    })
    void testLess(String left, String right) {
        assertThat(comparator.compare(left, right)).isNegative();
    }

    @ParameterizedTest
    @CsvSource({
        "abc, abc",
        "unreleased, Unreleased",
        "Unreleased, [Unreleased]",
        "1.2.3, 1.2.3",
        "1.2.3, 1.2.3 - whatever",
        "[1.2.3], 1.2.3",
        "1.2.3, [1.2.3]",
        "[1.2.4], [1.2.4]",
        "[2.3.5], [2.3.5] - extra text"
    })
    void testEqual(String left, String right) {
        assertThat(comparator.compare(left, right)).isZero();
    }
}
