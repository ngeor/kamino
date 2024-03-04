package com.github.ngeor.maven.document.property;

import com.github.ngeor.maven.document.effective.EffectivePom;
import com.github.ngeor.yak4jdom.DocumentWrapper;

@FunctionalInterface
public interface PropertyResolver {
    DocumentWrapper resolveProperties(EffectivePom effectivePom);
}
