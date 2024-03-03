package com.github.ngeor.maven.document.repository;

import com.github.ngeor.maven.document.effective.DefaultParentResolver;
import com.github.ngeor.maven.document.effective.ParentResolver;
import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.FileDocumentLoader;
import com.github.ngeor.maven.document.loader.cache.CachedDocumentDecorator;
import com.github.ngeor.maven.document.loader.cache.CanonicalFile;
import com.github.ngeor.maven.document.parent.DefaultLocalRepositoryLocator;
import com.github.ngeor.maven.document.parent.DefaultParentLoader;
import com.github.ngeor.maven.document.parent.LocalRepositoryLocator;
import com.github.ngeor.maven.document.parent.ParentLoader;
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
    private final Map<CanonicalFile, CacheCanResolveProperties> newCache = new HashMap<>();

    private final DocumentLoaderFactory<DocumentLoader> oldFactory = FileDocumentLoader.asFactory()
            .decorate(factory -> CachedDocumentDecorator.decorateFactory(factory, documentCache))
            .decorate(SanityCheckedInput::decorateFactory)
            .decorate(factory -> (pomFile -> {
                DocumentLoader result = factory.createDocumentLoader(pomFile);
                coordinatesToFile.put(result.coordinates(), pomFile);
                return result;
            }));

    private final LocalRepositoryLocator localRepositoryLocator = new DefaultLocalRepositoryLocator();
    private final ParentLoader parentLoader = new DefaultParentLoader(oldFactory, localRepositoryLocator);

    private final ParentResolver parentResolver = new DefaultParentResolver(parentLoader) {
        private final Map<CanonicalFile, DocumentLoader> parentResolverCache = new HashMap<>();

        @Override
        public DocumentLoader resolveWithParentRecursively(DocumentLoader input) {
            CanonicalFile key = new CanonicalFile(input.getPomFile());
            DocumentLoader result = parentResolverCache.get(key);
            if (result == null) {
                result = super.resolveWithParentRecursively(input);
                parentResolverCache.put(key, result);
            }
            return result;
        }
    };

    private final DocumentLoaderFactory<CanResolveProperties> factory = oldFactory
            .decorate(parentLoader::decorateFactory)
            .decorate(parentResolver::decorateFactory)
            .decorate(f -> pomFile -> new CanResolvePropertiesDecorator(f.createDocumentLoader(pomFile)))
            .decorate(f -> pomFile -> newCache.computeIfAbsent(
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
