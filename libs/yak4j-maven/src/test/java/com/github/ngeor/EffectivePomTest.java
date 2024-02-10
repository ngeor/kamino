package com.github.ngeor;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class EffectivePomTest {
    @Test
    void test() throws IOException, InterruptedException {
        String pom = """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.acme</groupId>
              <artifactId>dummy</artifactId>
              <version>1.0-SNAPSHOT</version>
          </project>""";
        File file = File.createTempFile("pom", ".xml");
        file.deleteOnExit();
        Files.writeString(file.toPath(), pom);

        Maven maven = new Maven(file);
        File outputFile = File.createTempFile("pom", ".xml");
        outputFile.deleteOnExit();
        maven.effectivePom(outputFile);

        // System.out.println(Files.readString(outputFile.toPath()));

        DocumentWrapper document = DocumentWrapper.parse(outputFile);
        assertThat(document.getDocumentElement().firstElementText("groupId")).isEqualTo("com.acme");
        assertThat(document.getDocumentElement().firstElementText("artifactId")).isEqualTo("dummy");
    }
}
