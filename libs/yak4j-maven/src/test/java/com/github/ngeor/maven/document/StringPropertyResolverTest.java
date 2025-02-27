package com.github.ngeor.maven.document;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;

class StringPropertyResolverTest {
    @Test
    void testNoPlaceholders() {
        String result = StringPropertyResolver.resolve("test", ignored -> {
            throw new UnsupportedOperationException("oops");
        });
        assertThat(result).isEqualTo("test");
    }

    @Test
    void testAllResolved() {
        Map<String, String> variables = Map.of("foo", "bar", "company", "acme");
        String result = StringPropertyResolver.resolve("test-${foo}-${company}-${foo}", variables::get);
        assertThat(result).isEqualTo("test-bar-acme-bar");
    }

    @Test
    void testPartialResolved() {
        Map<String, String> variables = Map.of("foo", "bar");
        String result = StringPropertyResolver.resolve("test-${foo}-${company}-${foo}", variables::get);
        assertThat(result).isEqualTo("test-bar-${company}-bar");
    }

    @Test
    void testNoneResolved() {
        Map<String, String> variables = Map.of();
        String result = StringPropertyResolver.resolve("test-${foo}-${company}-${foo}", variables::get);
        assertThat(result).isEqualTo("test-${foo}-${company}-${foo}");
    }

    @Test
    void testResolveMap() {
        Map<String, String> properties =
                new TreeMap<>(Map.of("alpha", "${version}-alpha", "foo", "bar", "version", "1-${foo}"));

        Map<String, String> resolved = StringPropertyResolver.resolve(properties);

        assertThat(resolved)
                .containsOnlyKeys("alpha", "foo", "version")
                .containsEntry("alpha", "1-bar-alpha")
                .containsEntry("foo", "bar")
                .containsEntry("version", "1-bar");
    }
}
