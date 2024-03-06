package com.github.ngeor.mr;

import static com.github.ngeor.mr.Util.VALID_CHILD_POM_CONTENTS;

import org.junit.jupiter.api.Test;

class PomBasicValidatorTest {
    private final PomBasicValidator validator = new PomBasicValidator();

    @Test
    void testModelVersionIsRequired() {
        Util.assertRemovingElementThrows("modelVersion", validator)
                .hasMessage("Element 'modelVersion' not found under 'project'");
    }

    @Test
    void testNameIsRequired() {
        Util.assertInvalidPom(VALID_CHILD_POM_CONTENTS.replaceAll("<name>foo</name>", ""), validator)
                .hasMessageContaining("name");
    }

    @Test
    void testDescriptionIsRequired() {
        String childElementName = "description";
        // test missing element
        Util.assertRemovingElementThrows(childElementName, validator)
                .hasMessage(String.format("Element '%s' not found under 'project'", childElementName));
        // test empty element
        Util.assertInvalidPom(
                        VALID_CHILD_POM_CONTENTS.replaceAll(
                                String.format("<%s>.+?</%s>", childElementName, childElementName),
                                String.format("<%s />", childElementName)),
                        validator)
                .hasMessage(String.format("Element 'project/%s' must have text content", childElementName));
    }

    @Test
    void testLicensesIsRequired() {
        Util.assertInvalidPom(
                        """
<project>
<modelVersion>4.0.0</modelVersion>
<groupId>com.acme</groupId>
<artifactId>foo</artifactId>
<version>1.0-SNAPSHOT</version>
<name>foo</name>
<description>Some library</description>
</project>
""",
                        validator)
                .hasMessageContaining("licenses");
    }

    @Test
    void testScmConnectionIsRequired() {
        Util.assertRemovingElementThrows("connection", validator)
                .hasMessage("Element 'connection' not found under 'project/scm'");
    }
}
