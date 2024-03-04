package com.github.ngeor.maven.document.repository;

import com.github.ngeor.maven.document.effective.CachedMerger;
import com.github.ngeor.maven.document.effective.EffectivePomDecorator;
import com.github.ngeor.maven.document.effective.PomMerger;
import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.FileDocumentLoader;
import com.github.ngeor.maven.document.loader.cache.CachedDocumentDecorator;
import com.github.ngeor.maven.document.parent.CanLoadParentFactory;
import com.github.ngeor.maven.document.parent.DefaultLocalRepositoryLocator;
import com.github.ngeor.maven.document.parent.DefaultParentPomFinder;
import com.github.ngeor.maven.document.property.CachedPropertyResolver;
import com.github.ngeor.maven.document.property.CanResolveProperties;
import com.github.ngeor.maven.document.property.CanResolvePropertiesDecorator;
import com.github.ngeor.maven.document.property.DefaultPropertyResolver;
import com.github.ngeor.maven.dom.MavenCoordinates;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PomRepository implements DocumentLoaderFactory<CanResolveProperties> {
    private final Map<MavenCoordinates, File> coordinatesToFile = new HashMap<>();

    private final DocumentLoaderFactory<CanResolveProperties> factory = FileDocumentLoader.asFactory()
            .decorate(CachedDocumentDecorator::decorateFactory)
            .decorate(SanityCheckedInput::decorateFactory)
            .decorate(f -> (pomFile -> {
                DocumentLoader result = f.createDocumentLoader(pomFile);
                coordinatesToFile.put(result.coordinates(), pomFile);
                return result;
            }))
            .decorate(f -> new CanLoadParentFactory(f, new DefaultParentPomFinder(new DefaultLocalRepositoryLocator())))
            .decorate(f -> EffectivePomDecorator.decorateFactory(f, new CachedMerger(new PomMerger())))
            .decorate(f -> CanResolvePropertiesDecorator.decorateFactory(
                    f, new CachedPropertyResolver(new DefaultPropertyResolver())));

    @Override
    public CanResolveProperties createDocumentLoader(File file) {
        return factory.createDocumentLoader(file);
    }

    public Optional<File> findKnownFile(MavenCoordinates coordinates) {
        return Optional.ofNullable(coordinatesToFile.get(Objects.requireNonNull(coordinates)));
    }
}
