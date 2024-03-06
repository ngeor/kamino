package com.github.ngeor.mr;

import com.github.ngeor.maven.document.effective.EffectivePomFactory;
import com.github.ngeor.maven.document.loader.FileDocumentLoader;
import com.github.ngeor.maven.document.parent.CanLoadParentFactory;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.function.Function;

@SuppressWarnings("java:S6548") // "enum singleton pattern detected"
public enum EffectivePomLoader implements Function<File, DocumentWrapper> {
    INSTANCE;

    @Override
    public DocumentWrapper apply(File modulePomFile) {
        return FileDocumentLoader.asFactory()
                .decorate(CanLoadParentFactory::new)
                .decorate(EffectivePomFactory::new)
                .createDocumentLoader(modulePomFile)
                .effectivePom();
    }
}
