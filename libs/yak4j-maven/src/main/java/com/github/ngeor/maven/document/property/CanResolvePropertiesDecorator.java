package com.github.ngeor.maven.document.property;

import com.github.ngeor.maven.document.effective.EffectivePom;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.parent.CanLoadParent;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class CanResolvePropertiesDecorator implements CanResolveProperties {
    private final EffectivePom decorated;
    private final PropertyResolver propertyResolver;

    public CanResolvePropertiesDecorator(EffectivePom decorated, PropertyResolver propertyResolver) {
        this.decorated = Objects.requireNonNull(decorated);
        this.propertyResolver = Objects.requireNonNull(propertyResolver);
    }

    @Override
    public DocumentWrapper effectivePom() {
        return decorated.effectivePom();
    }

    @Override
    public DocumentWrapper loadDocument() {
        return decorated.loadDocument();
    }

    @Override
    public File getPomFile() {
        return decorated.getPomFile();
    }

    @Override
    public Optional<CanLoadParent> loadParent() {
        return decorated.loadParent();
    }

    @Override
    public DocumentWrapper resolveProperties() {
        return propertyResolver.resolveProperties(this);
    }

    public static DocumentLoaderFactory<CanResolveProperties> decorateFactory(
            DocumentLoaderFactory<EffectivePom> decorated, PropertyResolver propertyResolver) {
        return pomFile -> new CanResolvePropertiesDecorator(decorated.createDocumentLoader(pomFile), propertyResolver);
    }
}
