package com.github.ngeor.mr;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.Preconditions;
import java.util.function.Consumer;

public class PomBasicValidator implements Consumer<DocumentWrapper> {
    @Override
    public void accept(DocumentWrapper effectivePom) {
        // ensure modelVersion, name, description exist
        // ensure licenses/license/name and url
        // ensure developers/developer/name and email
        // ensure scm and related properties
        Preconditions.check(effectivePom)
                .hasChildWithTextContent("modelVersion")
                .hasChildWithTextContent("name")
                .hasChildWithTextContent("description")
                .hasChild("licenses")
                .forEachChild("licenses", licenses -> licenses.hasChild("license")
                        .forEachChild("license", license -> license.hasChildWithTextContent("name")
                                .hasChildWithTextContent("url")))
                .hasChild("developers")
                .forEachChild(
                        "developers",
                        developers -> developers.hasChild("developer").forEachChild("developer", developer -> developer
                                .hasChildWithTextContent("name")
                                .hasChildWithTextContent("email")))
                .hasChildThat("scm", scm -> scm.hasChildWithTextContent("connection")
                        .hasChildWithTextContent("developerConnection")
                        .hasChildWithTextContent("tag")
                        .hasChildWithTextContent("url"));
    }
}
