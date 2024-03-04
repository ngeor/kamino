package com.github.ngeor.maven.document.property;

import com.github.ngeor.maven.document.effective.EffectivePom;
import com.github.ngeor.maven.document.loader.cache.CanonicalFile;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CachedPropertyResolver implements PropertyResolver {
    private final PropertyResolver decorated;
    private final Map<CanonicalFile, DocumentWrapper> cache = new HashMap<>();

    public CachedPropertyResolver(PropertyResolver decorated) {
        this.decorated = Objects.requireNonNull(decorated);
    }

    @Override
    public DocumentWrapper resolveProperties(EffectivePom effectivePom) {
        return cache.computeIfAbsent(
                new CanonicalFile(effectivePom.getPomFile()), key -> decorated.resolveProperties(effectivePom));
    }
}
