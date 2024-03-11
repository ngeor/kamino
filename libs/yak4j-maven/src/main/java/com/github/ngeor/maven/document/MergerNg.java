package com.github.ngeor.maven.document;

@FunctionalInterface
public interface MergerNg {
    EffectivePomDocument merge(EffectivePomDocument left, PomDocument right);
}
