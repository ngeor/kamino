package com.github.ngeor.maven;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.document.repository.PomRepository;
import com.github.ngeor.maven.process.Maven;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class EffectivePomIT {
    private Maven maven;
    private File file;

    @Test
    void test() throws IOException, ProcessFailedException {
        String pom =
                """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.acme</groupId>
              <artifactId>dummy</artifactId>
              <version>1.0-SNAPSHOT</version>
              <properties>
                <foo>123</foo>
                <bar>test-${foo}</bar>
              </properties>
          </project>""";
        file = File.createTempFile("pom", ".xml");
        file.deleteOnExit();
        Files.writeString(file.toPath(), pom);

        maven = new Maven(file);

        verifyEffectivePom(project -> {
            assertThat(project.firstElementText("groupId")).isEqualTo("com.acme");
            assertThat(project.firstElementText("artifactId")).isEqualTo("dummy");
            assertThat(project.firstElementText("properties", "foo")).isEqualTo("123");
            assertThat(project.firstElementText("properties", "bar")).isEqualTo("test-123");
        });
    }

    @Test
    void testParentPom() throws IOException, ProcessFailedException {
        String pom1 =
                """
        <project>
          <modelVersion>4.0.0</modelVersion>
          <groupId>com.acme</groupId>
          <artifactId>parent</artifactId>
          <version>1.0-SNAPSHOT</version>
          <packaging>pom</packaging>
          <properties>
            <foo>123</foo>
            <bar>test-${foo}</bar>
          </properties>
      </project>""";
        File file1 = File.createTempFile("pom", ".xml");
        file1.deleteOnExit();
        Files.writeString(file1.toPath(), pom1);

        new Maven(file1).install();

        String pom =
                """
        <project>
          <modelVersion>4.0.0</modelVersion>
          <artifactId>dummy</artifactId>
          <version>1.0-SNAPSHOT</version>
          <parent>
              <groupId>com.acme</groupId>
              <artifactId>parent</artifactId>
              <version>1.0-SNAPSHOT</version>
          </parent>
          <properties>
            <foo>456</foo>
          </properties>
      </project>""";
        file = File.createTempFile("pom", ".xml");
        file.deleteOnExit();
        Files.writeString(file.toPath(), pom);

        maven = new Maven(file);

        verifyEffectivePom(project -> {
            assertThat(project.firstElementText("groupId")).isEqualTo("com.acme");
            assertThat(project.firstElementText("artifactId")).isEqualTo("dummy");
            assertThat(project.firstElement("properties").orElseThrow().firstElementText("foo"))
                    .isEqualTo("456");
            assertThat(project.firstElement("properties").orElseThrow().firstElementText("bar"))
                    .isEqualTo("test-456");
        });
    }

    private void verifyEffectivePom(Consumer<ElementWrapper> assertions) throws IOException, ProcessFailedException {
        // assert by invoking Maven process
        assertions.accept(maven.effectivePomViaMaven().getDocumentElement());
        // assert by not invoking Maven process
        PomRepository pomRepository = new PomRepository();
        DocumentWrapper document = pomRepository.loadAndResolveProperties(file).resolveProperties();
        assertions.accept(document.getDocumentElement());
    }
}
