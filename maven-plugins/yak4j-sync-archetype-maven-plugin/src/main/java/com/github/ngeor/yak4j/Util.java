package com.github.ngeor.yak4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility methods.
 */
final class Util {
    private Util() {}

    /**
     * Combine the two lists into an array.
     *
     * @param a The first list.
     * @param b The second list.
     * @return An array with the contents of both lists.
     */
    static String[] combine(List<String> a, List<String> b) {
        List<String> result = new ArrayList<>(a);
        result.addAll(b);
        result.removeIf(Objects::isNull);
        return result.toArray(new String[0]);
    }

    /**
     * Concatenates the given strings, returning null if either one is null.
     * @param first The first string.
     * @param second The second string.
     * @return The concatenated value.
     */
    static String concatNull(String first, String second) {
        if (first == null || second == null) {
            return null;
        }

        return first + second;
    }

    /**
     * Formats the given map as a collection of maven command line arguments.
     * @param parameters An optional map.
     * @return A collection of command line parameters.
     */
    static List<String> formatParameters(Map<String, String> parameters) {
        if (parameters == null) {
            return Collections.emptyList();
        }

        return parameters.entrySet()
            .stream()
            .map(x -> String.format("-D%s=%s", x.getKey(), x.getValue()))
            .collect(Collectors.toList());
    }
}
