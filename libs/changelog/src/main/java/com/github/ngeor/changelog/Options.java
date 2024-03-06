package com.github.ngeor.changelog;

import com.github.ngeor.changelog.format.FormatOptions;
import com.github.ngeor.versions.SemVer;
import java.io.File;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface Options {
    File rootDirectory();

    FormatOptions formatOptions();

    Optional<String> modulePath();

    Optional<SemVer> futureVersion();

    @Value.Default
    default boolean overwrite() {
        return false;
    }
}
