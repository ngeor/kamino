package com.github.ngeor.mr;

import static com.github.ngeor.mr.MavenReleaserIT.VALID_CHILD_POM_CONTENTS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("MagicNumber")
class MavenReleaserTest {
    @Nested
    class PomValidationTest {

        @Test
        void testModelVersionIsRequired() {
            testValidation(removeElement("modelVersion"))
                    .hasMessage("Element 'modelVersion' not found under 'project'");
        }

        @Test
        void testGroupIdIsRequired() {
            testValidation(removeElement("groupId"))
                    .hasMessage("Cannot resolve coordinates, parent element is missing");
        }

        @Test
        void testArtifactIdIsRequired() {
            testValidation(removeElement("artifactId")).hasMessageContaining("artifactId");
        }

        @Test
        void testVersionIsRequired() {
            testValidation(removeElement("version"))
                    .hasMessage("Cannot resolve coordinates, parent element is missing");
        }

        @Test
        void testNameIsRequired() {
            testValidation(VALID_CHILD_POM_CONTENTS.replaceAll("<name>foo</name>", ""))
                    .hasMessageContaining("name");
        }

        @Test
        void testDescriptionIsRequired() {
            testMissingDescription();
        }

        @Test
        void testLicensesIsRequired() {
            testValidation(
                            """
        <project>
            <modelVersion>4.0.0</modelVersion>
            <groupId>com.acme</groupId>
            <artifactId>foo</artifactId>
            <version>1.0-SNAPSHOT</version>
            <name>foo</name>
            <description>Some library</description>
        </project>
        """)
                    .hasMessageContaining("licenses");
        }

        @Test
        void testScmConnectionIsRequired() {
            testValidation(removeElement("connection"))
                    .hasMessage("Element 'connection' not found under 'project/scm'");
        }

        private AbstractThrowableAssert<?, ? extends Throwable> testValidation(String invalidChildPomContents) {
            return assertThatThrownBy(() -> MavenReleaser.calcModuleCoordinatesAndDoSanityChecks(
                    DocumentWrapper.parseString(invalidChildPomContents)));
        }

        private void testMissingDescription() {
            String childElementName = "description";
            // test missing element
            testValidation(removeElement(childElementName))
                    .hasMessage(String.format("Element '%s' not found under 'project'", childElementName));
            // test empty element
            testValidation(VALID_CHILD_POM_CONTENTS.replaceAll(
                            String.format("<%s>.+?</%s>", childElementName, childElementName),
                            String.format("<%s />", childElementName)))
                    .hasMessage(String.format("Element 'project/%s' must have text content", childElementName));
        }

        private static String removeElement(String elementName) {
            return MavenReleaserIT.VALID_CHILD_POM_CONTENTS.replaceAll(
                    String.format("<%s>.+?</%s>", elementName, elementName), "");
        }
    }
}
