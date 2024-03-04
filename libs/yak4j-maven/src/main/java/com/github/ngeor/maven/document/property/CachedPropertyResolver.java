package com.github.ngeor.maven.document.property;

import com.github.ngeor.maven.document.cache.FileCache;
import com.github.ngeor.maven.document.effective.EffectivePom;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Objects;

public final class CachedPropertyResolver implements PropertyResolver {
    private final PropertyResolver decorated;
    private final FileCache<DocumentWrapper> cache = new FileCache<>();

    public CachedPropertyResolver(PropertyResolver decorated) {
        this.decorated = Objects.requireNonNull(decorated);
    }

    @Override
    public DocumentWrapper resolveProperties(EffectivePom effectivePom) {
        return cache.computeIfAbsent(effectivePom, decorated::resolveProperties);
    }
}
