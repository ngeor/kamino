package com.github.ngeor.maven.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import org.junit.jupiter.api.Test;

class BasePomDocumentTest {
    private static class TestPomDocument extends ResourcePomDocument {
        private int count = 0;

        public TestPomDocument(String resourceName) {
            super(resourceName);
        }

        @Override
        protected DocumentWrapper doLoadDocument() {
            count++;
            return super.doLoadDocument();
        }
    }

    @Test
    void coordinatesTwiceOnlyLoadsDocumentOnce() {
        TestPomDocument pom = new TestPomDocument("/pom1.xml");
        assertThat(pom.count).isZero();
        MavenCoordinates coordinates = pom.coordinates();
        assertThat(coordinates).isNotNull();
        assertThat(pom.count).isEqualTo(1);
        assertThat(pom.coordinates()).isEqualTo(coordinates);
        assertThat(pom.count).isEqualTo(1);
    }
}
