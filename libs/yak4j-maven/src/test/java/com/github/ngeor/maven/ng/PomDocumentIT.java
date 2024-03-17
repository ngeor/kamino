package com.github.ngeor.maven.ng;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PomDocumentIT {
    @TempDir
    private Path tempDir;

    private final PomDocumentFactory factory = new PomDocumentFactory();

    @Test
    void test2Level() throws IOException {
        // copy resources to temp dir
        copyResource("/2level/grandparent.xml", "pom.xml");
        copyResource("/2level/parent.xml", "parent/");
        copyResource("/2level/child1.xml", "parent/child1/");
        copyResource("/2level/child2.xml", "parent/child2/");

        // load child1
        PomDocument child1 = factory.create(tempDir, "parent/child1/");
        assertThat(child1).isNotNull();
        // load again
        assertThat(factory.create(tempDir, "parent/child1/"))
                .as("Should cache instances of PomDocument")
                .isSameAs(child1);
        // load child2
        PomDocument child2 = factory.create(tempDir, "parent/child2/");
        assertThat(child2).isNotNull().isNotSameAs(child1);
        // load document of child1 twice
        DocumentWrapper child1Doc = child1.loadDocument();
        assertThat(child1Doc).isNotNull();
        assertThat(child1.loadDocument())
                .as("Should cache instances of DocumentWrapper")
                .isSameAs(child1Doc);
        assertThat(child2.loadDocument()).isNotNull().isNotSameAs(child1Doc);
        // parent of child1 should be same instance as parent of child2
        assertThat(child1.parent()).isNotEmpty();
        PomDocument parent = child1.parent().orElseThrow();
        assertThat(child2.parent()).isNotEmpty().containsSame(parent);
        assertThat(child1.parent().orElseThrow().loadDocument())
                .isNotNull()
                .isSameAs(child2.parent().orElseThrow().loadDocument());
        assertThat(parent.parent()).isNotEmpty();
        PomDocument grandparent = parent.parent().orElseThrow();
        assertThat(grandparent.parent()).isEmpty();

        // effective pom
        List<String> merges = new ArrayList<>();
        factory.addMergeListener(
                (left, right) -> merges.add(String.format("%s <- %s", left.artifactId(), right.artifactId())));
        EffectiveDocument child1Effective = child1.toEffective();
        assertThat(child1Effective).isNotNull();
        assertThat(merges).isEmpty();
        MavenCoordinates child1EffectiveCoordinates = child1Effective.coordinates();
        assertThat(child1EffectiveCoordinates).isEqualTo(child1.coordinates());
        assertThat(merges).isEmpty();
        child1Effective.loadDocument();
        assertThat(merges).hasSize(2).containsExactly("grandparent <- parent", "parent <- child1");

        merges.clear();
        EffectiveDocument child2Effective = child2.toEffective();
        assertThat(child2Effective).isNotNull();
        child2Effective.loadDocument();
        assertThat(merges).hasSize(1).containsExactly("parent <- child2");

        merges.clear();
        EffectiveDocument child2Effective2 = child2.toEffective();
        assertThat(child2Effective2).isNotNull().isSameAs(child2Effective);
        child2Effective2.loadDocument();
        assertThat(merges).isEmpty();

        // TODO more assertions
    }

    @Test
    void testAggregator1() throws IOException {
        // copy resources to temp dir
        copyResource("/aggregator1/pom1.xml", "pom.xml");
        copyResource("/aggregator1/child1.xml", "child1/");
        copyResource("/aggregator1/child2.xml", "child2/");
        copyResource("/aggregator1/child3.xml", "child3/");

        // load aggregator
        PomDocument aggregator = factory.create(tempDir, "./");
        assertThat(aggregator.coordinates()).isEqualTo(new MavenCoordinates("com.acme", "aggregator", "1.1"));
        assertThat(aggregator.modules()).containsExactly("child1", "child2", "child3");
        assertThat(factory.moduleByCoordinates(new MavenCoordinates("com.acme", "aggregator", "1.1")))
                .isSameAs(aggregator);
        assertThat(aggregator.internalDependenciesOfModule("child1")).containsExactlyInAnyOrder("child2", "child3");
        assertThat(aggregator.internalDependenciesOfModule("child2")).containsExactlyInAnyOrder("child3");
        assertThat(aggregator.internalDependenciesOfModule("child3")).isEmpty();
    }

    @Test
    void testAggregator2() throws IOException {
        // copy resources to temp dir
        copyResource("/aggregator2/pom1.xml", "pom.xml");
        for (int i = 1; i <= 6; i++) {
            copyResource("/aggregator2/child" + i + ".xml", "child" + i + "/");
        }

        // load aggregator
        PomDocument aggregator = factory.create(tempDir, "./");
        assertThat(aggregator.modules()).containsExactly("child1", "child2", "child3", "child4", "child5", "child6");

        // test internalDependenciesOfModule
        assertThat(aggregator.internalDependenciesOfModule("child1")).containsExactlyInAnyOrder("child4");
        assertThat(aggregator.internalDependenciesOfModule("child2")).containsExactlyInAnyOrder("child4", "child5");
        assertThat(aggregator.internalDependenciesOfModule("child3"))
                .containsExactlyInAnyOrder("child4", "child5", "child6");
        assertThat(aggregator.internalDependenciesOfModule("child4")).isEmpty();
        assertThat(aggregator.internalDependenciesOfModule("child5")).isEmpty();
        assertThat(aggregator.internalDependenciesOfModule("child6")).containsExactlyInAnyOrder("child5");

        // test ancestorsOfModule
        assertThat(aggregator.ancestorsOfModule("child1")).isEmpty();
        assertThat(aggregator.ancestorsOfModule("child2")).containsExactlyInAnyOrder("child1");
        assertThat(aggregator.ancestorsOfModule("child3")).containsExactlyInAnyOrder("child2", "child1");
        assertThat(aggregator.ancestorsOfModule("child4")).isEmpty();
        assertThat(aggregator.ancestorsOfModule("child5")).isEmpty();
        assertThat(aggregator.ancestorsOfModule("child6")).isEmpty();
    }

    private void copyResource(String resourceName, String destination) throws IOException {
        Path parentPath;
        Path filePath;
        if (destination.endsWith("/") || destination.endsWith(File.separator)) {
            parentPath = tempDir.resolve(destination);
            filePath = parentPath.resolve("pom.xml");
        } else {
            filePath = tempDir.resolve(destination);
            parentPath = filePath.getParent();
        }
        if (!parentPath.toFile().exists()) {
            Files.createDirectory(parentPath);
        }
        copyResource(resourceName, filePath);
    }

    private void copyResource(String resourceName, Path file) {
        try (InputStream is = Objects.requireNonNull(getClass().getResourceAsStream(resourceName));
                FileOutputStream fos = new FileOutputStream(file.toFile())) {
            is.transferTo(fos);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
