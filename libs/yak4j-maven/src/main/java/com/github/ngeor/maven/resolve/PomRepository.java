package com.github.ngeor.maven.resolve;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.FileDocumentLoader;
import com.github.ngeor.maven.document.loader.cache.CachedDocumentDecorator;
import com.github.ngeor.maven.document.loader.cache.CanonicalFile;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.property.resolver.PropertyResolver;
import com.github.ngeor.maven.resolve.input.DefaultLocalRepositoryLocator;
import com.github.ngeor.maven.resolve.input.DefaultParentLoader;
import com.github.ngeor.maven.resolve.input.DefaultParentResolver;
import com.github.ngeor.maven.resolve.input.LocalRepositoryLocator;
import com.github.ngeor.maven.resolve.input.ParentLoader;
import com.github.ngeor.maven.resolve.input.ParentResolver;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PomRepository implements DocumentLoaderFactory, ParentLoader, ParentResolver {
    private final Map<MavenCoordinates, File> coordinatesToFile = new HashMap<>();
    private final Map<CanonicalFile, DocumentWrapper> documentCache = new HashMap<>();
    private final Map<CanonicalFile, DocumentWrapper> propertyCache = new HashMap<>();
    private final Map<CanonicalFile, DocumentLoader> parentResolverCache = new HashMap<>();

    private final DocumentLoaderFactory factory = FileDocumentLoader.asFactory()
            .decorate(factory -> CachedDocumentDecorator.decorateFactory(factory, documentCache))
            .decorate(SanityCheckedInput::decorateFactory)
            .decorate(factory -> (pomFile -> {
                DocumentLoader result = factory.createDocumentLoader(pomFile);
                coordinatesToFile.put(result.coordinates(), pomFile);
                return result;
            }));
    private final LocalRepositoryLocator localRepositoryLocator = new DefaultLocalRepositoryLocator();
    private final ParentLoader parentLoader = new DefaultParentLoader(factory, localRepositoryLocator);
    private final ParentResolver parentResolver = new DefaultParentResolver(parentLoader) {
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

    @Override
    public DocumentLoader createDocumentLoader(File file) {
        return factory.createDocumentLoader(file);
    }

    @Override
    public Optional<DocumentLoader> loadParent(DocumentLoader input) {
        return parentLoader.loadParent(input);
    }

    public DocumentLoader resolveWithParentRecursively(File file) {
        return resolveWithParentRecursively(createDocumentLoader(file));
    }

    @Override
    public DocumentLoader resolveWithParentRecursively(DocumentLoader input) {
        return parentResolver.resolveWithParentRecursively(input);
    }

    public DocumentLoader loadAndResolveProperties(File file) {
        return resolveWithParentRecursively(file)
                .mapDocument(doc ->
                        propertyCache.computeIfAbsent(new CanonicalFile(file), ignored -> doResolveProperties(doc)));
    }

    public Optional<File> findKnownFile(MavenCoordinates coordinates) {
        return Optional.ofNullable(coordinatesToFile.get(Objects.requireNonNull(coordinates)));
    }

    private static DocumentWrapper doResolveProperties(DocumentWrapper document) {
        Map<String, String> unresolvedProperties = DomHelper.getProperties(document);
        if (unresolvedProperties == null || unresolvedProperties.isEmpty()) {
            return document;
        }

        // resolve them
        Map<String, String> resolvedProperties = PropertyResolver.resolve(unresolvedProperties);
        DocumentWrapper result = document.deepClone();
        boolean hadChanges = result.getDocumentElement()
                .transformTextNodes(text -> PropertyResolver.resolve(text, resolvedProperties::get));
        return hadChanges ? result : document;
    }
}
