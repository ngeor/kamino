package com.github.ngeor;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public record Commit(String sha, LocalDate authorDate, String tag, String summary) {
    public static Optional<Commit> parse(String line) {
        if (line == null) {
            return Optional.empty();
        }

        String[] parts = line.split("\\|", 4);
        if (parts.length != 4) {
            return Optional.empty();
        }

        LocalDate authorDate;
        try {
            authorDate = LocalDate.parse(parts[1]);
        } catch (DateTimeParseException ignored) {
            return Optional.empty();
        }

        String tag;
        if (parts[2].startsWith("tag: ")) {
            tag = parts[2].substring("tag: ".length());
        } else {
            tag = null;
        }

        return Optional.of(new Commit(parts[0], authorDate, tag, parts[3]));
    }
}
