package com.github.ngeor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ArgumentParser {
    private final List<ArgSpec> argSpecs = new ArrayList<>();

    public void addPositionalArgument(String name, boolean required) {
        argSpecs.add(new ArgSpec(name, required, true));
    }

    public Map<String, Object> parse(String[] args) {
        int i = 0;
        List<ArgSpec> candidates = new ArrayList<>(argSpecs);
        Map<String, Object> result = new LinkedHashMap<>();
        while (i < args.length) {
            String arg = args[i];
            if (arg.startsWith("--")) {

            } else {
                ArgSpec argSpec = extract(candidates, ArgSpec::positional).orElseThrow(() -> new IllegalArgumentException("Unexpected argument " + arg));
                result.put(argSpec.name(), arg);
            }

            i++;
        }

        candidates.stream().filter(ArgSpec::required).map(ArgSpec::name).map(s -> String.format("%s is required", s)).forEach(s -> { throw new IllegalArgumentException(s); });

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

    public record ArgSpec(String name, boolean required, boolean positional) {}
}
