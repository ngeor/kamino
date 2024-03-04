package com.github.ngeor.maven.document.repository;

import com.github.ngeor.maven.document.effective.CachedMerger;
import com.github.ngeor.maven.document.effective.EffectivePomFactory;
import com.github.ngeor.maven.document.effective.PomMerger;
import com.github.ngeor.maven.document.loader.CachedDocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.FileDocumentLoader;
import com.github.ngeor.maven.document.parent.CanLoadParentFactory;
import com.github.ngeor.maven.document.parent.DefaultLocalRepositoryLocator;
import com.github.ngeor.maven.document.parent.DefaultParentPomFinder;
import com.github.ngeor.maven.document.property.CachedPropertyResolver;
import com.github.ngeor.maven.document.property.CanResolveProperties;
import com.github.ngeor.maven.document.property.CanResolvePropertiesFactory;
import com.github.ngeor.maven.document.property.DefaultPropertyResolver;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

public final class PomRepository implements DocumentLoaderFactory<CanResolveProperties> {
    private final Map<MavenCoordinates, File> coordinatesToFile = new HashMap<>();

    private final DocumentLoaderFactory<CanResolveProperties> factory = FileDocumentLoader.asFactory()
            .decorate(f -> DocumentLoaderVisitorFactory.visitDocument(f, this::sanityCheckDocument))
            .decorate(f -> new DocumentLoaderVisitorFactory(f, this::populateCoordinates))
            .decorate(CachedDocumentLoaderFactory::new)
            .decorate(f -> new CanLoadParentFactory(f, new DefaultParentPomFinder(new DefaultLocalRepositoryLocator())))
            .decorate(f -> new EffectivePomFactory(f, new CachedMerger(new PomMerger())))
            .decorate(
                    f -> new CanResolvePropertiesFactory(f, new CachedPropertyResolver(new DefaultPropertyResolver())));

    @Override
    public CanResolveProperties createDocumentLoader(File file) {
        return factory.createDocumentLoader(file);
    }

    public Optional<File> findLoadedFile(MavenCoordinates coordinates) {
        return Optional.ofNullable(coordinatesToFile.get(Objects.requireNonNull(coordinates)));
    }

    private void sanityCheckDocument(DocumentWrapper document) {
        Objects.requireNonNull(document);
        ElementWrapper element = Objects.requireNonNull(document.getDocumentElement());
        Validate.isTrue(
                ElementNames.PROJECT.equals(element.getNodeName()),
                "Unexpected root element '%s' (expected '%s')",
                element.getNodeName(),
                ElementNames.PROJECT);
    }

    private void populateCoordinates(DocumentLoader documentLoader, DocumentWrapper document) {
        coordinatesToFile.put(DomHelper.coordinates(document), documentLoader.getPomFile());
    }
}
