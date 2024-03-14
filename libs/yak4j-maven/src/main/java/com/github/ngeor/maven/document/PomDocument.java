package com.github.ngeor.maven.document;

import com.github.ngeor.maven.dom.ParentPom;
import java.util.Optional;

public abstract class PomDocument extends BasePomDocument {

    public Optional<PomDocument> parent(ParentFinderNg parentFinder) {
        return parentFinder.findParent(this);
    }

    public EffectivePomDocument effectivePom(ParentFinderNg parentFinder, MergerNg merger) {
        ParentPom parentPom = parentPom().orElse(null);
        if (parentPom == null) {
            return new EffectivePomDocument(loadDocument());
        }

        EffectivePomDocument parentDoc = parent(parentFinder).orElseThrow().effectivePom(parentFinder, merger);
        return merger.merge(parentDoc, this);
    }
}
