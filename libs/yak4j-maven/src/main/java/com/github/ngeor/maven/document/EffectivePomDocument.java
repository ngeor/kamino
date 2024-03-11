package com.github.ngeor.maven.document;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Objects;

public class EffectivePomDocument extends BasePomDocument {
    private final DocumentWrapper document;

    public EffectivePomDocument(DocumentWrapper document) {
        this.document = Objects.requireNonNull(document);
    }

    @Override
    protected DocumentWrapper doLoadDocument() {
        return document;
    }
}
