package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class SimpleStringTemplateTest {
    @Test
    void test() {
        SimpleStringTemplate template = new SimpleStringTemplate("Hello, $name!");
        String result = template.render(Map.of("name", "John"));
        assertThat(result).isEqualTo("Hello, John!");
    }
}
