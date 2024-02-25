package com.github.ngeor.maven.resolve;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.ParentPom;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public final class DefaultResolver implements Resolver {
    @Override
    public Input resolve(Input child, ParentPom parentPom) throws IOException {
        Objects.requireNonNull(child);
        Objects.requireNonNull(parentPom);
        MavenCoordinates coordinates = parentPom.coordinates();
        Objects.requireNonNull(coordinates);
        Validate.validState(coordinates.isValid(), "Missing maven coordinates");
        File parentPomFile;
        if (parentPom.relativePath() == null) {
            parentPomFile = parentPomFileFromLocalRepository(parentPom);
        } else if (child instanceof FileInput fileChild) {
            parentPomFile = parentPomFileFromRelativePath(fileChild.getPomFile(), parentPom);
        } else {
            throw new UnsupportedOperationException();
        }

        return new FileInput(parentPomFile);
    }

    private File parentPomFileFromLocalRepository(ParentPom parentPom) {
        MavenCoordinates coordinates = parentPom.coordinates();
        File parentPomFile = new File(System.getProperty("user.home"))
                .toPath()
                .resolve(".m2")
                .resolve("repository")
                .resolve(coordinates.groupId().replace('.', '/'))
                .resolve(coordinates.artifactId())
                .resolve(coordinates.version())
                .resolve(coordinates.artifactId() + "-" + coordinates.version() + ".pom")
                .toFile();

        if (!parentPomFile.isFile()) {
            throw new UnsupportedOperationException("Installing missing Maven pom not supported: " + parentPomFile);
        }
        return parentPomFile;
    }

    private File parentPomFileFromRelativePath(File pomFile, ParentPom parentPom) throws FileNotFoundException {
        File parentPomFile = pomFile.toPath()
                .getParent()
                .resolve(parentPom.relativePath())
                .resolve("pom.xml")
                .toFile();
        if (!parentPomFile.isFile()) {
            throw new FileNotFoundException("Parent pom not found at " + parentPom.relativePath());
        }
        return parentPomFile;
    }
}
