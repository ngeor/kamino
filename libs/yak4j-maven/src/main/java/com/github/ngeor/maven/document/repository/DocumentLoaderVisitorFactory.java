package com.github.ngeor.maven.document.repository;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactoryDecorator;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class DocumentLoaderVisitorFactory extends DocumentLoaderFactoryDecorator<DocumentLoader, DocumentLoader> {
    private final BiConsumer<DocumentLoader, DocumentWrapper> visitor;

    public DocumentLoaderVisitorFactory(
            DocumentLoaderFactory<DocumentLoader> decorated, BiConsumer<DocumentLoader, DocumentWrapper> visitor) {
        super(decorated);
        this.visitor = Objects.requireNonNull(visitor);
    }

    public static DocumentLoaderFactory<DocumentLoader> visitDocumentLoader(
            DocumentLoaderFactory<DocumentLoader> decorated, Consumer<DocumentLoader> visitor) {
        return new DocumentLoaderVisitorFactory(decorated, (documentLoader, ignored) -> visitor.accept(documentLoader));
    }

    public static DocumentLoaderFactory<DocumentLoader> visitDocument(
            DocumentLoaderFactory<DocumentLoader> decorated, Consumer<DocumentWrapper> visitor) {
        return new DocumentLoaderVisitorFactory(decorated, (ignored, document) -> visitor.accept(document));
    }

    @Override
    protected DocumentLoader decorateDocumentLoader(DocumentLoader inner) {
        return new DocumentLoaderVisitor(inner, visitor);
    }
}
