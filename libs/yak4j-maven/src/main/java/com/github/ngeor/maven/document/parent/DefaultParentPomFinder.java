package com.github.ngeor.maven.document.parent;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.dom.ParentPom;
import java.io.File;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public final class DefaultParentPomFinder implements ParentPomFinder {
    private final LocalRepositoryLocator localRepositoryLocator;

    public DefaultParentPomFinder(LocalRepositoryLocator localRepositoryLocator) {
        this.localRepositoryLocator = Objects.requireNonNull(localRepositoryLocator);
    }

    @Override
    public Optional<File> findParentPom(DocumentLoader input) {
        return DomHelper.getParentPom(input.loadDocument()).map(parentPom -> findParentPomXmlFile(input, parentPom));
    }

    // NOTE: if this is expensive, make it protected and cache it with a decorator
    private File findParentPomXmlFile(DocumentLoader input, ParentPom parentPom) {
        return findParentPomXmlFileByRelativePath(input, parentPom)
                .orElseGet(() -> findParentPomXmlFileInLocalRepository(parentPom));
    }

    private Optional<File> findParentPomXmlFileByRelativePath(DocumentLoader input, ParentPom parentPom) {
        String relativePath = StringUtils.defaultIfBlank(parentPom.relativePath(), "../pom.xml");
        File f = new File(input.getPomFile().getParentFile(), relativePath);
        if (f.isDirectory()) {
            f = new File(f, "pom.xml");
        }
        return f.isFile() ? Optional.of(f) : Optional.empty();
    }

    private File findParentPomXmlFileInLocalRepository(ParentPom parentPom) {
        MavenCoordinates coordinates = parentPom.validateCoordinates();
        return localRepositoryLocator
                .localRepository()
                .resolve(coordinates.groupId().replace('.', '/'))
                .resolve(coordinates.artifactId())
                .resolve(coordinates.version())
                .resolve(coordinates.artifactId() + "-" + coordinates.version() + ".pom")
                .toFile();
    }
}
