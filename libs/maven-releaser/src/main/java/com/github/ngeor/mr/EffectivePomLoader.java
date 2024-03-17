package com.github.ngeor.mr;

import com.github.ngeor.maven.document.PomDocumentFactory;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.function.Function;

@SuppressWarnings("java:S6548") // "enum singleton pattern detected"
public enum EffectivePomLoader implements Function<File, DocumentWrapper> {
    INSTANCE;

    @Override
    public DocumentWrapper apply(File modulePomFile) {
        return new PomDocumentFactory().create(modulePomFile).toEffective().loadDocument();
    }
}
