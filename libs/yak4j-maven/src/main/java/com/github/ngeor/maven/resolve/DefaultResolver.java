package com.github.ngeor.maven.resolve;

import com.github.ngeor.maven.ParentPom;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public final class DefaultResolver implements PomRepository.Resolver {
    @Override
    public PomRepository.Input resolve(PomRepository.Input child, ParentPom parentPom) {
        Objects.requireNonNull(child);
        Objects.requireNonNull(parentPom);
        Objects.requireNonNull(parentPom.coordinates());
        Validate.validState(parentPom.coordinates().isValid(), "Missing maven coordinates");
        File parentPomFile;
        if (parentPom.relativePath() == null) {
            parentPomFile = parentPomFileFromLocalRepository(parentPom);
        } else if (child instanceof PomRepository.Input.FileInput fileChild) {
            parentPomFile = parentPomFileFromRelativePath(fileChild.pomFile(), parentPom);
        } else {
            throw new UnsupportedOperationException();
        }

        return new PomRepository.Input.FileInput(parentPomFile);
    }

    private File parentPomFileFromLocalRepository(ParentPom parentPom) {
        File parentPomFile = new File(System.getProperty("user.home"))
                .toPath()
                .resolve(".m2")
                .resolve("repository")
                .resolve(parentPom.coordinates().groupId().replace('.', '/'))
                .resolve(parentPom.coordinates().artifactId())
                .resolve(parentPom.coordinates().version())
                .resolve(parentPom.coordinates().artifactId() + "-"
                        + parentPom.coordinates().version() + ".pom")
                .toFile();

        if (!parentPomFile.isFile()) {
            throw new UnsupportedOperationException("Installing missing Maven pom not supported: " + parentPomFile);
        }
        return parentPomFile;
    }

    private File parentPomFileFromRelativePath(File pomFile, ParentPom parentPom) {
        File parentPomFile = pomFile.toPath()
                .getParent()
                .resolve(parentPom.relativePath())
                .resolve("pom.xml")
                .toFile();
        if (!parentPomFile.isFile()) {
            throw new UncheckedIOException(
                    new FileNotFoundException("Parent pom not found at " + parentPom.relativePath()));
        }
        return parentPomFile;
    }
}
