package com.github.ngeor.maven.document;

import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.ParentPom;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Optional;

public class PomDocument extends BasePomDocument {
    public PomDocument(DocumentWrapper document) {
        super(document);
    }

    public Optional<ParentPom> parentPom() {
        return DomHelper.getParentPom(getDocument());
    }

    public Optional<PomDocument> parent(Repository repository) {
        return parentPom().map(repository::load).map(PomDocument::new);
    }

    public EffectivePomDocument effectivePom(Repository repository, MergerNg merger) {
        ParentPom parentPom = parentPom().orElse(null);
        if (parentPom == null) {
            return new EffectivePomDocument(getDocument());
        }

        EffectivePomDocument parentDoc = parent(repository).orElseThrow().effectivePom(repository, merger);
        return merger.merge(parentDoc, this);
    }
}
