package com.github.ngeor.yak4jdom;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;

class StringIntMapTest {
    @Test
    void testCreateAlreadySorted() {
        StringIntMap map = new StringIntMap("a", "b", "c");
        assertThat(map.isEmpty()).isFalse();
        assertThat(map.get("a")).isZero();
        assertThat(map.get("b")).isEqualTo(1);
        assertThat(map.get("c")).isEqualTo(2);
        assertThat(map.get("d")).isNegative();
    }

    @Test
    void testCreateNotPreSorted() {
        StringIntMap map = new StringIntMap("c", "b", "a");
        assertThat(map.isEmpty()).isFalse();
        assertThat(map.get("c")).isZero();
        assertThat(map.get("b")).isEqualTo(1);
        assertThat(map.get("a")).isEqualTo(2);
        assertThat(map.get("d")).isNegative();
    }

    @Test
    void testRemoveExistingItem() {
        StringIntMap map = new StringIntMap("s", "u", "p", "e", "r");
        assertThat(map.isEmpty()).isFalse();
        assertThat(map.get("s")).isZero();
        assertThat(map.get("u")).isEqualTo(1);
        assertThat(map.get("p")).isEqualTo(2);
        assertThat(map.get("e")).isEqualTo(3);
        assertThat(map.get("r")).isEqualTo(4);
        map.remove("u");
        assertThat(map.get("s")).isZero();
        assertThat(map.get("u")).isNegative();
        assertThat(map.get("p")).isEqualTo(2);
        assertThat(map.get("e")).isEqualTo(3);
        assertThat(map.get("r")).isEqualTo(4);
    }

    @Test
    void testRemoveNonExistingItem() {
        StringIntMap map = new StringIntMap("s", "u", "p", "e", "r");
        map.remove("x");
        assertThat(map.get("s")).isZero();
        assertThat(map.get("u")).isEqualTo(1);
        assertThat(map.get("p")).isEqualTo(2);
        assertThat(map.get("e")).isEqualTo(3);
        assertThat(map.get("r")).isEqualTo(4);
    }

    @Test
    void testIsEmpty() {
        StringIntMap map = new StringIntMap("xyz", "abc");
        assertThat(map.isEmpty()).isFalse();
        map.remove("abc");
        assertThat(map.isEmpty()).isFalse();
        map.remove("xyz");
        assertThat(map.isEmpty()).isTrue();
        Set.of("xyz", "abc").forEach(x -> assertThat(map.get(x)).isNegative());
    }
}
