package com.github.ngeor.maven.document;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class ResourceDocument extends BaseDocument {
    public ResourceDocument(
            PomDocumentFactory owner, String resourceName, UnaryOperator<DocumentWrapper> documentDecorator) {
        super(owner, () -> documentDecorator.apply(loadResourceDocument(resourceName)));
    }

    public ResourceDocument(PomDocumentFactory owner, String resourceName) {
        this(owner, resourceName, UnaryOperator.identity());
    }

    private static DocumentWrapper loadResourceDocument(String resourceName) {
        try (InputStream is = Objects.requireNonNull(ResourceDocument.class.getResourceAsStream(resourceName))) {
            return DocumentWrapper.parse(is);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
