package com.github.ngeor.mr;

import com.github.ngeor.maven.document.EffectiveDocument;
import com.github.ngeor.maven.document.PomDocumentFactory;
import java.io.File;
import java.util.function.Function;

@SuppressWarnings("java:S6548") // "enum singleton pattern detected"
public enum EffectivePomLoader implements Function<File, EffectiveDocument> {
    INSTANCE;

    @Override
    public EffectiveDocument apply(File modulePomFile) {
        return new PomDocumentFactory().create(modulePomFile).toEffective();
    }
}
