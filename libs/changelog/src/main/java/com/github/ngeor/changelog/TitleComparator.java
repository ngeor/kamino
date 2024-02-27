package com.github.ngeor.changelog;

import com.github.ngeor.versions.SemVer;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TitleComparator implements Comparator<String> {
    // TODO make the pattern configurable, aligning with the formatter,
    // but offering more flexibility, in case past changelog entries were generated in a different format
    private static final Pattern semVerInBrackets =
            Pattern.compile("^\\[(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<patch>\\d+)].*$");
    private static final Pattern semVer = Pattern.compile("^(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<patch>\\d+).*$");
    private final String unreleasedTitle;

    public TitleComparator(String unreleasedTitle) {
        this.unreleasedTitle = unreleasedTitle;
    }

    @Override
    public int compare(String left, String right) {
        if (left == null) {
            return right == null ? 0 : -1;
        } else {
            return right == null ? 1 : compareNonNull(left, right);
        }
    }

    private int compareNonNull(String left, String right) {
        Kind leftKind = kindOf(left);
        Kind rightKind = kindOf(right);
        return leftKind.compareTo(rightKind);
    }

    private Kind kindOf(String title) {
        return Stream.of(semVerInBrackets, semVer)
                .map(pattern -> pattern.matcher(title))
                .filter(Matcher::matches)
                .map(matcher -> (Kind) new Kind.Version(fromMatcher(matcher)))
                .findAny()
                .or(() -> Stream.of(unreleasedTitle, "[" + unreleasedTitle + "]")
                        .filter(t -> title.regionMatches(true, 0, t, 0, t.length()))
                        .map(ignored -> (Kind) new Kind.Unreleased(title))
                        .findAny())
                .orElseGet(() -> new Kind.Unknown(title));
    }

    private SemVer fromMatcher(Matcher matcher) {
        return new SemVer(
                Integer.parseInt(matcher.group("major")),
                Integer.parseInt(matcher.group("minor")),
                Integer.parseInt(matcher.group("patch")));
    }

    private sealed interface Kind extends Comparable<Kind> {
        record Unknown(String value) implements Kind {
            @Override
            public int compareTo(Kind o) {
                if (o instanceof Unknown other) {
                    return value.compareToIgnoreCase(other.value);
                } else {
                    return 1;
                }
            }
        }

        record Unreleased(String value) implements Kind {
            @Override
            public int compareTo(Kind o) {
                if (o instanceof Unknown) {
                    return -1;
                } else if (o instanceof Unreleased) {
                    return 0;
                } else {
                    return 1;
                }
            }
        }

        record Version(SemVer semVer) implements Kind {
            @Override
            public int compareTo(Kind o) {
                if (o instanceof Version other) {
                    return semVer.compareTo(other.semVer);
                } else {
                    return -1;
                }
            }
        }
    }
}
