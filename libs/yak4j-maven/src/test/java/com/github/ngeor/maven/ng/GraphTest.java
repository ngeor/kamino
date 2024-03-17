package com.github.ngeor.maven.ng;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GraphTest {
    private final Graph<String> graph = new Graph<>();

    @Test
    void putFromNullIsNotAllowed() {
        assertThatThrownBy(() -> graph.put(null, "AMS")).isInstanceOf(NullPointerException.class);
    }

    @Test
    void putToNullIsNotAllowed() {
        assertThatThrownBy(() -> graph.put("ATH", null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void putSameFromToIsNotAllowed() {
        assertThatThrownBy(() -> graph.put("AMS", "AMS")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void hasDirectPath() {
        // arrange
        graph.put("ATH", "AMS");

        // act and assert
        assertThat(graph.hasDirectPath("ATH", "AMS")).isTrue();
        assertThat(graph.hasDirectPath("AMS", "ATH")).isFalse();
    }

    @Test
    void visitDirectAndIndirectDestinations() {
        // arrange
        graph.put("ATH", "SKG");
        graph.put("ATH", "AMS");
        graph.put("AMS", "ATH");
        graph.put("AMS", "JFK");
        graph.put("LHR", "AMS");
        graph.put("LHR", "ATH");
        Set<String> result = new HashSet<>();

        // act
        graph.visit("ATH", result::add);

        // assert
        assertThat(result).containsExactlyInAnyOrder(
            "SKG",
            "AMS",
            "JFK"
        );
    }
}
