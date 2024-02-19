package com.github.ngeor;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class CommitFilter implements Predicate<String> {
    private static final List<Pattern> needles = List.of(
            Pattern.compile("^\\[maven-release-plugin]"),
            Pattern.compile("\\bfix(ed|ing)? build\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(Updat(e|ed|ign) )?changelog\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^(chore|fix):( apply)? spotless$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^(chore|fix): fix failing tests$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^release\\("),
            Pattern.compile("^chore: sort( )?pom$", Pattern.CASE_INSENSITIVE));

    @Override
    public boolean test(String summary) {
        if (summary == null || summary.isBlank()) {
            return false;
        }

        return needles.stream().map(Pattern::asPredicate).noneMatch(patternPredicate -> patternPredicate.test(summary));
    }
}
