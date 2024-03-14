package com.github.ngeor.maven.ng;

import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.ParentPom;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class PomDocument {
    private final PomDocumentFactory owner;
    private final File pomFile;
    private final Supplier<DocumentWrapper> document;
    private final Supplier<Optional<ParentPom>> parentPom;

    public PomDocument(PomDocumentFactory owner, File pomFile) {
        this.owner = Objects.requireNonNull(owner);
        this.pomFile = Objects.requireNonNull(pomFile);
        this.document = new FnLazy<>(() -> DocumentWrapper.parse(pomFile));
        this.parentPom = new FnOptLazy<>(() -> DomHelper.getParentPom(loadDocument()));
    }

    public DocumentWrapper loadDocument() {
        return document.get();
    }

    private Optional<ParentPom> parentPom() {
        return parentPom.get();
    }

    public Optional<PomDocument> parent() {
        return parentPom().map(p -> {
            String relativePath = Objects.requireNonNullElse(p.relativePath(), "../pom.xml");
            return owner.create(pomFile.toPath().getParent().resolve(relativePath));
        });
    }
}
