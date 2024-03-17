package com.github.ngeor.maven.ng;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;

public class ResourceDocument extends BaseDocument {
    protected ResourceDocument(PomDocumentFactory owner, String resourceName) {
        super(owner, () -> loadResourceDocument(resourceName));
    }

    private static DocumentWrapper loadResourceDocument(String resourceName) {
        try (InputStream is = Objects.requireNonNull(ResourceDocument.class.getResourceAsStream(resourceName))) {
            return DocumentWrapper.parse(is);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
