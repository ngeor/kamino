package com.github.ngeor.maven.document;

public interface MergerNg {
    EffectivePomDocument merge(PomDocument root);

    EffectivePomDocument merge(EffectivePomDocument left, PomDocument right);
}
