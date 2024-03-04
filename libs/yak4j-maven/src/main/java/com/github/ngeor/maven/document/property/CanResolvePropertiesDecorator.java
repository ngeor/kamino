package com.github.ngeor.maven.document.property;

import com.github.ngeor.maven.document.effective.EffectivePomDecorator;
import com.github.ngeor.yak4jdom.DocumentWrapper;

public class CanResolvePropertiesDecorator<E extends CanResolveProperties> extends EffectivePomDecorator<E>
        implements CanResolveProperties {
    public CanResolvePropertiesDecorator(E decorated) {
        super(decorated);
    }

    @Override
    public DocumentWrapper resolveProperties() {
        return getDecorated().resolveProperties();
    }
}
