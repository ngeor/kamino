package com.github.ngeor.maven.resolve.cache;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.junit.jupiter.api.Test;

class CanonicalFileTest {
    @Test
    void testSameFile() {
        CanonicalFile a = new CanonicalFile(new File("pom.xml"));
        CanonicalFile b = new CanonicalFile(new File("pom.xml"));
        assertThat(a).isEqualTo(b);
    }

    @Test
    void testDifferentFile() {
        CanonicalFile a = new CanonicalFile(new File("pom.xml"));
        CanonicalFile b = new CanonicalFile(new File("pom.bak"));
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void testSameCanonicalFile() {
        File file1 = new File("pom.xml");
        File file2 = new File("./pom.xml");
        assertThat(file1).isNotEqualTo(file2);
        assertThat(new CanonicalFile(file1)).isEqualTo(new CanonicalFile(file2));
    }
}
