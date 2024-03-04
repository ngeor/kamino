package com.github.ngeor.maven.document.property;

import com.github.ngeor.maven.document.effective.EffectivePom;
import com.github.ngeor.maven.document.effective.EffectivePomDecorator;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Objects;

final class CanResolvePropertiesAdaptor extends EffectivePomDecorator<EffectivePom> implements CanResolveProperties {
    private final PropertyResolver propertyResolver;

    public CanResolvePropertiesAdaptor(EffectivePom decorated, PropertyResolver propertyResolver) {
        super(decorated);
        this.propertyResolver = Objects.requireNonNull(propertyResolver);
    }

    @Override
    public DocumentWrapper resolveProperties() {
        return propertyResolver.resolveProperties(this);
    }
}
