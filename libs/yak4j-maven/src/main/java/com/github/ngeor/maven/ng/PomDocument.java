package com.github.ngeor.maven.ng;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class PomDocument extends BaseDocument {
    private final File pomFile;
    private final FnLazy<EffectiveDocument> lazyEffectiveDocument;

    protected PomDocument(PomDocumentFactory owner, File pomFile) {
        super(owner);
        this.pomFile = Objects.requireNonNull(pomFile);
        this.lazyEffectiveDocument = new FnLazy<>(this::doCreateEffectiveDocument);
    }

    @Override
    protected DocumentWrapper doLoadDocument() {
        return DocumentWrapper.parse(pomFile);
    }

    public Optional<PomDocument> parent() {
        return parentPom().map(p -> {
            String relativePath = Objects.requireNonNullElse(p.relativePath(), "../pom.xml");
            File parentPomFile =
                    pomFile.toPath().getParent().resolve(relativePath).toFile();
            if (parentPomFile.isDirectory()) {
                parentPomFile = new File(parentPomFile, "pom.xml");
            }
            // TODO try local repository
            return getOwner().create(parentPomFile);
        });
    }

    public EffectiveDocument toEffective() {
        return lazyEffectiveDocument.get();
    }

    private EffectiveDocument doCreateEffectiveDocument() {
        PomDocument parentPomDocument = parent().orElse(null);
        return parentPomDocument == null
                ? new EffectiveDocument.Root(this)
                : new EffectiveDocument.Child(parentPomDocument.toEffective(), this);
    }
}
