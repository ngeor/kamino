package com.github.ngeor.maven.document.repository;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderDecorator;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Objects;
import java.util.function.BiConsumer;

public class DocumentLoaderVisitor extends DocumentLoaderDecorator<DocumentLoader> {
    private final BiConsumer<DocumentLoader, DocumentWrapper> visitor;

    public DocumentLoaderVisitor(DocumentLoader decorated, BiConsumer<DocumentLoader, DocumentWrapper> visitor) {
        super(decorated);
        this.visitor = Objects.requireNonNull(visitor);
    }

    @Override
    public DocumentWrapper loadDocument() {
        DocumentWrapper result = super.loadDocument();
        visitor.accept(this, result);
        return result;
    }
}
