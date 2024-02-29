package com.github.ngeor.maven.resolve.input;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.ParentPom;
import com.github.ngeor.maven.dom.DomHelper;
import java.io.File;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class DefaultParentLoader implements ParentLoader {
    private final InputFactory factory;
    private final LocalRepositoryLocator localRepositoryLocator;

    public DefaultParentLoader(InputFactory factory, LocalRepositoryLocator localRepositoryLocator) {
        this.factory = Objects.requireNonNull(factory);
        this.localRepositoryLocator = Objects.requireNonNull(localRepositoryLocator);
    }

    @Override
    public Optional<Input> loadParent(Input input) {
        return DomHelper.getParentPom(input.document())
                .map(parentPom -> findParentPomXmlFile(input, parentPom))
                .map(factory::load);
    }

    // NOTE: if this is expensive, make it protected and cache it with a decorator
    private File findParentPomXmlFile(Input input, ParentPom parentPom) {
        return findParentPomXmlFileByRelativePath(input, parentPom)
                .orElseGet(() -> findParentPomXmlFileInLocalRepository(parentPom));
    }

    private Optional<File> findParentPomXmlFileByRelativePath(Input input, ParentPom parentPom) {
        String relativePath = StringUtils.defaultIfBlank(parentPom.relativePath(), "../pom.xml");
        File f = new File(input.pomFile().getParentFile(), relativePath);
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
