package com.github.ngeor.mr;

import com.github.ngeor.git.FetchOption;
import com.github.ngeor.git.Git;
import com.github.ngeor.git.LsFilesOption;
import com.github.ngeor.process.ProcessFailedException;
import java.io.File;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.function.FailableFunction;

public class GitInitializer implements FailableFunction<File, Git, ProcessFailedException> {
    @Override
    public Git apply(File monorepoRoot) throws ProcessFailedException {
        Git git = new Git(monorepoRoot);
        git.ensureOnDefaultBranch();
        Validate.isTrue(!git.hasStagedChanges(), "repo has staged files");
        Validate.isTrue(!git.hasNonStagedChanges(), "repo has modified files");
        Validate.isTrue(
                git.lsFiles(LsFilesOption.OTHER, LsFilesOption.EXCLUDE_STANDARD)
                        .findFirst()
                        .isEmpty(),
                "repo has untracked files");
        git.fetch(FetchOption.PRUNE, FetchOption.PRUNE_TAGS, FetchOption.TAGS);
        git.pull();
        return git;
    }
}
