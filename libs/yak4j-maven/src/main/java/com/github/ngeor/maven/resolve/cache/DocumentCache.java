package com.github.ngeor.maven.resolve.cache;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.function.Function;

@FunctionalInterface
public interface DocumentCache {
    DocumentWrapper computeIfAbsent(CanonicalFile key, Function<CanonicalFile, DocumentWrapper> provider);
}
