package com.github.ngeor.maven.document.effective;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.yak4jdom.DocumentWrapper;

public interface EffectivePom extends DocumentLoader {
    DocumentWrapper effectivePom();
}
