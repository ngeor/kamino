package com.github.ngeor.maven.resolve.input;

import com.github.ngeor.maven.document.loader.DocumentLoader;

public interface ParentResolver {
    DocumentLoader resolveWithParentRecursively(DocumentLoader input);
}
