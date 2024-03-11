package com.github.ngeor.maven.document;

import com.github.ngeor.maven.dom.ParentPom;
import com.github.ngeor.yak4jdom.DocumentWrapper;

@FunctionalInterface
public interface Repository {
    DocumentWrapper load(ParentPom parentPom);
}
