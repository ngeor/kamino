package com.github.ngeor.arturito;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public final class ArgParser {
    private ArgParser() {}

    private static final String PREFIX = "--";

    public static Map<String, String> parse(String[] args) {
        Objects.requireNonNull(args);
        Map<String, String> result = new HashMap<>();
        String lastName = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            Validate.notBlank(arg, "Unexpected blank argument at index %d", i);
            if (arg.startsWith(PREFIX)) {
                lastName = arg.substring(PREFIX.length());
                Validate.notBlank(lastName, "Argument %s was blank after prefix removal at index %d", arg, i);
                result.put(lastName, "");
            } else {
                Validate.isTrue(lastName != null, "Unexpected positional argument %s at index %d", arg, i);
                result.put(lastName, arg);
                lastName = null;
            }
        }

        return result;
    }
}
