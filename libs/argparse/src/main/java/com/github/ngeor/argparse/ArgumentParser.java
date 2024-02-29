package com.github.ngeor.argparse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class ArgumentParser {
    private final List<ArgSpec> argSpecs = new ArrayList<>();

    public void addPositionalArgument(String name, boolean required, String description) {
        add(new ArgSpecBuilder(name, SpecKind.POSITIONAL)
                .required(required)
                .description(description)
                .build());
    }

    public void addNamedArgument(String name, boolean required, String description) {
        add(new ArgSpecBuilder(name, SpecKind.NAMED)
                .required(required)
                .description(description)
                .build());
    }

    public void addFlagArgument(String name, String description) {
        add(new ArgSpecBuilder(name, SpecKind.FLAG).description(description).build());
    }

    public void add(ArgSpec argSpec) {
        argSpecs.add(Objects.requireNonNull(argSpec));
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
                        result.put(argSpec.name(), argSpec.normalize(value));
                    } else {
                        throw new IllegalStateException("No value for named argument " + argName);
                    }
                }
            } else {
                ArgSpec argSpec = extract(candidates, a -> a.kind() == SpecKind.POSITIONAL)
                        .orElseThrow(() -> new IllegalArgumentException("Unexpected argument " + arg));
                result.put(argSpec.name(), argSpec.normalize(arg));
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

    public void printHelp() {
        for (ArgSpec argSpec : argSpecs) {
            printHelp(argSpec);
        }
    }

    @SuppressWarnings("java:S106")
    private void printHelp(ArgSpec argSpec) {
        switch (argSpec.kind()) {
            case FLAG -> System.out.printf("[--%s] %s%n", argSpec.name(), argSpec.description());
            case NAMED -> System.out.printf(
                    argSpec.required() ? "--%s VALUE %s%n" : "[--%s VALUE] %s%n",
                    argSpec.name(),
                    argSpec.description());
            case POSITIONAL -> System.out.printf(
                    argSpec.required() ? "%s %s%n" : "[%s] %s%n", argSpec.name(), argSpec.description());
        }
    }
}
