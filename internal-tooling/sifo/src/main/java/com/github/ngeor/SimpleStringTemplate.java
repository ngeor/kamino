package com.github.ngeor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public final class SimpleStringTemplate {
    private final String template;

    public SimpleStringTemplate(String template) {
        this.template = template;
    }

    public static SimpleStringTemplate ofResource(String resourceName) throws IOException {
        try (InputStream is = Objects.requireNonNull(
                SimpleStringTemplate.class.getResourceAsStream(resourceName),
                "Could not load resource: " + resourceName)) {
            return new SimpleStringTemplate(new String(is.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    public String render(Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replaceAll("\\$" + entry.getKey(), entry.getValue());
        }
        return result;
    }
}
