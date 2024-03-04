package com.github.ngeor.maven.document.effective;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.document.loader.CachedDocumentDecorator;
import com.github.ngeor.maven.document.loader.CanonicalFile;
import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderDecorator;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.FileDocumentLoader;
import com.github.ngeor.maven.document.parent.CanLoadParentFactory;
import com.github.ngeor.maven.document.parent.DefaultParentPomFinder;
import com.github.ngeor.maven.document.parent.LocalRepositoryLocator;
import com.github.ngeor.maven.document.parent.ParentPomFinder;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@SuppressWarnings("MagicNumber")
class EffectivePomDecoratorTest {
    @TempDir
    private Path localRepository;

    @TempDir
    private Path rootDir;

    private final LocalRepositoryLocator localRepositoryLocator = () -> localRepository;
    private final ParentPomFinder parentPomFinder = new DefaultParentPomFinder(localRepositoryLocator);
    private final DocumentLoaderFactory<EffectivePom> factory = FileDocumentLoader.asFactory()
            .decorate(f -> new CanLoadParentFactory(f, parentPomFinder))
            .decorate(f -> EffectivePomDecorator.decorateFactory(f, new PomMerger()));
    private final Map<CanonicalFile, Integer> loadCount = new HashMap<>();
    private final DocumentLoaderFactory<EffectivePom> cachedFactory = FileDocumentLoader.asFactory()
            .decorate(f -> pomFile -> (DocumentLoader) new DocumentLoaderDecorator(f.createDocumentLoader(pomFile)) {
                @Override
                public DocumentWrapper loadDocument() {
                    int x = loadCount.compute(new CanonicalFile(pomFile), (k, v) -> v == null ? 1 : v + 1);
                    return super.loadDocument();
                }
            })
            .decorate(CachedDocumentDecorator::decorateFactory)
            .decorate(f -> new CanLoadParentFactory(f, parentPomFinder))
            .decorate(f -> EffectivePomDecorator.decorateFactory(f, new CachedMerger(new PomMerger())));

    private Set<Integer> loadCounts() {
        return new HashSet<>(loadCount.values());
    }

    @BeforeEach
    void beforeEach() throws IOException {
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
        Files.writeString(
                rootDir.resolve("second-child.xml"),
                """
<project>
    <artifactId>second-child</artifactId>
    <parent>
        <groupId>com.acme</groupId>
        <artifactId>parent</artifactId>
        <version>1.0</version>
        <relativePath>parent.xml</relativePath>
    </parent>
    <properties>
        <taste>bitter</taste>
    </properties>
</project>""");
    }

    @Test
    void noParentPom() {
        EffectivePom input = factory.createDocumentLoader(rootDir, "grandparent.xml");

        // act
        assertThat(input.effectivePom().writeToString())
                .isEqualTo(input.loadDocument().writeToString());
    }

    @Test
    void noParentPomWithCacheTwice() {
        EffectivePom input = cachedFactory.createDocumentLoader(rootDir, "grandparent.xml");

        // act
        assertThat(input.effectivePom()).isNotNull().isSameAs(input.loadDocument());
        assertThat(input.effectivePom()).isNotNull().isSameAs(input.loadDocument());
        assertThat(loadCounts()).hasSize(1).containsOnly(1);
    }

    @Test
    void withParentPom() {
        EffectivePom input = factory.createDocumentLoader(rootDir, "parent.xml");

        // act
        DocumentWrapper resolvedDocument = input.effectivePom();

        // assert
        assertThat(resolvedDocument).isNotNull().isNotSameAs(input.loadDocument());
        assertThat(DomHelper.getCoordinates(resolvedDocument))
                .isEqualTo(new MavenCoordinates("com.acme", "parent", "1.0"));
        assertThat(DomHelper.getProperty(resolvedDocument, "color")).contains("blue");
        assertThat(DomHelper.getProperty(resolvedDocument, "taste")).contains("sour");
        assertThat(DomHelper.getParentPom(resolvedDocument)).isEmpty();
    }

    @Test
    void withParentPomWithCacheTwice() {
        EffectivePom input = cachedFactory.createDocumentLoader(rootDir, "parent.xml");

        // act
        DocumentWrapper doc1 = input.effectivePom();
        DocumentWrapper doc2 = input.effectivePom();

        // assert
        assertThat(doc1).isNotNull().isSameAs(doc2);
        assertThat(DomHelper.getCoordinates(doc1)).isEqualTo(new MavenCoordinates("com.acme", "parent", "1.0"));
        assertThat(DomHelper.getProperty(doc1, "color")).contains("blue");
        assertThat(DomHelper.getProperty(doc1, "taste")).contains("sour");
        assertThat(DomHelper.getParentPom(doc1)).isEmpty();
        assertThat(loadCounts()).hasSize(1).containsOnly(1);
    }

    @Test
    void withGrandParentPom() {
        EffectivePom input = factory.createDocumentLoader(rootDir, "child.xml");

        // act
        DocumentWrapper resolvedDocument = input.effectivePom();

        // assert
        assertThat(resolvedDocument).isNotNull().isNotSameAs(input.loadDocument());
        assertThat(DomHelper.getCoordinates(resolvedDocument))
                .isEqualTo(new MavenCoordinates("com.acme", "child", "1.0"));
        assertThat(DomHelper.getProperty(resolvedDocument, "color")).contains("blue");
        assertThat(DomHelper.getProperty(resolvedDocument, "taste")).contains("sweet");
        assertThat(DomHelper.getParentPom(resolvedDocument)).isEmpty();
    }
}
