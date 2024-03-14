package com.github.ngeor.maven.document;

import com.github.ngeor.maven.dom.ParentPom;
import java.util.Optional;

public abstract class PomDocument extends BasePomDocument {

    public Optional<PomDocument> parent(Repository repository) {
        return repository.findParent(this);
    }

    public EffectivePomDocument effectivePom(Repository repository, MergerNg merger) {
        ParentPom parentPom = parentPom().orElse(null);
        if (parentPom == null) {
            return new EffectivePomDocument(loadDocument());
        }

        EffectivePomDocument parentDoc = parent(repository).orElseThrow().effectivePom(repository, merger);
        return merger.merge(parentDoc, this);
    }
}
