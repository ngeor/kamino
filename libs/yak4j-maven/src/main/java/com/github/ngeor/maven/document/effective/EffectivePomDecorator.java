package com.github.ngeor.maven.document.effective;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.parent.CanLoadParent;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Optional;

public record EffectivePomDecorator(CanLoadParent canLoadParent, ParentResolver parentResolver) implements EffectivePom {
    @Override
    public DocumentWrapper effectivePom() {
        return parentResolver.resolveWithParentRecursively(this).loadDocument();
    }

    @Override
    public DocumentWrapper loadDocument() {
        return canLoadParent.loadDocument();
    }

    @Override
    public File getPomFile() {
        return canLoadParent.getPomFile();
    }

    @Override
    public Optional<DocumentLoader> loadParent() {
        return canLoadParent.loadParent();
    }
}
