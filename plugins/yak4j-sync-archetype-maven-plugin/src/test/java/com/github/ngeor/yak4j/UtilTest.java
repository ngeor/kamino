package com.github.ngeor.yak4j;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Util}.
 */
class UtilTest {

    @Test
    void combine() {
        // arrange
        List<String> a = Collections.singletonList("abc");
        List<String> b = Collections.singletonList("def");

        // act
        String[] result = Util.combine(a, b);

        // assert
        assertThat(result).containsExactly("abc", "def");
    }

    @Test
    void combineRemovesNullElementsFromFirstList() {
        // arrange
        List<String> a = Arrays.asList("abc", null);
        List<String> b = Collections.singletonList("def");

        // act
        String[] result = Util.combine(a, b);

        // assert
        assertThat(result).containsExactly("abc", "def");
    }

    @Test
    void combineRemovesNullElementsFromSecondList() {
        // arrange
        List<String> a = Collections.singletonList("abc");
        List<String> b = Arrays.asList("def", null);

        // act
        String[] result = Util.combine(a, b);

        // assert
        assertThat(result).containsExactly("abc", "def");
    }

    @Test
    void concatBothPresent() {
        // act
        String result = Util.concatNull("-DarchetypeVersion=", "1.2.3");

        // assert
        assertThat(result).isEqualTo("-DarchetypeVersion=1.2.3");
    }

    @Test
    void concatFirstMissing() {
        // act
        String result = Util.concatNull(null, "1.2.3");

        // assert
        assertThat(result).isNull();
    }

    @Test
    void concatSecondMissing() {
        // act
        String result = Util.concatNull("-Dwhatever=", null);

        // assert
        assertThat(result).isNull();
    }

    @Test
    void formatParametersNull() {
        assertThat(Util.formatParameters(null)).isEmpty();
    }

    @Test
    void formatParametersEmpty() {
        assertThat(Util.formatParameters(new LinkedHashMap<>())).isEmpty();
    }

    @Test
    void formatParameters() {
        // arrange
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("color", "blue");

        // act
        List<String> result = Util.formatParameters(parameters);

        // assert
        assertThat(result).containsExactly("-Dcolor=blue");
    }
}
