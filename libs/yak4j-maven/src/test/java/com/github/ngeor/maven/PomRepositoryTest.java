package com.github.ngeor.maven;

import static com.github.ngeor.maven.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.ElementNames.VERSION;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PomRepositoryTest {
    private final PomRepository pomRepository = new PomRepository();

    @ParameterizedTest
        @NullAndEmptySource
    void loadEmpty(String xmlContents) {
        assertThatThrownBy(() -> pomRepository.load(xmlContents))
            .hasMessage("xmlContents is required");
    }

    @Test
    void loadInvalidXml() {
        assertThatThrownBy(() -> pomRepository.load("oops"))
            .hasMessage("Cannot parse xmlContents");
    }

    @Test
    void loadIncorrectRootElement() {
        assertThatThrownBy(() -> pomRepository.load("<oops />"))
            .hasMessage("Unexpected root element 'oops' (expected 'project')");
    }

    @ParameterizedTest
    @ValueSource(strings = {GROUP_ID, ARTIFACT_ID, VERSION})
    void loadMissingGroupId(String missingElement) {
        String xmlContents = """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
            </project>""".replaceAll(String.format("<%s>.+?</%s>", missingElement, missingElement), "");
        assertThatThrownBy(() -> pomRepository.load(xmlContents)).hasMessage("Missing maven coordinates");
    }

    @Test
    void loadTwiceIsNotAllowed() {
        String xmlContents = """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
            </project>""";
        pomRepository.load(xmlContents);
        assertThatThrownBy(() -> pomRepository.load(xmlContents)).hasMessage("Document com.acme:foo:1.0 is already loaded");
    }
}
