package com.github.ngeor.mr;

import com.github.ngeor.maven.document.effective.EffectivePomFactory;
import com.github.ngeor.maven.document.loader.FileDocumentLoader;
import com.github.ngeor.maven.document.parent.CanLoadParentFactory;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.function.Function;

public class EffectivePomLoader implements Function<File, DocumentWrapper> {
    @Override
    public DocumentWrapper apply(File modulePomFile) {
        return FileDocumentLoader.asFactory()
                .decorate(CanLoadParentFactory::new)
                .decorate(EffectivePomFactory::new)
                .createDocumentLoader(modulePomFile)
                .effectivePom();
    }
}
