package com.github.ngeor.maven.resolve;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.ParentPom;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DefaultResolverIT {
    private final DefaultResolver resolver = new DefaultResolver();

    @Nested
    class ResolveRelative {
        @TempDir
        private Path rootDir;

        private FileInput child;
        private MavenCoordinates parentCoordinates;

        @BeforeEach
        void beforeEach() throws IOException {
            Files.writeString(
                    rootDir.resolve("pom.xml"),
                    """
                    <project>
                    </project>
                    """);
            Files.createDirectory(rootDir.resolve("child"));
            Files.writeString(
                    rootDir.resolve("child").resolve("pom.xml"),
                    """
                    <project>
                    </project>
                    """);
            child = new FileInput(rootDir.resolve("child").resolve("pom.xml").toFile());
            parentCoordinates = new MavenCoordinates("com.acme", "foo", "1.0");
        }

        @Test
        void resolveRelativeDirectory() {
            // arrange
            ParentPom parentPom = new ParentPom(parentCoordinates, "..");

            // act
            Input result = resolver.resolve(child, parentPom);

            // assert
            assertHappyFlow(result);
        }

        @Test
        void resolveRelativeFile() {
            // arrange
            ParentPom parentPom = new ParentPom(parentCoordinates, "../pom.xml");

            // act
            Input result = resolver.resolve(child, parentPom);

            // assert
            assertHappyFlow(result);
        }

        private void assertHappyFlow(Input result) {
            try {
                assertThat(result).isNotNull().isInstanceOf(FileInput.class);
                File actual = ((FileInput) result).pomFile().getCanonicalFile();
                File expected = rootDir.resolve("pom.xml").toFile().getCanonicalFile();
                assertThat(actual).isEqualTo(expected);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }
}
