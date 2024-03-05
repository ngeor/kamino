package com.github.ngeor.mr;

import com.github.ngeor.changelog.format.FormatOptions;
import com.github.ngeor.versions.SemVer;
import java.io.File;
import org.immutables.value.Value;

@Value.Immutable
public interface Options {
    File monorepoRoot();

    String path();

    FormatOptions formatOptions();

    SemVer nextVersion();

    boolean push();
}
