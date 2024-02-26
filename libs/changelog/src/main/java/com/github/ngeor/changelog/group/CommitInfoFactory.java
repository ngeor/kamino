package com.github.ngeor.changelog.group;

import com.github.ngeor.git.Commit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CommitInfoFactory {
    private static final Pattern conventionalCommitPattern = Pattern.compile(
            "^(?<type>[a-z]+)(\\((?<scope>[a-z]+)\\))?(?<breaking>!)?:\\s*(?<description>.+)$",
            Pattern.CASE_INSENSITIVE);

    private CommitInfoFactory() {}

    public static CommitInfo toCommitInfo(Commit commit) {
        Matcher matcher = conventionalCommitPattern.matcher(commit.summary());
        if (matcher.matches()) {
            String type = matcher.group("type");
            String description = matcher.group("description");
            String scope = matcher.group("scope");
            boolean isBreaking = matcher.group("breaking") != null;
            return new ConventionalCommit(type, scope, description, isBreaking);
        }

        return new UntypedCommit(commit);
    }
}
