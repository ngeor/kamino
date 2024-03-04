package com.github.ngeor.maven.document.cache;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FileCache<E> {
    private final Map<CanonicalFile, E> cache = new HashMap<>();

    public E computeIfAbsent(File file, Supplier<E> supplier) {
        return cache.computeIfAbsent(new CanonicalFile(file), ignoredKey -> supplier.get());
    }

    public <D extends DocumentLoader> E computeIfAbsent(D documentLoader, Function<D, E> supplier) {
        return cache.computeIfAbsent(
                new CanonicalFile(documentLoader.getPomFile()), ignoredKey -> supplier.apply(documentLoader));
    }
}
