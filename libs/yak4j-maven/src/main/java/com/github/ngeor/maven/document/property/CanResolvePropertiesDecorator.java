package com.github.ngeor.maven.document.property;

import com.github.ngeor.maven.document.effective.EffectivePom;
import com.github.ngeor.maven.document.parent.CanLoadParent;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Optional;

public record CanResolvePropertiesDecorator(EffectivePom decorated) implements CanResolveProperties {
    @Override
    public DocumentWrapper effectivePom() {
        return decorated.effectivePom();
    }

    @Override
    public DocumentWrapper loadDocument() {
        return decorated.loadDocument();
    }

    @Override
    public File getPomFile() {
        return decorated.getPomFile();
    }

    @Override
    public Optional<CanLoadParent> loadParent() {
        return decorated.loadParent();
    }
}
