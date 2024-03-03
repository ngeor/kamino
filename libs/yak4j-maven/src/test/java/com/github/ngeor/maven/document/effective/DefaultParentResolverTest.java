package com.github.ngeor.maven.document.effective;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.FileDocumentLoader;
import com.github.ngeor.maven.document.parent.DefaultParentLoader;
import com.github.ngeor.maven.document.parent.LocalRepositoryLocator;
import com.github.ngeor.maven.document.parent.ParentLoader;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@SuppressWarnings("MagicNumber")
class DefaultParentResolverTest {
    @TempDir
    private Path localRepository;

    @TempDir
    private Path rootDir;

    private final DocumentLoaderFactory factory = FileDocumentLoader.asFactory();
    private final LocalRepositoryLocator localRepositoryLocator = () -> localRepository;
    private final ParentLoader parentLoader = new DefaultParentLoader(factory, localRepositoryLocator);
    private final ParentResolver parentResolver = new DefaultParentResolver(parentLoader);

    @Test
    void noParentPom() throws IOException {
        Files.writeString(
                rootDir.resolve("pom.xml"),
                """
    <project>
        <groupId>com.acme</groupId>
        <artifactId>foo</artifactId>
        <version>1.0</version>
        <properties>
            <color>red</color>
        </properties>
    </project>""");
        DocumentLoader input =
                factory.createDocumentLoader(rootDir.resolve("pom.xml").toFile());

        // act
        DocumentLoader result = parentResolver.resolveWithParentRecursively(input);

        // assert
        assertThat(result).isNotNull().isSameAs(input);
    }

    @Test
    void withParentPom() throws IOException {
        Files.writeString(
                rootDir.resolve("parent.xml"),
                """
    <project>
        <groupId>com.acme</groupId>
        <artifactId>parent</artifactId>
        <version>1.0</version>
        <properties>
            <color>blue</color>
        </properties>
    </project>""");
        Files.writeString(
                rootDir.resolve("child.xml"),
                """
    <project>
        <artifactId>child</artifactId>
        <parent>
            <groupId>com.acme</groupId>
            <artifactId>parent</artifactId>
            <version>1.0</version>
            <relativePath>parent.xml</relativePath>
        </parent>
        <properties>
            <taste>sweet</taste>
        </properties>
    </project>""");
        DocumentLoader input =
                factory.createDocumentLoader(rootDir.resolve("child.xml").toFile());

        // act
        DocumentLoader result = parentResolver.resolveWithParentRecursively(input);

        // assert
        assertThat(result).isNotNull().isNotSameAs(input);
        DocumentWrapper resolvedDocument = result.loadDocument();
        assertThat(DomHelper.getCoordinates(resolvedDocument))
                .isEqualTo(new MavenCoordinates("com.acme", "child", "1.0"));
        assertThat(DomHelper.getProperty(resolvedDocument, "color")).contains("blue");
        assertThat(DomHelper.getProperty(resolvedDocument, "taste")).contains("sweet");
        assertThat(DomHelper.getParentPom(resolvedDocument)).isEmpty();
    }

    @Test
    void withGrandParentPom() throws IOException {
        Files.writeString(
                rootDir.resolve("grandparent.xml"),
                """
    <project>
        <groupId>com.acme</groupId>
        <artifactId>grandparent</artifactId>
        <version>1.0</version>
        <properties>
            <color>blue</color>
        </properties>
    </project>""");
        Files.writeString(
                rootDir.resolve("parent.xml"),
                """
    <project>
        <artifactId>parent</artifactId>
        <parent>
            <groupId>com.acme</groupId>
            <artifactId>grandparent</artifactId>
            <version>1.0</version>
            <relativePath>grandparent.xml</relativePath>
        </parent>
        <properties>
            <taste>sour</taste>
        </properties>
    </project>""");
        Files.writeString(
                rootDir.resolve("child.xml"),
                """
    <project>
        <artifactId>child</artifactId>
        <parent>
            <groupId>com.acme</groupId>
            <artifactId>parent</artifactId>
            <version>1.0</version>
            <relativePath>parent.xml</relativePath>
        </parent>
        <properties>
            <taste>sweet</taste>
        </properties>
    </project>""");
        DocumentLoader input =
                factory.createDocumentLoader(rootDir.resolve("child.xml").toFile());

        // act
        DocumentLoader result = parentResolver.resolveWithParentRecursively(input);

        // assert
        assertThat(result).isNotNull().isNotSameAs(input);
        DocumentWrapper resolvedDocument = result.loadDocument();
        assertThat(DomHelper.getCoordinates(resolvedDocument))
                .isEqualTo(new MavenCoordinates("com.acme", "child", "1.0"));
        assertThat(DomHelper.getProperty(resolvedDocument, "color")).contains("blue");
        assertThat(DomHelper.getProperty(resolvedDocument, "taste")).contains("sweet");
        assertThat(DomHelper.getParentPom(resolvedDocument)).isEmpty();
    }
}
