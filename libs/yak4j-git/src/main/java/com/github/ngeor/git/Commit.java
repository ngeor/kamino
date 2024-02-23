package com.github.ngeor.git;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Commit(String sha, LocalDate authorDate, String tag, String summary) {
    private static final Pattern pattern = Pattern.compile(
            "^(?<sha>[0-9a-f]+)\\|(?<date>\\d{4}-\\d{2}-\\d{2})\\|.*?(tag:\\s*(?<tag>.+))?\\|(?<summary>.+)$");

    public static Optional<Commit> parse(String line) {
        if (line == null) {
            return Optional.empty();
        }

        Matcher matcher = pattern.matcher(line);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        LocalDate authorDate;
        try {
            authorDate = LocalDate.parse(matcher.group("date"));
        } catch (DateTimeParseException ignored) {
            return Optional.empty();
        }

        return Optional.of(
                new Commit(matcher.group("sha"), authorDate, matcher.group("tag"), matcher.group("summary")));
    }
}
