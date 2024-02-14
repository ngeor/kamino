package com.github.ngeor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class ArgumentParser {
    private final List<ArgSpec> argSpecs = new ArrayList<>();

    public void addPositionalArgument(String name, boolean required) {
        add(new ArgSpec(name, required, SpecKind.POSITIONAL));
    }

    public void addNamedArgument(String name, boolean required) {
        add(new ArgSpec(name, required, SpecKind.NAMED));
    }

    public void addFlagArgument(String name) {
        add(new ArgSpec(name, false, SpecKind.FLAG));
    }

    public void add(ArgSpec argSpec) {
        argSpecs.add(argSpec);
    }

    public Map<String, Object> parse(String[] args) {
        int i = 0;
        List<ArgSpec> candidates = new ArrayList<>(argSpecs);
        Map<String, Object> result = new LinkedHashMap<>();
        while (i < args.length) {
            String arg = args[i];
            if (arg.startsWith("--")) {
                String argName = arg.substring(2);
                if (argName.isBlank()) {
                    throw new IllegalArgumentException("Unsupported -- argument");
                }
                ArgSpec argSpec = extract(
                                candidates, a -> a.kind() != SpecKind.POSITIONAL && argName.equalsIgnoreCase(a.name()))
                        .orElseThrow(() -> new IllegalArgumentException("Unexpected argument " + arg));

                if (argSpec.kind() == SpecKind.FLAG) {
                    result.put(argSpec.name(), Boolean.TRUE);
                } else {
                    i++;

                    if (i < args.length) {
                        String value = args[i];
                        result.put(argSpec.name(), value);
                    } else {
                        throw new IllegalStateException("No value for named argument " + argName);
                    }
                }
            } else {
                ArgSpec argSpec = extract(candidates, a -> a.kind() == SpecKind.POSITIONAL)
                        .orElseThrow(() -> new IllegalArgumentException("Unexpected argument " + arg));
                result.put(argSpec.name(), arg);
            }

            i++;
        }

        candidates.stream()
                .filter(ArgSpec::required)
                .map(ArgSpec::name)
                .map(s -> String.format("%s is required", s))
                .forEach(s -> {
                    throw new IllegalArgumentException(s);
                });

        return result;
    }

    private static Optional<ArgSpec> extract(List<ArgSpec> list, Predicate<ArgSpec> predicate) {
        for (int i = 0; i < list.size(); i++) {
            if (predicate.test(list.get(i))) {
                return Optional.of(list.remove(i));
            }
        }
        return Optional.empty();
    }
}
