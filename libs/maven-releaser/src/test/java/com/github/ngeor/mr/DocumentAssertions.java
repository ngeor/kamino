package com.github.ngeor.mr;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.nio.file.Path;

@SuppressWarnings("UnusedReturnValue")
record DocumentAssertions(DocumentWrapper document) {
    DocumentAssertions(Path path) {
        this(DocumentWrapper.parse(path.toFile()));
    }

    public DocumentAssertions hasCoordinates(MavenCoordinates expected) {
        assertThat(DomHelper.coordinates(document)).isEqualTo(expected);
        return this;
    }

    public DocumentAssertions hasParentPom() {
        assertThat(document.getDocumentElement().firstElement(ElementNames.PARENT))
                .isPresent();
        return this;
    }

    public DocumentAssertions doesNotHaveParentPom() {
        assertThat(document.getDocumentElement().firstElement(ElementNames.PARENT))
                .isEmpty();
        return this;
    }

    public DocumentAssertions doesNotHaveModules() {
        assertThat(document.getDocumentElement().firstElement(ElementNames.MODULES))
                .isEmpty();
        return this;
    }

    public DocumentAssertions hasScmTag(String expected) {
        assertThat(document.getDocumentElement()
                        .findChildElements("scm")
                        .flatMap(e -> e.findChildElements("tag"))
                        .flatMap(ElementWrapper::getTextContentTrimmedAsStream))
                .containsExactly(expected);
        return this;
    }
}
