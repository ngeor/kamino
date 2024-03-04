package com.github.ngeor.maven.document.repository;

import com.github.ngeor.maven.document.effective.EffectivePomFactory;
import com.github.ngeor.maven.document.loader.CachedDocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.FileDocumentLoader;
import com.github.ngeor.maven.document.parent.CanLoadParentFactory;
import com.github.ngeor.maven.document.property.CachedPropertyResolver;
import com.github.ngeor.maven.document.property.CanResolveProperties;
import com.github.ngeor.maven.document.property.CanResolvePropertiesFactory;
import com.github.ngeor.maven.document.property.DefaultPropertyResolver;
import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public final class PomRepository implements DocumentLoaderFactory<CanResolveProperties> {
    private final DocumentLoaderFactory<CanResolveProperties> factory = FileDocumentLoader.asFactory()
            .decorate(f -> DocumentLoaderVisitorFactory.visitDocument(f, this::sanityCheckDocument))
            .decorate(CachedDocumentLoaderFactory::new)
            .decorate(CanLoadParentFactory::new)
            .decorate(EffectivePomFactory::new)
            .decorate(
                    f -> new CanResolvePropertiesFactory(f, new CachedPropertyResolver(new DefaultPropertyResolver())));

    @Override
    public CanResolveProperties createDocumentLoader(File file) {
        return factory.createDocumentLoader(file);
    }

    private void sanityCheckDocument(DocumentWrapper document) {
        Objects.requireNonNull(document);
        ElementWrapper element = Objects.requireNonNull(document.getDocumentElement());
        Validate.isTrue(
                ElementNames.PROJECT.equals(element.getNodeName()),
                "Unexpected root element '%s' (expected '%s')",
                element.getNodeName(),
                ElementNames.PROJECT);
    }
}
