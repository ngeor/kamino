package com.github.ngeor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class StringTemplate {
    private final String template;

    public StringTemplate(String template) {
        this.template = template;
    }

    public static StringTemplate ofResource(String resourceName) throws IOException {
        try (InputStream is = StringTemplate.class.getResourceAsStream(resourceName)) {
            return new StringTemplate(new String(is.readAllBytes(), StandardCharsets.UTF_8));
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
