package com.github.ngeor.maven.document.property;

import com.github.ngeor.maven.document.effective.EffectivePom;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactoryDecorator;
import java.util.Objects;

public final class CanResolvePropertiesFactory
        extends DocumentLoaderFactoryDecorator<EffectivePom, CanResolveProperties> {
    private final PropertyResolver propertyResolver;

    public CanResolvePropertiesFactory(
            DocumentLoaderFactory<EffectivePom> decorated, PropertyResolver propertyResolver) {
        super(decorated);
        this.propertyResolver = Objects.requireNonNull(propertyResolver);
    }

    @Override
    protected CanResolveProperties decorateDocumentLoader(EffectivePom inner) {
        return new CanResolvePropertiesAdaptor(inner, propertyResolver);
    }
}
