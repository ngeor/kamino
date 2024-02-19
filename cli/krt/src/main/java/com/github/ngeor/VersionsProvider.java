package com.github.ngeor;

import com.github.ngeor.versions.SemVer;
import java.io.IOException;
import java.util.SortedSet;

public interface VersionsProvider {
    SortedSet<SemVer> listVersions() throws IOException, InterruptedException;
}
