package com.github.ngeor;

import java.io.IOException;

public interface VersionSetter {
    void bumpVersion(String version) throws IOException, InterruptedException;
}
