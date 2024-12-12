package com.github.ngeor.maven.document;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResolvedPropertiesDocumentTest {
    private final PomDocumentFactory factory = new PomDocumentFactory();

    @Test
    void unresolvedProperties() {
        ResourceDocument doc = new ResourceDocument(factory, "/2level/grandparent.xml");
        assertThat(doc.getProperty("color")).contains("blue");
        assertThat(doc.getProperty("phrase")).contains("Always ${color}");
    }

    @Test
    void resolvedProperties() {
        BaseDocument doc = new ResourceDocument(factory, "/2level/grandparent.xml").toResolvedProperties();
        assertThat(doc.getProperty("color")).contains("blue");
        assertThat(doc.getProperty("phrase")).contains("Always blue");
    }
}
