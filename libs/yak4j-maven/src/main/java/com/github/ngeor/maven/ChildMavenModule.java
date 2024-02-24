package com.github.ngeor.maven;

import java.io.File;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public final class ChildMavenModule extends MavenModuleNg {
    private final MavenModuleNg parent;
    private final String moduleName;

    public ChildMavenModule(File pomFile, MavenModuleNg parent, String moduleName) {
        super(pomFile);
        this.parent = Objects.requireNonNull(parent);
        this.moduleName = Validate.notBlank(moduleName);
    }

    public String getModuleName() {
        return moduleName;
    }
}
