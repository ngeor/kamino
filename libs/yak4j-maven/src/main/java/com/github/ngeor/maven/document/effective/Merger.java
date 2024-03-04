package com.github.ngeor.maven.document.effective;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.yak4jdom.DocumentWrapper;

@FunctionalInterface
public interface Merger {
    DocumentWrapper mergeIntoLeft(DocumentWrapper left, DocumentLoader child);
}
