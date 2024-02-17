package com.github.ngeor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class PipVersionSetter implements VersionSetter {
    private final Path projectRoot;

    public PipVersionSetter(Path projectRoot) {
        this.projectRoot = projectRoot;
    }

    @Override
    public void bumpVersion(String version) throws IOException {
        List<String> setupCfgContents = Files.readAllLines(projectRoot.resolve("setup.cfg"));
        String moduleName = getModuleNameFromSetupCfg(setupCfgContents);
        Path moduleFile = projectRoot.resolve(moduleName).resolve("__init__.py");
        List<String> moduleContents = Files.readAllLines(moduleFile);
        List<String> newContents = updateVersion(moduleContents, version);
        Files.write(moduleFile, newContents);
    }

    private String getModuleNameFromSetupCfg(List<String> setupCfgContents) {
        String prefix = "version = attr: ";
        String versionLine = setupCfgContents.stream()
                .filter(line -> line.startsWith(prefix))
                .findFirst()
                .orElseThrow();
        String versionSource = versionLine.substring(prefix.length());
        String postfix = ".__version__";
        if (versionSource.endsWith(postfix)) {
            return versionSource.substring(0, versionSource.length() - postfix.length());
        }
        throw new IllegalStateException("Unsupported version line");
    }

    private List<String> updateVersion(List<String> moduleContents, String version) {
        final String newVersionLine = String.format("__version__ = \"%s\"", version);
        return moduleContents.stream()
                .map(line -> line.startsWith("__version__") ? newVersionLine : line)
                .collect(Collectors.toList());
    }
}
