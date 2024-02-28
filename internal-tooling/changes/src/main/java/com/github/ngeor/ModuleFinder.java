package com.github.ngeor;

import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.mr.Defaults;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.stream.Stream;

public class ModuleFinder {
    public Stream<String> eligibleModules(File rootDirectory) {
        return DomHelper.getModules(DocumentWrapper.parse(
                        rootDirectory.toPath().resolve("pom.xml").toFile()))
                .filter(Defaults::isEligibleForRelease);
    }
}
