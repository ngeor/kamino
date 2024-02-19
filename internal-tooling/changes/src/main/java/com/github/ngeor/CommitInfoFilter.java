package com.github.ngeor;

import java.util.function.Predicate;

public class CommitInfoFilter implements Predicate<Release.CommitInfo> {
    @Override
    public boolean test(Release.CommitInfo commitInfo) {
        return commitInfo != null && new CommitFilter().test(commitInfo.commit());
    }
}
