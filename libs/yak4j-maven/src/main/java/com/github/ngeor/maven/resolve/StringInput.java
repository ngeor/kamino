package com.github.ngeor.maven.resolve;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import org.apache.commons.lang3.Validate;

public record StringInput(String contents) implements Input {
    public StringInput {
        Validate.notBlank(contents, "xmlContents is required");
    }

    @Override
    public DocumentWrapper loadDocument() {
        return DocumentWrapper.parseString(contents);
    }
}
