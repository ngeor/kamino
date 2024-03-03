package com.github.ngeor.maven.document.repository;

import com.github.ngeor.maven.document.effective.EffectivePomDecorator;
import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.FileDocumentLoader;
import com.github.ngeor.maven.document.loader.cache.CachedDocumentDecorator;
import com.github.ngeor.maven.document.loader.cache.CanonicalFile;
import com.github.ngeor.maven.document.parent.CanLoadParentFactory;
import com.github.ngeor.maven.document.parent.DefaultLocalRepositoryLocator;
import com.github.ngeor.maven.document.parent.DefaultParentPomFinder;
import com.github.ngeor.maven.document.property.CanResolveProperties;
import com.github.ngeor.maven.document.property.CanResolvePropertiesDecorator;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PomRepository implements DocumentLoaderFactory<CanResolveProperties> {
    private final Map<MavenCoordinates, File> coordinatesToFile = new HashMap<>();
    private final Map<CanonicalFile, DocumentWrapper> documentCache = new HashMap<>();
    private final Map<CanonicalFile, CacheCanResolveProperties> cache = new HashMap<>();

    private final DocumentLoaderFactory<CanResolveProperties> factory = FileDocumentLoader.asFactory()
            .decorate(factory -> CachedDocumentDecorator.decorateFactory(factory, documentCache))
            .decorate(SanityCheckedInput::decorateFactory)
            .decorate(factory -> (pomFile -> {
                DocumentLoader result = factory.createDocumentLoader(pomFile);
                coordinatesToFile.put(result.coordinates(), pomFile);
                return result;
            }))
            .decorate(factory ->
                    new CanLoadParentFactory(factory, new DefaultParentPomFinder(new DefaultLocalRepositoryLocator())))
            .decorate(EffectivePomDecorator::decorateFactory)
            .decorate(CanResolvePropertiesDecorator::decorateFactory)
            .decorate(f -> pomFile -> cache.computeIfAbsent(
                    new CanonicalFile(pomFile),
                    ignored -> new CacheCanResolveProperties(f.createDocumentLoader(pomFile))));

    @Override
    public CanResolveProperties createDocumentLoader(File file) {
        return factory.createDocumentLoader(file);
    }

    public Optional<File> findKnownFile(MavenCoordinates coordinates) {
        return Optional.ofNullable(coordinatesToFile.get(Objects.requireNonNull(coordinates)));
    }
}
