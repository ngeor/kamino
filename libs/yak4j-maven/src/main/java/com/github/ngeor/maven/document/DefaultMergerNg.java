package com.github.ngeor.maven.document;

import com.github.ngeor.maven.document.effective.PomMerger;

public class DefaultMergerNg implements MergerNg {
    @Override
    public EffectivePomDocument merge(PomDocument root) {
        return new EffectivePomDocument(root.loadDocument());
    }

    @Override
    public EffectivePomDocument merge(EffectivePomDocument left, PomDocument right) {
        return new EffectivePomDocument(
                PomMerger.mergeIntoLeft(left.loadDocument().deepClone(), right.loadDocument()));
    }
}
