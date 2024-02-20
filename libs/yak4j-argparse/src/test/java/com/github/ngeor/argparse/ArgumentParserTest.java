package com.github.ngeor.argparse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArgumentParserTest {
    private final ArgumentParser parser = new ArgumentParser();

    @Test
    void oneRequiredPositionalArgumentPresent() {
        parser.addPositionalArgument("path", true, "");
        Map<String, Object> result = parser.parse(new String[] {"libs"});
        assertThat(result).containsOnlyKeys("path").containsEntry("path", "libs");
    }

    @Test
    void oneRequiredPositionalArgumentMissing() {
        parser.addPositionalArgument("path", true, "");
        assertThatThrownBy(() -> parser.parse(new String[0])).hasMessage("path is required");
    }

    @Test
    void oneRequiredPositionalArgumentSuperfluousArguments() {
        parser.addPositionalArgument("path", true, "");
        assertThatThrownBy(() -> parser.parse(new String[] {"libs", "tests"})).hasMessage("Unexpected argument tests");
    }

    @Test
    void oneOptionalPositionalArgumentMissing() {
        parser.addPositionalArgument("path", false, "");
        Map<String, Object> result = parser.parse(new String[0]);
        assertThat(result).isEmpty();
    }

    @Test
    void oneRequiredNamedArgumentPresent() {
        parser.addNamedArgument("color", true, "");
        Map<String, Object> result = parser.parse(new String[] {"--color", "blue"});
        assertThat(result).containsOnlyKeys("color").containsEntry("color", "blue");
    }

    @Test
    void oneRequiredNamedArgumentMissing() {
        parser.addNamedArgument("color", true, "");
        assertThatThrownBy(() -> parser.parse(new String[0])).hasMessage("color is required");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void oneNamedArgumentValueMissing(boolean required) {
        parser.addNamedArgument("color", required, "");
        assertThatThrownBy(() -> parser.parse(new String[] {"--color"}))
                .hasMessage("No value for named argument color");
    }

    @Test
    void oneRequiredNamedUnexpectedNamedArgument() {
        parser.addNamedArgument("color", true, "");
        assertThatThrownBy(() -> parser.parse(new String[] {"--color", "red", "--flag", "blue"}))
                .hasMessage("Unexpected argument --flag");
    }

    @Test
    void oneOptionalNamedArgumentMissing() {
        parser.addNamedArgument("color", false, "");
        Map<String, Object> result = parser.parse(new String[0]);
        assertThat(result).isEmpty();
    }

    @Test
    void oneFlagArgumentPresent() {
        parser.addFlagArgument("dry-run", "");
        Map<String, Object> result = parser.parse(new String[] {"--dry-run"});
        assertThat(result).containsOnlyKeys("dry-run").containsEntry("dry-run", true);
    }

    @Test
    void oneFlagArgumentMissing() {
        parser.addFlagArgument("dry-run", "");
        Map<String, Object> result = parser.parse(new String[0]);
        assertThat(result).isEmpty();
    }

    @Test
    void normalizeArgument() {
        parser.add(new ArgSpecBuilder("path", SpecKind.POSITIONAL)
                .normalizer(String::toLowerCase)
                .build());
        Map<String, Object> result = parser.parse(new String[] {"Hello"});
        assertThat(result).containsEntry("path", "hello");
    }
}
