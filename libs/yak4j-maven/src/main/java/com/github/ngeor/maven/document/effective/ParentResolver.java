package com.github.ngeor.maven.document.effective;

import com.github.ngeor.maven.document.loader.DocumentLoader;

public interface ParentResolver {
    DocumentLoader resolveWithParentRecursively(DocumentLoader input);
}
