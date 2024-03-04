package com.github.ngeor.maven.document.property;

import com.github.ngeor.maven.document.effective.EffectivePom;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import java.io.File;
import java.util.Objects;

public final class CanResolvePropertiesFactory implements DocumentLoaderFactory<CanResolveProperties> {
    private final DocumentLoaderFactory<EffectivePom> decorated;
    private final PropertyResolver propertyResolver;

    public CanResolvePropertiesFactory(
            DocumentLoaderFactory<EffectivePom> decorated, PropertyResolver propertyResolver) {
        this.decorated = Objects.requireNonNull(decorated);
        this.propertyResolver = Objects.requireNonNull(propertyResolver);
    }

    @Override
    public CanResolveProperties createDocumentLoader(File pomFile) {
        return new CanResolvePropertiesDecorator(decorated.createDocumentLoader(pomFile), propertyResolver);
    }
}
