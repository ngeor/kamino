package com.github.ngeor.maven.resolve.cache;

import com.github.ngeor.maven.resolve.input.Input;
import com.github.ngeor.maven.resolve.input.InputDecorator;
import com.github.ngeor.maven.resolve.input.InputFactory;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Map;
import java.util.Objects;

public class CachedDocumentDecorator extends InputDecorator {
    private final DocumentCache cache;

    public CachedDocumentDecorator(Input decorated, DocumentCache cache) {
        super(decorated);
        this.cache = Objects.requireNonNull(cache);
    }

    @Override
    public DocumentWrapper document() {
        return cache.computeIfAbsent(new CanonicalFile(pomFile()), ignored -> super.document());
    }

    public static Input cache(Input input, DocumentCache cache) {
        return input instanceof CachedDocumentDecorator ? input : new CachedDocumentDecorator(input, cache);
    }

    public static InputFactory decorateFactory(InputFactory factory, DocumentCache cache) {
        return pomFile -> CachedDocumentDecorator.cache(factory.load(pomFile), cache);
    }

    public static InputFactory decorateFactory(InputFactory factory, Map<CanonicalFile, DocumentWrapper> cache) {
        return decorateFactory(factory, cache::computeIfAbsent);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CachedDocumentDecorator other
                && Objects.equals(cache, other.cache)
                && Objects.equals(getDecorated(), other.getDecorated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(cache, getDecorated());
    }
}
