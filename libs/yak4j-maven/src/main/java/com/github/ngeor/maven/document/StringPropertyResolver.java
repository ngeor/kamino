package com.github.ngeor.maven.document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public final class StringPropertyResolver {
    private StringPropertyResolver() {}

    public static String resolve(String value, UnaryOperator<String> resolver) {
        if (value == null) {
            return value;
        }
        StringBuilder builder = new StringBuilder(value);
        int fromIndex = 0;
        int index = builder.indexOf("${", fromIndex);
        while (index >= 0) {
            int closeIndex = builder.indexOf("}", index);
            if (closeIndex == -1) {
                throw new IllegalStateException("Unclosed property");
            }
            String variable = builder.substring(index + 2, closeIndex);
            if (variable.isEmpty()) {
                throw new IllegalStateException("Empty variable");
            }
            String resolvedValue = resolver.apply(variable);
            if (resolvedValue != null) {
                builder.replace(index, closeIndex + 1, resolvedValue);
                index = builder.indexOf("${", index + resolvedValue.length());
            } else {
                index = builder.indexOf("${", closeIndex + 1);
            }
        }
        return builder.toString();
    }

    public static Map<String, String> resolve(Map<String, String> properties) {
        Map<String, String> resolvedProperties = new HashMap<>(properties);
        LinkedList<String> keys = new LinkedList<>(properties.keySet());
        while (!keys.isEmpty()) {
            String key = keys.removeFirst();
            resolveOne(resolvedProperties, key, Set.of(key));
        }
        return resolvedProperties;
    }

    private static String resolveOne(Map<String, String> properties, String key, Set<String> stack) {
        String value = properties.get(key);
        var result = resolve(value, k -> {
            if (stack.contains(k)) {
                throw new IllegalStateException("Cyclical property");
            }

            Set<String> newStack = new HashSet<>(stack);
            newStack.add(k);

            return resolveOne(properties, k, newStack);
        });
        properties.put(key, result);
        return result;
    }
}
