package com.github.ngeor.maven.resolve;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.resolve.cache.CachedDocumentDecorator;
import com.github.ngeor.maven.resolve.cache.CanonicalFile;
import com.github.ngeor.maven.resolve.input.DefaultLocalRepositoryLocator;
import com.github.ngeor.maven.resolve.input.DefaultParentLoader;
import com.github.ngeor.maven.resolve.input.DefaultParentResolver;
import com.github.ngeor.maven.resolve.input.FileInput;
import com.github.ngeor.maven.resolve.input.Input;
import com.github.ngeor.maven.resolve.input.InputFactory;
import com.github.ngeor.maven.resolve.input.LocalRepositoryLocator;
import com.github.ngeor.maven.resolve.input.ParentLoader;
import com.github.ngeor.maven.resolve.input.ParentResolver;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PomRepository implements InputFactory, ParentLoader, ParentResolver {
    private final Map<MavenCoordinates, File> coordinatesToFile = new HashMap<>();
    private final Map<CanonicalFile, DocumentWrapper> documentCache = new HashMap<>();
    private final Map<CanonicalFile, DocumentWrapper> propertyCache = new HashMap<>();
    private final Map<CanonicalFile, Input> parentResolverCache = new HashMap<>();

    private final InputFactory factory = FileInput.asFactory()
            .decorate(factory -> CachedDocumentDecorator.decorateFactory(factory, documentCache))
            .decorate(SanityCheckedInput::decorateFactory)
            .decorate(factory -> (pomFile -> {
                Input result = factory.load(pomFile);
                coordinatesToFile.put(result.coordinates(), pomFile);
                return result;
            }));
    private final LocalRepositoryLocator localRepositoryLocator = new DefaultLocalRepositoryLocator();
    private final ParentLoader parentLoader = new DefaultParentLoader(factory, localRepositoryLocator);
    private final ParentResolver parentResolver = new DefaultParentResolver(parentLoader) {
        @Override
        public Input resolveWithParentRecursively(Input input) {
            CanonicalFile key = new CanonicalFile(input.pomFile());
            Input result = parentResolverCache.get(key);
            if (result == null) {
                result = super.resolveWithParentRecursively(input);
                parentResolverCache.put(key, result);
            }
            return result;
        }
    };

    @Override
    public Input load(File file) {
        return factory.load(file);
    }

    @Override
    public Optional<Input> loadParent(Input input) {
        return parentLoader.loadParent(input);
    }

    public Input resolveWithParentRecursively(File file) {
        return resolveWithParentRecursively(load(file));
    }

    @Override
    public Input resolveWithParentRecursively(Input input) {
        return parentResolver.resolveWithParentRecursively(input);
    }

    public Input loadAndResolveProperties(File file) {
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
